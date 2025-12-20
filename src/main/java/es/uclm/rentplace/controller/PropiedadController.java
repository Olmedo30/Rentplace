// src/main/java/es/uclm/rentplace/controller/PropiedadController.java
package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.PropiedadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private usuarioDAO usuarioDAO;

    // Listado público de todas las propiedades
    @GetMapping("/listado")
    public String listarPropiedades(Model model) {
        List<Propiedad> propiedades = propiedadService.listarPropiedadesActivas();
        model.addAttribute("propiedades", propiedades);
        return "propiedades-list";
    }

    // Mis propiedades (solo para propietarios)
    @GetMapping("/my-properties")
    public String myProperties(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión para ver tus propiedades.");
            return "login";
        }

        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "login";
        }

        // Verificar que el usuario sea propietario
        if (usuario.getRol() != Usuario.Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden acceder a esta página.");
            return "home";
        }

        List<Propiedad> misPropiedades = propiedadService.obtenerPropiedadesDePropietario(userId);
        model.addAttribute("misPropiedades", misPropiedades);
        return "my-properties";
    }
    
    @GetMapping("/{id}")
    public String verPropiedad(@PathVariable Long id, Model model, HttpSession session) {
        Propiedad propiedad = propiedadService.obtenerPropiedadPorId(id);
        if (propiedad == null || !propiedad.getActivo()) {
            model.addAttribute("error", "La propiedad solicitada no está disponible.");
            model.addAttribute("userId", session.getAttribute("userId"));
            return "redirect:/propiedades/listado";
        }

        // Pasar datos a la vista
        model.addAttribute("propiedad", propiedad);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol")); // Para el botón de wishlist

        return "propiedad-detalle";
    }

    // Formulario para añadir nueva propiedad
    @GetMapping("/add-property")
    public String addPropertyForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden añadir propiedades.");
            return "home";
        }

        model.addAttribute("propiedad", new Propiedad());
        return "add-property";
    }

    // Procesar el formulario de añadir propiedad
    @PostMapping("/add-property")
    public String addProperty(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String direccion,
            @RequestParam String ciudad,
            @RequestParam String tipoInmueble,
            @RequestParam Integer habitaciones,
            @RequestParam Integer capacidad,
            @RequestParam BigDecimal precioNoche,
            @RequestParam String politicaCancelacion,
            @RequestParam(required = false) Boolean permiteReservaInmediata,
            @RequestParam(required = false) MultipartFile foto,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden añadir propiedades.");
            return "home";
        }

        try {
        	
        	boolean reservaInmediata = Boolean.TRUE.equals(permiteReservaInmediata);
            Propiedad propiedad = propiedadService.registrarPropiedad(
                usuario, titulo, descripcion, direccion, ciudad, tipoInmueble,
                habitaciones, capacidad, precioNoche, politicaCancelacion, reservaInmediata
            );
            
            // Aquí podrías manejar la subida de fotos si lo implementas
            // Por ahora, solo creamos la propiedad
            
            model.addAttribute("message", "Propiedad registrada exitosamente.");
            return "redirect:/propiedades/my-properties";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar la propiedad: " + e.getMessage());
            model.addAttribute("propiedad", new Propiedad());
            return "add-property";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden editar propiedades.");
            return "home";
        }

        Propiedad propiedad = propiedadService.obtenerPropiedadPorId(id);
        if (propiedad == null) {
            model.addAttribute("error", "La propiedad no existe.");
            return "redirect:/propiedades/my-properties";
        }
     // Verificar que el usuario es el dueño
        if (!propiedad.getPropietario().getId().equals(userId)) {
            model.addAttribute("error", "No tienes permiso para editar esta propiedad.");
            return "redirect:/propiedades/my-properties";
        }

        model.addAttribute("propiedad", propiedad);
        model.addAttribute("esEdicion", true); // ← Flag para distinguir en la plantilla
        return "add-property"; // Reutilizamos la misma plantilla
    }

    // Procesar actualización de propiedad
    @PostMapping("/editar/{id}")
    public String actualizarPropiedad(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String direccion,
            @RequestParam String ciudad,
            @RequestParam String tipoInmueble,
            @RequestParam Integer habitaciones,
            @RequestParam Integer capacidad,
            @RequestParam BigDecimal precioNoche,
            @RequestParam String politicaCancelacion,
            @RequestParam(required = false) Boolean permiteReservaInmediata,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null || usuario.getRol() != Usuario.Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden editar propiedades.");
            return "home";
        }

        Propiedad propiedadExistente = propiedadService.obtenerPropiedadPorId(id);
        if (propiedadExistente == null) {
            model.addAttribute("error", "La propiedad no existe.");
            return "redirect:/propiedades/my-properties";
        }

        if (!propiedadExistente.getPropietario().getId().equals(userId)) {
            model.addAttribute("error", "No tienes permiso para editar esta propiedad.");
            return "redirect:/propiedades/my-properties";
        }
        try {
            // Actualizar los campos
            propiedadExistente.setTitulo(titulo);
            propiedadExistente.setDescripcion(descripcion);
            propiedadExistente.setDireccion(direccion);
            propiedadExistente.setCiudad(ciudad);
            propiedadExistente.setTipoInmueble(tipoInmueble);
            propiedadExistente.setHabitaciones(habitaciones);
            propiedadExistente.setCapacidad(capacidad);
            propiedadExistente.setPrecioNoche(precioNoche);
            propiedadExistente.setPoliticaCancelacion(politicaCancelacion);
            propiedadExistente.setPermiteReservaInmediata(Boolean.TRUE.equals(permiteReservaInmediata));
            
            propiedadService.actualizarPropiedad(propiedadExistente);
            
            model.addAttribute("message", "Propiedad actualizada exitosamente.");
            return "redirect:/propiedades/my-properties";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la propiedad: " + e.getMessage());
            model.addAttribute("propiedad", propiedadExistente);
            model.addAttribute("esEdicion", true);
            return "add-property";
        }
    }
}