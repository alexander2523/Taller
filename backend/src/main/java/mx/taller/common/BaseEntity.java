package mx.taller.common;
import jakarta.persistence.*; import java.util.UUID;
@MappedSuperclass
public abstract class BaseEntity {
 @Id @Column(columnDefinition="BINARY(16)", updatable=false, nullable=false) private UUID id;
 @Column(nullable=false) private boolean activo=true;
 @PrePersist protected void generarId(){ if(id==null) id=UuidV7.create(); }
 public UUID getId(){return id;} public boolean isActivo(){return activo;} public void setActivo(boolean v){activo=v;}
}
