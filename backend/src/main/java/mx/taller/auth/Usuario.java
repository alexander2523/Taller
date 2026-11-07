package mx.taller.auth;
import jakarta.persistence.*; import mx.taller.common.BaseEntity;
@Entity @Table(name="usuarios") public class Usuario extends BaseEntity {
 @Column(nullable=false,unique=true,length=254) private String email; @Column(nullable=false) private String passwordHash;
 @Enumerated(EnumType.STRING) private Rol rol;
 protected Usuario(){} public Usuario(String email,String passwordHash){this.email=email;this.passwordHash=passwordHash;}
 public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Rol getRol(){return rol;} public void setRol(Rol r){rol=r;}
}
