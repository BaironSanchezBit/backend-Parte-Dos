/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.models;

import java.util.List;

public class EstadisticasDTO {
    private long numeroDeViajes;
    private double tiempoPromedioPorViaje;
    private double toneladasTransportadas;
    private double distanciaRecorrida;

    // Getters y Setters
    public long getNumeroDeViajes() {
        return numeroDeViajes;
    }

    public void setNumeroDeViajes(long numeroDeViajes) {
        this.numeroDeViajes = numeroDeViajes;
    }

    public double getTiempoPromedioPorViaje() {
        return tiempoPromedioPorViaje;
    }

    public void setTiempoPromedioPorViaje(double tiempoPromedioPorViaje) {
        this.tiempoPromedioPorViaje = tiempoPromedioPorViaje;
    }

    public double getToneladasTransportadas() {
        return toneladasTransportadas;
    }

    public void setToneladasTransportadas(double toneladasTransportadas) {
        this.toneladasTransportadas = toneladasTransportadas;
    }

    public double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public void setDistanciaRecorrida(double distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }
}