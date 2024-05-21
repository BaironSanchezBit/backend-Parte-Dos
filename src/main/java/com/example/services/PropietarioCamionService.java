/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Conductor;
import com.example.models.PropietarioCarga;
import com.example.models.PropietarioCargaDTO;

import com.example.models.PropietarioCamion;
import com.example.models.PropietarioCamionDTO;
import com.example.models.Ruta;
import com.example.models.RutaDTO;
import com.example.models.SolicitudCarga;

import com.example.models.Vehiculo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
@Path("/Propietariocamion")
@Produces(MediaType.APPLICATION_JSON)
public class PropietarioCamionService {

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
        Query q = entityManager.createQuery("select u from PropietarioCamion u order by u.nombre ASC");
        List<PropietarioCarga> conductor = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(conductor).build();
    }

    @PUT
    @Path("/actualizar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConductor(PropietarioCamionDTO propietarioactualizado) {
        EntityTransaction transaction = null;
        try {
            System.out.println(propietarioactualizado.getId());
            PropietarioCamion propietario = entityManager.find(PropietarioCamion.class, propietarioactualizado.getId());
            if (propietario != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                // Actualizar los campos del conductor
                propietario.setCorreo(propietarioactualizado.getCorreo());
                propietario.setDireccion(propietarioactualizado.getDireccion());
                propietario.setNombre(propietarioactualizado.getNombre());
                propietario.setTelefono(propietarioactualizado.getTelefono());
                propietario.setContraseña(propietarioactualizado.getContraseña());
                propietario.setVehiculo(propietarioactualizado.getVehiculo());
                List<String> temp = propietario.getConductores();
                temp.add(propietarioactualizado.getConductor().getId());
                propietario.setConductores(temp);
                // Persistir los cambios con merge
                entityManager.merge(propietario);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("Conductor actualizado correctamente")
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al actualizar el conductor")
                    .build();
        }
    }

    @POST
    @Path("/eliminar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarConductor(PropietarioCamionDTO PropietarioEliminado) {
        EntityTransaction transaction = null;
        try {
            PropietarioCamion temp = entityManager.find(PropietarioCamion.class, PropietarioEliminado.getId());
            if (temp != null) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(temp);
                transaction.commit();

                return Response.status(Response.Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("Propietario eliminado correctamente")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity("No se encontró el Propietario")
                        .build();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity("Error al eliminar el Propietario")
                    .build();
        }
    }

    @POST
    @Path("/elegirconductor")
    public Response elegirConductor(@QueryParam("propietarioId") String propietarioId, @QueryParam("conductorId") String conductorId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            PropietarioCamion propietario = entityManager.find(PropietarioCamion.class, propietarioId);
            Conductor conductor = entityManager.find(Conductor.class, conductorId);

            if (propietario == null || conductor == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Conductor o propietario no encontrado").build();
            }

            transaction.begin();
            propietario.setConductorSeleccionado(conductor.getId());
            entityManager.merge(propietario);
            transaction.commit();

            return Response.status(Response.Status.OK).entity("Conductor seleccionado correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al seleccionar el conductor").build();
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
            solicitud.setEstado("RUTA DEFINIDA");
            ruta.setOrigen(rutaDTO.getOrigen());
            ruta.setDestino(rutaDTO.getDestino());
            ruta.setDescripcion(rutaDTO.getDescripcion());

            entityManager.persist(ruta);

            solicitud.setRuta(ruta);
            entityManager.merge(solicitud);

            transaction.commit();

            return Response.status(Response.Status.CREATED).entity(ruta.getId()).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al definir la ruta").build();
        }
    }

    @GET
    @Path("/verificar")
    public Response Aplicado(@QueryParam("propietarioId") String propietarioId) {
        if (propietarioId == null || propietarioId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("El ID del propietario no puede ser nulo o vacío").build();
        }

        // Consulta para obtener las solicitudes de carga pendientes
        Query query = entityManager.createQuery("SELECT s FROM SolicitudCarga s WHERE s.estado = 'Pendiente' ORDER BY s.fecha DESC");
        List<SolicitudCarga> solicitudes = query.getResultList();
        List<SolicitudCarga> solicitudes2 = new ArrayList<>();

        // Comparación de IDs
        for (SolicitudCarga temp : solicitudes) {
            if (temp.getPropietarioSeleccionado() != null && propietarioId.equals(temp.getPropietarioSeleccionado().getId())) {
                solicitudes2.add(temp);
            }
        }

        if (solicitudes2.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron solicitudes para el propietario especificado").build();
        }

        return Response.ok(solicitudes2).build();
    }

    @PUT
    @Path("/asignarPropietario")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response asignarPropietario(@QueryParam("solicitudId") String solicitudId, @QueryParam("propietarioId") String propietarioId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            SolicitudCarga solicitud = entityManager.find(SolicitudCarga.class, solicitudId);
            PropietarioCamion propietario = entityManager.find(PropietarioCamion.class, propietarioId);

            if (solicitud == null || propietario == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud o propietario no encontrado").build();
            }

            transaction.begin();
            solicitud.setPropietarioSeleccionado(propietario);
            entityManager.merge(solicitud);
            transaction.commit();
            solicitud.setEstado("Asignado");

            return Response.status(Response.Status.OK).entity("Propietario asignado correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al asignar el propietario").build();
        }
    }

    @POST
    @Path("/aplicar")
    public Response aplicarPropietario(@QueryParam("solicitudId") String solicitudId, @QueryParam("propietarioId") String propietarioId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            SolicitudCarga solicitud = entityManager.find(SolicitudCarga.class, solicitudId);
            PropietarioCamion propietario = entityManager.find(PropietarioCamion.class, propietarioId);

            if (solicitud == null || propietario == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Solicitud o propietario no encontrado").build();
            }

            transaction.begin();

            solicitud.getAplicaciones().add(propietario);
            solicitud.setEstado("Aplicado");

            entityManager.merge(solicitud);
            transaction.commit();

            return Response.status(Response.Status.OK).entity("Aplicación realizada correctamente").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al aplicar el propietario").build();
        }
    }

    @POST
    @Path("/agregar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(PropietarioCamionDTO propietariocamion) {
        PropietarioCamion c = new PropietarioCamion();
        JSONObject rta = new JSONObject();
        c.setCorreo(propietariocamion.getCorreo());
        c.setDireccion(propietariocamion.getDireccion());
        c.setNombre(propietariocamion.getNombre());
        c.setTelefono(propietariocamion.getTelefono());
        c.setContraseña(propietariocamion.getContraseña());
        c.setVehiculo(propietariocamion.getVehiculo());

        if (propietariocamion.getConductor() != null) {
            Conductor conductor = entityManager.find(Conductor.class, propietariocamion.getConductor().getId());
            if (conductor == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Conductor no encontrado").build();
            }
            List<String> temp = c.getConductores();
            temp.add(conductor.getId());
            c.setConductores(temp);
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("id_propietario", c.getId());
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
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

// ejemplo    
//{
// "nombre": "Ana Gomez",
// "correo":"ana@gomez.com",
// "telefono": "7659675",
//"direccion": "Calle 42 #18",
// "contraseña": "ola",
// "vehicle":{
// "marca":"chevrolet",
//    "placa":"ABC-123",
//    "tipocarroceria":"camion"
//      "modelo":"chevroletx"
// }
// } 
    @GET
    @Path("/solicitudesDisponibles")
    public Response obtenerSolicitudesDisponibles() {
        Query query = entityManager.createQuery("SELECT s FROM SolicitudCarga s WHERE s.estado = 'Pendiente' ORDER BY s.fecha DESC");
        List<SolicitudCarga> solicitudes = query.getResultList();

        return Response.ok(solicitudes).build();
    }

    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }

}
