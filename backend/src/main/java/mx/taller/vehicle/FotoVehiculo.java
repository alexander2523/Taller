package mx.taller.vehicle;
import jakarta.persistence.*; import mx.taller.common.BaseEntity;
@Entity @Table(name="fotos_vehiculo") public class FotoVehiculo extends BaseEntity { @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="vehiculo_id",nullable=false) private Vehiculo vehiculo; @Column(nullable=false,length=500) private String ruta; @Column(nullable=false,length=80) private String tipo; protected FotoVehiculo(){} public FotoVehiculo(Vehiculo v,String r,String t){vehiculo=v;ruta=r;tipo=t;} public String getRuta(){return ruta;} public String getTipo(){return tipo;} }
