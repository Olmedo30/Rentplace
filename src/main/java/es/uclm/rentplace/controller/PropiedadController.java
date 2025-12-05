package es.uclm.rentplace.controller;

import es.uclm.rentplace.service.DisponibilidadService;
import es.uclm.rentplace.entity.ListaDeseos;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.ListaDeseosService;
import es.uclm.rentplace.service.PropiedadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {
	
	private final PropiedadService propiedadService;
    private final usuarioDAO usuarioDAO;
    private final DisponibilidadService disponibilidadService;
    
    @Autowired
    public PropiedadController(PropiedadService propiedadService, usuarioDAO usuarioDAO, DisponibilidadService disponibilidadService) {
    	this.propiedadService = propiedadService;
    	this.usuarioDAO = usuarioDAO;
    	this.disponibilidadService = disponibilidadService;
    }
    
    @Autowired
    private ListaDeseosService ListaDeseosService;
    
    // Mostrar formulario para crear propiedad
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("propiedad", new Propiedad());
        return "crear-propiedad";
    }
    
    // Procesar creación de propiedad
    @PostMapping("/crear")
    public String crearPropiedad(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String direccion,
            @RequestParam String ciudad,
            @RequestParam String tipoInmueble,
            @RequestParam Integer habitaciones,
            @RequestParam Integer capacidad,
            @RequestParam BigDecimal precioNoche,
            @RequestParam String politicaCancelacion,
            @RequestParam Boolean permiteReservaInmediata,
            HttpSession session,
            Model model) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }
        
        // Verificar que es propietario
        if (usuario.getRol() != Usuario.Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden crear propiedades.");
            return "redirect:/home";
        }
        
        Propiedad propiedad = propiedadService.registrarPropiedad(
            usuario, titulo, descripcion, direccion, ciudad, tipoInmueble,
            habitaciones, capacidad, precioNoche, politicaCancelacion, permiteReservaInmediata
        );
        
        model.addAttribute("message", "Propiedad registrada exitosamente.");
        return "redirect:/mis-propiedades";
    }
    
    // Listar propiedades del propietario
    @GetMapping("/mis-propiedades")
    public String listarMisPropiedades(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Usuario usuario = usuarioDAO.findById(userId).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Propiedad> propiedades = propiedadService.obtenerPropiedadesDePropietario(userId);
        model.addAttribute("propiedades", propiedades);
        return "mis-propiedades";
    }
    
    // Activar/Desactivar propiedad
    @PostMapping("/toggle-activo")
    public String toggleActivo(@RequestParam Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Propiedad propiedad = propiedadService.obtenerPorId(id);
        if (propiedad == null) {
            model.addAttribute("error", "Propiedad no encontrada.");
            return "redirect:/mis-propiedades";
        }
        
        // Verificar que el usuario es el propietario
        if (!propiedad.getPropietario().getId().equals(userId)) {
            model.addAttribute("error", "No tienes permisos para modificar esta propiedad.");
            return "redirect:/mis-propiedades";
        }
        
        if (propiedad.getActivo()) {
            propiedadService.desactivarPropiedad(id);
            model.addAttribute("message", "Propiedad desactivada.");
        } else {
            propiedadService.activarPropiedad(id);
            model.addAttribute("message", "Propiedad activada.");
        }
        
        return "redirect:/mis-propiedades";
    }
    
    // Búsqueda básica de propiedades
    @GetMapping("/buscar")
    public String buscarPropiedades(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String tipoInmueble,
            Model model) {
        
        List<Propiedad> propiedades;
        if (ciudad != null && !ciudad.isEmpty()) {
            propiedades = propiedadService.buscarPorCiudad(ciudad);
        } else if (tipoInmueble != null && !tipoInmueble.isEmpty()) {
            propiedades = propiedadService.buscarPorTipo(tipoInmueble);
        } else {
            propiedades = propiedadService.listarPropiedadesActivas();
        }
        
        model.addAttribute("propiedades", propiedades);
        return "resultados-busqueda";
    }
    
    // Búsqueda avanzada
    @GetMapping("/buscar-avanzado")
    public String mostrarBusquedaAvanzada() {
        return "busqueda-avanzada";
    }
    
    @GetMapping("/buscar-disponibles")
    public String buscarPropiedadesDisponibles(
            @RequestParam String ciudad,
            @RequestParam LocalDateTime fechaEntrada,
            @RequestParam LocalDateTime fechaSalida,
            Model model) {
        
        List<Propiedad> propiedades = disponibilidadService.buscarPropiedadesDisponibles(
            ciudad, null, null, fechaEntrada, fechaSalida
        );
        
        model.addAttribute("propiedades", propiedades);
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        return "resultados-busqueda";
    }
    
    @PostMapping("/buscar-avanzado")
    public String buscarAvanzado(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String tipoInmueble,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Boolean reservaInmediata,
            Model model) {
        
        List<Propiedad> propiedades = propiedadService.buscarAvanzado(
            ciudad, tipoInmueble, precioMax, reservaInmediata
        );
        
        model.addAttribute("propiedades", propiedades);
        return "resultados-busqueda";
    }
    
    // Ver detalle de propiedad
    @GetMapping("/{id}")
    public String verDetallePropiedad(@PathVariable Long id, Model model, HttpSession session) {
        Propiedad propiedad = propiedadService.obtenerPorId(id);
        if (propiedad == null || !propiedad.getActivo()) {
            model.addAttribute("error", "Propiedad no encontrada o no disponible.");
            return "error";
        }
        
        model.addAttribute("propiedad", propiedad);
        
        // Verificar si el usuario ya tiene esta propiedad en su lista de deseos
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            ListaDeseos lista = ListaDeseosService.obtenerListaDeUsuario(userId);
            boolean enLista = lista != null && lista.getPropiedades().stream()
                    .anyMatch(p -> p.getId().equals(id));
            model.addAttribute("enListaDeseos", enLista);
        }
        
        return "detalle-propiedad";
    }
}