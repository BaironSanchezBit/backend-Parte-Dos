/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.models;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @author User
 */
@NoSql(dataFormat = DataFormatType.MAPPED)
@Embeddable
@XmlRootElement
public class Carga {
//    Fecha, propietario de carga, origen, destino, dimensiones, peso, valor asegurado, empaque 
    private String fecha;
    private String id;
    private String propietariocarga;
    private String Origen;
    private String destino;
    private String dimensiones;
    private String peso;
    private String valor;
    private String empaque;
    
    public Carga(){
        
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropietariocarga() {
        return propietariocarga;
    }

    public void setPropietariocarga(String propietariocarga) {
        this.propietariocarga = propietariocarga;
    }

    public String getOrigen() {
        return Origen;
    }

    public void setOrigen(String Origen) {
        this.Origen = Origen;
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

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getEmpaque() {
        return empaque;
    }

    public void setEmpaque(String empaque) {
        this.empaque = empaque;
    }
}
