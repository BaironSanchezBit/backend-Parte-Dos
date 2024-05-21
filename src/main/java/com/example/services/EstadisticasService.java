/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Remision;
import com.example.models.EstadisticasDTO;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/estadisticas")
@Produces(MediaType.APPLICATION_JSON)
public class EstadisticasService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/consolidar")
    public Response consolidarEstadisticas() {
        Query query = entityManager.createQuery("SELECT r FROM Remision r");
        List<Remision> remisiones = query.getResultList();

        // Calcular estadÃ­sticas
        EstadisticasDTO estadisticasDTO = new EstadisticasDTO();
        estadisticasDTO.setNumeroDeViajes(remisiones.size());

        double tiempoTotal = 0;
        double toneladasTotal = 0;
        double distanciaTotal = 0;

        for (Remision remision : remisiones) {
            tiempoTotal += remision.getDuracion();
            toneladasTotal += remision.getPeso();
            distanciaTotal += remision.getDistancia();
        }

        if (!remisiones.isEmpty()) {
            estadisticasDTO.setTiempoPromedioPorViaje(tiempoTotal / remisiones.size());
        }
        estadisticasDTO.setToneladasTransportadas(toneladasTotal);
        estadisticasDTO.setDistanciaRecorrida(distanciaTotal);

        return Response.ok(estadisticasDTO).build();
    }
}
