package mx.taller.client;
import jakarta.persistence.*; import mx.taller.common.BaseEntity; import mx.taller.workshop.Taller;
@Entity @Table(name="clientes") public class Cliente extends BaseEntity {
 @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="taller_id",nullable=false) private Taller taller;
 @Column(nullable=false,length=100) private String nombre; @Column(nullable=false,length=100) private String apellido; @Column(nullable=false,unique=true,length=254) private String email; @Column(nullable=false,unique=true,length=20) private String telefono;
 protected Cliente(){} public Cliente(Taller taller,String n,String a,String e,String t){this.taller=taller;actualizar(n,a,e,t);} public void actualizar(String n,String a,String e,String t){nombre=n;apellido=a;email=e.toLowerCase();telefono=t;} public Taller getTaller(){return taller;} public String getNombre(){return nombre;}public String getApellido(){return apellido;}public String getEmail(){return email;}public String getTelefono(){return telefono;}
}
