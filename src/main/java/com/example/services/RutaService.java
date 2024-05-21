package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Ruta;
import com.example.models.RutaDTO;
import com.example.models.SolicitudCarga;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/rutas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RutaService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/obtener")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerRutas() {
        Query query = entityManager.createQuery("SELECT r FROM Ruta r ORDER BY r.origen ASC");
        List<Ruta> rutas = query.getResultList();
        return Response.ok(rutas).build();
    }

    @POST
    @Path("/crearRuta")
    public Response crearRuta(RutaDTO rutaDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Ruta ruta = new Ruta();

            ruta.setOrigen(rutaDTO.getOrigen());
            ruta.setDestino(rutaDTO.getDestino());
            ruta.setDescripcion(rutaDTO.getDescripcion());

            entityManager.persist(ruta);
            transaction.commit();

            return Response.status(Response.Status.CREATED).entity(ruta.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();  // Añadir esta línea para obtener más información del error.
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al crear la ruta").build();
        }
    }

    @POST
    @Path("/definirRuta")
    public Response definirRuta(RutaDTO rutaDTO, @QueryParam("solicitudId") String solicitudId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            SolicitudCarga solicitud = entityManager.find(SolicitudCarga.class, solicitudId);
            if (solicitud == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud no encontrada").build();
            }
            transaction.begin();

            Ruta ruta = new Ruta();
            ruta.setOrigen(rutaDTO.getOrigen());
            ruta.setDestino(rutaDTO.getDestino());
            ruta.setDescripcion(rutaDTO.getDescripcion());

            entityManager.persist(ruta);

            solicitud.setRuta(ruta);
            entityManager.merge(solicitud);

            transaction.commit();

            // Notificar al conductor
            notificarConductor(solicitud);

            return Response.status(Response.Status.CREATED).entity(ruta.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al definir la ruta").build();
        }
    }

    private void notificarConductor(SolicitudCarga solicitudCarga) {
        // LÃ³gica de notificaciÃ³n al conductor (correo electrÃ³nico, SMS, etc.)
    }

    @PUT
    @Path("/{id}")
    public Response actualizarRuta(@PathParam("id") String id, Ruta rutaActualizada) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Ruta ruta = entityManager.find(Ruta.class, id);
            if (ruta == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            ruta.setDescripcion(rutaActualizada.getDescripcion());
            entityManager.merge(ruta);
            transaction.commit();
            return Response.ok().build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al actualizar la ruta").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarRuta(@PathParam("id") String id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Ruta ruta = entityManager.find(Ruta.class, id);
            if (ruta == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            entityManager.remove(ruta);
            transaction.commit();
            return Response.ok().build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al eliminar la ruta").build();
        }
    }
}
