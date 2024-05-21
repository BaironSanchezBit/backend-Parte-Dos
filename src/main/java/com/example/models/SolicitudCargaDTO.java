package com.example.models;

import java.util.Calendar;

public class SolicitudCargaDTO {

    private String id; // Agrega este campo
    private String origen;
    private String destino;
    private String dimensiones;
    private Double peso;
    private Double valorAsegurado;
    private String empaque;
    private Calendar fecha;
    private PropietarioCargaDTO propietarioCarga;

    // Getters y Setters
    public String getId() { // Agrega este método
        return id;
    }

    public void setId(String id) { // Agrega este método
        this.id = id;
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

    public Calendar getFecha() {
        return fecha;
    }

    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    public PropietarioCargaDTO getPropietarioCarga() {
        return propietarioCarga;
    }

    public void setPropietarioCarga(PropietarioCargaDTO propietarioCarga) {
        this.propietarioCarga = propietarioCarga;
    }
}