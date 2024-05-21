/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Conductor;
import com.example.models.ConductorDTO;
import com.example.models.PropietarioCarga;
import com.example.models.PropietarioCargaDTO;

import com.example.models.Vehiculo;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
@Path("/conductor")
@Produces(MediaType.APPLICATION_JSON)
public class ConductorService {

    @PersistenceContext(unitName = "mongoPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Conductor
    @GET
    @Path("/obtener")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Conductor u order by u.nombre ASC");
        List<Conductor> conductor = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(conductor).build();
    }

    @PUT
    @Path("/actualizar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConductor(ConductorDTO conductorActualizado) {
        EntityTransaction transaction = null;
        try {
            System.out.println(conductorActualizado.getId());
            Conductor conductor = entityManager.find(Conductor.class, conductorActualizado.getId());
            if (conductor != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                // Actualizar los campos del conductor
                conductor.setCorreo(conductorActualizado.getCorreo());
                conductor.setDireccion(conductorActualizado.getDireccion());
                conductor.setNombre(conductorActualizado.getNombre());
                conductor.setTelefono(conductorActualizado.getTelefono());
                conductor.setContraseña(conductorActualizado.getContraseña());
                conductor.setVehiculo(conductorActualizado.getVehiculo());
                // Persistir los cambios con merge
                entityManager.merge(conductor);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("PropietarioCarga actualizado correctamente")
                        .build();

            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("No se encontró el PropietarioCarga")
                        .build();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al actualizar el PropietarioCarga")
                    .build();
        }
    }

    @POST
    @Path("/eliminar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarConductor(ConductorDTO condutorEliminado) {
        EntityTransaction transaction = null;
        try {
            Conductor temp = entityManager.find(Conductor.class, condutorEliminado.getId());
            if (temp != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(temp);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("Conductor eliminado correctamente")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("No se encontró el conductor")
                        .build();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al eliminar el conductor")
                    .build();
        }
    }

    @POST
    @Path("/agregar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(ConductorDTO conductor) {
        Conductor c = new Conductor();
        JSONObject rta = new JSONObject();
        c.setCorreo(conductor.getCorreo());
        c.setDireccion(conductor.getDireccion());
        c.setNombre(conductor.getNombre());
        c.setTelefono(conductor.getTelefono());
        c.setContraseña(conductor.getContraseña());
        c.setVehiculo(conductor.getVehiculo());
        conductor.setId(c.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("competitor_id", c.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            c = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin",
                "*").entity(rta.toJSONString()).build();
    }
    
//    public String obteneridvehiculo(Vehicle vehicle) {
//        Query q = entityManager.createQuery("select u from Vehiculo u order by u.placa ASC");
//        List<Vehiculo> vehiculo = q.getResultList();
//        for (Vehiculo temp : vehiculo) {
//            if (vehicle.getMarca().equals(temp.getMarca())) {  
//                return temp.getId();
//            }
//        }
//        return null;
//
//    }
    
    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }

}
