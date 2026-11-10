package mx.taller.auth;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
 private final UsuarioRepository repo;
 private final PasswordEncoder encoder;
 private final PasswordResetService passwordReset;
 private final boolean exposeResetToken;

 public AuthController(UsuarioRepository repo, PasswordEncoder encoder, PasswordResetService passwordReset,
         @org.springframework.beans.factory.annotation.Value("${app.password-reset.expose-token:false}") boolean exposeResetToken) {
  this.repo=repo; this.encoder=encoder; this.passwordReset=passwordReset; this.exposeResetToken=exposeResetToken;
 }

 @PostMapping("/registro")
 public Map<String,Object> registro(@Valid @RequestBody RegistroUsuarioDto d){
  if(repo.existsByEmailIgnoreCase(d.email()))throw new IllegalArgumentException("El correo ya está registrado");
  Usuario u=repo.save(new Usuario(d.email().toLowerCase(Locale.ROOT),encoder.encode(d.password())));
  return Map.of("id",u.getId(),"mensaje","Registro recibido; un administrador debe asignar un rol");
 }

 @PostMapping("/login")
 public Map<String,Object> login(@Valid @RequestBody LoginDto d){
  Usuario u=repo.findByEmailAndActivoTrue(d.email().toLowerCase(Locale.ROOT)).orElseThrow(()->new IllegalArgumentException("Credenciales inválidas"));
  if(!encoder.matches(d.password(),u.getPasswordHash()))throw new IllegalArgumentException("Credenciales inválidas");
  return Map.of("email",u.getEmail(),"rol",u.getRol()==null?"PENDIENTE":u.getRol().name());
 }

 @PostMapping("/password-reset/request")
 public Map<String,String> solicitarRecuperacion(@Valid @RequestBody EmailDto d) {
  var token=passwordReset.requestReset(d.email());
  Map<String,String> respuesta=new java.util.LinkedHashMap<>();
  respuesta.put("mensaje","Si existe una cuenta activa con ese correo, podrás continuar con la recuperación.");
  if(exposeResetToken) token.ifPresent(value->respuesta.put("token",value));
  return respuesta;
 }

 @PostMapping("/password-reset/confirm")
 public Map<String,String> confirmarRecuperacion(@Valid @RequestBody ResetPasswordDto d) {
  passwordReset.resetPassword(d.token(),d.password());
  return Map.of("mensaje","La contraseña se actualizó. Ya puedes iniciar sesión.");
 }

 public record LoginDto(@NotBlank @Email @Pattern(regexp="^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") String email,@NotBlank @Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{10,72}$") String password){}
 public record EmailDto(@NotBlank @Email @Pattern(regexp="^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") String email){}
 public record ResetPasswordDto(@NotBlank @Pattern(regexp="^[A-Za-z0-9_-]{40,50}$") String token,@NotBlank @Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{10,72}$") String password){}
}
