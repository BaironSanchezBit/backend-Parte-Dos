package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Remision;
import com.example.models.RemisionDTO;
import com.example.models.Ruta;
import com.example.models.SolicitudCarga;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/remisiones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RemisionService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/obtener")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerRutas() {
        Query query = entityManager.createQuery("SELECT r FROM Remision r");
        List<Remision> remisiones = query.getResultList();
        return Response.ok(remisiones).build();
    }

    @POST
    @Path("/agregar")
    public Response crearRemision(RemisionDTO remisionDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Remision remision = new Remision();

            remision.setFechaHoraRecogida(remisionDTO.getFechaHoraRecogida());
            remision.setOrigen(remisionDTO.getOrigen());
            remision.setDestino(remisionDTO.getDestino());
            remision.setPlacaCamion(remisionDTO.getPlacaCamion());
            remision.setConductor(remisionDTO.getConductor());

            // Buscar la ruta correspondiente usando el ID proporcionado
            Ruta ruta = entityManager.find(Ruta.class, remisionDTO.getRuta().getId());
            if (ruta == null) {
                // Si la ruta no existe, devuelve un error
                return Response.status(Response.Status.BAD_REQUEST).entity("La ruta proporcionada no existe").build();
            }
            remision.setRuta(ruta);

            // Buscar la solicitud de carga correspondiente usando el ID proporcionado
            SolicitudCarga solicitudCarga = entityManager.find(SolicitudCarga.class, remisionDTO.getSolicitudCarga().getId());
            if (solicitudCarga == null) {
                // Si la solicitud de carga no existe, devuelve un error
                return Response.status(Response.Status.BAD_REQUEST).entity("La solicitud de carga proporcionada no existe").build();
            }
            remision.setSolicitudCarga(solicitudCarga);

            remision.setEstado("En tránsito"); // Estado inicial
            entityManager.persist(remision);

            // Actualizar la solicitud de carga con la remisión
            solicitudCarga.setRemision(remision);
            solicitudCarga.setEstado("EN_TRANSITO");
            entityManager.merge(solicitudCarga);

            transaction.commit();
            return Response.status(Response.Status.CREATED).entity(remision.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al crear la remisión").build();
        }
    }

    @PUT
    @Path("/reportarEstado/{id}")
    public Response reportarEstado(@PathParam("id") String id, @QueryParam("estado") String estado) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Remision remision = entityManager.find(Remision.class, id);
            if (remision == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            transaction.begin();
            remision.setEstado(estado);

            // Si el estado es "Entregado", también actualiza la solicitud de carga
            if ("Entregado".equals(estado)) {
                SolicitudCarga solicitudCarga = remision.getSolicitudCarga();
                solicitudCarga.setEstado("ENTREGADA");
                entityManager.merge(solicitudCarga);
            }

            entityManager.merge(remision);
            transaction.commit();
            return Response.ok().build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al reportar el estado").build();
        }
    }

    @PUT
    @Path("/cerrarRemision/{id}")
    public Response cerrarRemision(@PathParam("id") String id, @QueryParam("valoracion") String valoracion) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Remision remision = entityManager.find(Remision.class, id);
            if (remision == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            transaction.begin();
            remision.setValoracion(valoracion);
            remision.setEstado("Cerrado");
            entityManager.merge(remision);

            // Actualizar el estado de la solicitud de carga a "CERRADO"
            SolicitudCarga solicitudCarga = remision.getSolicitudCarga();
            solicitudCarga.setEstado("CERRADO");
            entityManager.merge(solicitudCarga);

            transaction.commit();
            return Response.ok().build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al cerrar la remisión").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarRemision(@PathParam("id") String id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Remision remision = entityManager.find(Remision.class, id);
            if (remision == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            transaction.begin();
            entityManager.remove(remision);
            transaction.commit();
            return Response.ok().build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al eliminar la remisiÃ³n").build();
        }
    }
}
