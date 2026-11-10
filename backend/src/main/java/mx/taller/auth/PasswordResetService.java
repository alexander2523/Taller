package mx.taller.auth;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

@Service
public class PasswordResetService {
    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UsuarioRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final ObjectProvider<JavaMailSender> mailSenders;
    private final String frontendUrl;
    private final String fromAddress;
    private final boolean exposeToken;

    public PasswordResetService(UsuarioRepository users, PasswordResetTokenRepository tokens,
            PasswordEncoder encoder, ObjectProvider<JavaMailSender> mailSenders,
            @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl,
            @Value("${app.mail.from:${spring.mail.username:}}") String fromAddress,
            @Value("${app.password-reset.expose-token:false}") boolean exposeToken) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.mailSenders = mailSenders;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.fromAddress = fromAddress;
        this.exposeToken = exposeToken;
    }

    @Transactional
    public Optional<String> requestReset(String email) {
        JavaMailSender mailSender = exposeToken ? null : mailSenders.getIfAvailable();
        if (!exposeToken && (mailSender == null || fromAddress.isBlank())) {
            throw new IllegalStateException("La recuperación de contraseña no está configurada. Configura el correo SMTP del servidor.");
        }
        return users.findByEmailAndActivoTrue(email.trim().toLowerCase(Locale.ROOT)).map(user -> {
            tokens.findByUsuarioIdAndUsedAtIsNull(user.getId()).forEach(token -> token.markUsed(Instant.now()));
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            tokens.save(new PasswordResetToken(user, hash(rawToken), Instant.now().plusSeconds(30 * 60)));

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(user.getEmail());
            message.setSubject("Recupera tu contraseña | Gestión de Taller");
            message.setText("Recibimos una solicitud para cambiar la contraseña de tu cuenta.\n\n"
                    + "Abre este enlace en los próximos 30 minutos para elegir una contraseña nueva:\n"
                    + frontendUrl + "/?resetToken=" + rawToken
                    + "\n\nSi no solicitaste este cambio, puedes ignorar este correo.");
            if (exposeToken) return rawToken;
            try {
                mailSender.send(message);
            } catch (RuntimeException e) {
                log.warn("No se pudo enviar un correo de recuperación", e);
            }
            return null;
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = tokens.findByTokenHash(hash(rawToken))
                .filter(t -> t.getUsedAt() == null && t.getExpiresAt().isAfter(Instant.now()) && t.getUsuario().isActivo())
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido o ya venció"));
        Usuario user = token.getUsuario();
        user.setPasswordHash(encoder.encode(newPassword));
        users.save(user);
        token.markUsed(Instant.now());
        tokens.save(token);
        tokens.findByUsuarioIdAndUsedAtIsNull(user.getId()).forEach(t -> t.markUsed(Instant.now()));
    }

    private static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo procesar el enlace de recuperación", e);
        }
    }
}
