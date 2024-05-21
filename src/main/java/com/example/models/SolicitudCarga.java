package com.example.models;

import com.sun.istack.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

@NoSql(dataFormat = DataFormatType.MAPPED)
@Entity
@XmlRootElement
public class SolicitudCarga implements Serializable {

    private String origen;
    private String destino;
    private String dimensiones;
    private Double peso;
    private Double valorAsegurado;
    private String empaque;
    private String estado; // "Pendiente", "Asignado", "Cancelado"

    @ManyToOne
    private PropietarioCamion propietarioSeleccionado;

    private Ruta ruta;
    
    @OneToOne(cascade = CascadeType.ALL)
    private Remision remision;
    
    @Id
    @GeneratedValue(generator = "UUID")
    @Field(name = "_id")
    private String id;

    @NotNull
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt;

    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt;

    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fecha;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private PropietarioCarga propietarioCarga;

    @OneToMany
    private List<PropietarioCamion> aplicaciones = new ArrayList<>();

    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = Calendar.getInstance();
    }

    @PrePersist
    private void creationTimestamp() {
        this.createdAt = this.updatedAt = Calendar.getInstance();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getFecha() {
        return fecha;
    }

    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    public PropietarioCarga getPropietarioCarga() {
        return propietarioCarga;
    }

    public void setPropietarioCarga(PropietarioCarga propietarioCarga) {
        this.propietarioCarga = propietarioCarga;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getValorAsegurado() {
        return valorAsegurado;
    }

    public void setValorAsegurado(Double valorAsegurado) {
        this.valorAsegurado = valorAsegurado;
    }

    public String getEmpaque() {
        return empaque;
    }

    public void setEmpaque(String empaque) {
        this.empaque = empaque;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public PropietarioCamion getPropietarioSeleccionado() {
        return propietarioSeleccionado;
    }

    public void setPropietarioSeleccionado(PropietarioCamion propietarioSeleccionado) {
        this.propietarioSeleccionado = propietarioSeleccionado;
    }

    public List<PropietarioCamion> getAplicaciones() {
        return aplicaciones;
    }

    public void setAplicaciones(List<PropietarioCamion> aplicaciones) {
        this.aplicaciones = aplicaciones;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Remision getRemision() {
        return remision;
    }

    public void setRemision(Remision remision) {
        this.remision = remision;
    }
}
