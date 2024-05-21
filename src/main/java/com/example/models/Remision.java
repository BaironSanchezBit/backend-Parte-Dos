package com.example.models;

import com.sun.istack.NotNull;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
@XmlRootElement
public class Remision implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @Field(name = "_id")
    private String id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fechaHoraRecogida;

    private String origen;
    private String destino;
    private String placaCamion;
    private String conductor;
    private String estado; // "En tránsito", "Entregado", "Cerrado"
    private String valoracion; // "Excelente", "Buena", "Regular", "Mala"

    @ManyToOne
    private Ruta ruta;

    @ManyToOne
    private SolicitudCarga solicitudCarga;

    private double peso; // Peso de la carga
    private double distancia; // Distancia del viaje
    private Calendar fechaHoraEntrega; // Fecha y hora de entrega

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getFechaHoraRecogida() {
        return fechaHoraRecogida;
    }

    public void setFechaHoraRecogida(Calendar fechaHoraRecogida) {
        this.fechaHoraRecogida = fechaHoraRecogida;
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

    public String getPlacaCamion() {
        return placaCamion;
    }

    public void setPlacaCamion(String placaCamion) {
        this.placaCamion = placaCamion;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public SolicitudCarga getSolicitudCarga() {
        return solicitudCarga;
    }

    public void setSolicitudCarga(SolicitudCarga solicitudCarga) {
        this.solicitudCarga = solicitudCarga;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public Calendar getFechaHoraEntrega() {
        return fechaHoraEntrega;
    }

    public void setFechaHoraEntrega(Calendar fechaHoraEntrega) {
        this.fechaHoraEntrega = fechaHoraEntrega;
    }

    // Método para calcular la duración del viaje en horas
    public double getDuracion() {
        long diffInMillis = fechaHoraEntrega.getTimeInMillis() - fechaHoraRecogida.getTimeInMillis();
        return (double) diffInMillis / (1000 * 60 * 60);
    }
}
