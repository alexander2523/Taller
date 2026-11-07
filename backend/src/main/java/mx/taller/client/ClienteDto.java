package mx.taller.client;
import jakarta.validation.constraints.*;
public record ClienteDto(@NotBlank @Pattern(regexp="^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{2,100}$") String nombre,@NotBlank @Pattern(regexp="^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{2,100}$") String apellido,@NotBlank @Email @Pattern(regexp="^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") String email,@NotBlank @Pattern(regexp="^\\+?[0-9]{10,15}$") String telefono){}
