package com.example.services;

import com.example.PersistenceManager;
import com.example.models.PropietarioCamion;
import com.example.models.PropietarioCarga;
import com.example.models.SolicitudCarga;
import com.example.models.SolicitudCargaDTO;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import org.json.simple.JSONObject;

@Path("/solicitudesCarga")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitudCargaService {

    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    @GET
    @Path("/obtener")
    public Response obtenerSolicitudes() {
        Query query = entityManager.createQuery("SELECT s FROM SolicitudCarga s ORDER BY s.fecha DESC");
        List<SolicitudCarga> solicitudes = query.getResultList();
        return Response.ok(solicitudes).build();
    }

    @POST
    @Path("/agregar")
    public Response agregarSolicitud(SolicitudCargaDTO solicitudCargaDTO) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            SolicitudCarga solicitud = new SolicitudCarga();

            solicitud.setFecha(solicitudCargaDTO.getFecha());
            solicitud.setOrigen(solicitudCargaDTO.getOrigen());
            solicitud.setDestino(solicitudCargaDTO.getDestino());
            solicitud.setDimensiones(solicitudCargaDTO.getDimensiones());
            solicitud.setPeso(solicitudCargaDTO.getPeso());
            solicitud.setValorAsegurado(solicitudCargaDTO.getValorAsegurado());
            solicitud.setEmpaque(solicitudCargaDTO.getEmpaque());
            solicitud.setEstado("Pendiente"); // Estado inicial

            // Asignar propietario de carga existente
            if (solicitudCargaDTO.getPropietarioCarga() != null) {
                PropietarioCarga propietarioCarga = entityManager.find(PropietarioCarga.class, solicitudCargaDTO.getPropietarioCarga().getId());
                if (propietarioCarga == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("Propietario de carga no encontrado").build();
                }
                solicitud.setPropietarioCarga(propietarioCarga);
            }

            entityManager.persist(solicitud);
            transaction.commit();

            return Response.status(Response.Status.CREATED).entity(solicitud.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al agregar la solicitud").build();
        }
    }

    @POST
    @Path("/seleccionarPropietario")
    public Response seleccionarPropietario(@QueryParam("solicitudId") String solicitudId, @QueryParam("propietarioId") String propietarioId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            SolicitudCarga solicitud = entityManager.find(SolicitudCarga.class, solicitudId);
            PropietarioCamion propietario = entityManager.find(PropietarioCamion.class, propietarioId);
            if (solicitud == null || propietario == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud o propietario no encontrado").build();
            }
            transaction.begin();
            solicitud.setPropietarioSeleccionado(propietario);
            solicitud.setEstado("Asignado");
            entityManager.merge(solicitud);
            transaction.commit();

            // Notificar al propietario seleccionado
            notificarPropietarioSeleccionado(solicitud);

            return Response.status(Response.Status.OK).entity("Propietario seleccionado correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al seleccionar propietario").build();
        }
    }

    private void notificarPropietarioSeleccionado(SolicitudCarga solicitudCarga) {
        PropietarioCamion propietario = solicitudCarga.getPropietarioSeleccionado();
        // SimulaciÃ³n de notificaciÃ³n (en un entorno real, esto serÃ­a un correo electrÃ³nico, SMS, etc.)
        System.out.println("NotificaciÃ³n enviada al propietario seleccionado:");
        System.out.println("Nombre: " + propietario.getNombre());
        System.out.println("Correo: " + propietario.getCorreo());
        System.out.println("TelÃ©fono: " + propietario.getTelefono());
        System.out.println("Solicitud ID: " + solicitudCarga.getId());
        System.out.println("Origen: " + solicitudCarga.getOrigen());
        System.out.println("Destino: " + solicitudCarga.getDestino());
    }

    private void notificarPropietarios(SolicitudCarga solicitudCarga) {
        Query query = entityManager.createQuery("SELECT p FROM PropietarioCamion p WHERE p.vehiculo.capacidad >= :peso");
        query.setParameter("peso", solicitudCarga.getPeso());
        List<PropietarioCamion> propietarios = query.getResultList();
        // Enviar notificaciones a los propietarios encontrados
        for (PropietarioCamion propietario : propietarios) {
            // SimulaciÃ³n de notificaciÃ³n (en un entorno real, esto serÃ­a un correo electrÃ³nico, SMS, etc.)
            System.out.println("NotificaciÃ³n enviada al propietario:");
            System.out.println("Nombre: " + propietario.getNombre());
            System.out.println("Correo: " + propietario.getCorreo());
            System.out.println("TelÃ©fono: " + propietario.getTelefono());
            System.out.println("Solicitud ID: " + solicitudCarga.getId());
            System.out.println("Origen: " + solicitudCarga.getOrigen());
            System.out.println("Destino: " + solicitudCarga.getDestino());
            System.out.println("Peso: " + solicitudCarga.getPeso());
        }
    }

    @PUT
    @Path("/actualizarSolicitud")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarSolicitud(SolicitudCarga solicitudActualizada) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            SolicitudCarga solicitud = entityManager.find(SolicitudCarga.class, solicitudActualizada.getId());
            if (solicitud == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud no encontrada").build();
            }

            transaction.begin();
            solicitud.setPropietarioSeleccionado(solicitudActualizada.getPropietarioSeleccionado());
            entityManager.merge(solicitud);
            transaction.commit();

            return Response.status(Response.Status.OK).entity("Solicitud actualizada correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al actualizar la solicitud").build();
        }
    }

    @DELETE
    @Path("/eliminar/{id}")
    public Response eliminarSolicitud(@PathParam("id") String id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            SolicitudCarga solicitud = entityManager.find(SolicitudCarga.class, id);
            if (solicitud == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud no encontrada").build();
            }
            transaction.begin();
            entityManager.remove(solicitud);
            transaction.commit();
            return Response.ok().entity("Solicitud eliminada correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al eliminar la solicitud").build();
        }
    }

    @OPTIONS
    @Path("{path : .*}")
    public Response options() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }
}
