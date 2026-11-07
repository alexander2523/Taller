package mx.taller.auth;
import jakarta.validation.constraints.*;
public record RegistroUsuarioDto(@NotBlank @Email @Pattern(regexp="^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") String email,@NotBlank @Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{10,72}$") String password){}
