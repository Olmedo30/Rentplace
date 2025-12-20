package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.InquilinoDAO;
import es.uclm.rentplace.persistence.PropietarioDAO;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.service.PropiedadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {

    private static final Logger log = LoggerFactory.getLogger(PropiedadController.class);

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PropietarioDAO propietarioDAO;

    @Autowired
    private usuarioDAO usuarioPersistence;

    @Autowired
    private InquilinoDAO inquilinoDAO;

    // Mostrar formulario
    @GetMapping("/nueva")
    public String nuevaPropiedadForm(Model model) {
        return "add-propiedad";
    }

    @PostMapping("/add-property")
    @ResponseBody
    public ResponseEntity<?> addProperty(@RequestBody Propiedad propiedad, HttpSession session) {
        // 1) Obtener userId de la sesión
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>("Debes iniciar sesión para añadir propiedades.", HttpStatus.UNAUTHORIZED);
        }

        // 2) Obtener el Usuario logado
        Optional<Usuario> usuarioOpt = usuarioPersistence.findById(userId);
        if (usuarioOpt.isEmpty()) {
            session.invalidate();
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
        Usuario usuario = usuarioOpt.get();

        // 3) Encontrar o crear el Propietario asociado al Usuario
        Propietario propietario = propietarioDAO.findByUsuarioId(userId).orElseGet(() -> {
            Propietario newPropietario = new Propietario(usuario);
            return propietarioDAO.save(newPropietario);
        });

        // Guardar la propiedad usando el servicio
        boolean ok = propiedadService.registrarPropiedad(propiedad, propietario);

        if (!ok) {
            return new ResponseEntity<>("Datos inválidos o no se pudo crear la propiedad.", HttpStatus.BAD_REQUEST);
        }

        log.info("Propiedad creada por propietarioId={} titulo={}", propietario.getId(), propiedad.getTitulo());
        return new ResponseEntity<>(propiedad, HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public String crearPropiedad(
            @RequestParam String titulo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String tipoInmueble,
            @RequestParam(defaultValue = "1") int capacidad,
            @RequestParam BigDecimal precioNoche,
            @RequestParam(name = "politica_cancelacion", required = false) String politicaCancelacion,
            @RequestParam(name = "permite_reserva_inmediata", defaultValue = "false") boolean permiteReservaInmediata,
            HttpSession session,
            Model model
    ) {
        // 1) Obtener userId de la sesión
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión para añadir propiedades.");
            return "login";
        }

        // 2) Obtener el Usuario logado
        Optional<Usuario> usuarioOpt = usuarioPersistence.findById(userId);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            session.invalidate(); // Invalidar sesión si el usuario no existe
            return "login";
        }
        Usuario usuario = usuarioOpt.get();

        // 3) Encontrar o crear el Propietario asociado al Usuario
        Propietario propietario = propietarioDAO.findByUsuarioId(userId).orElseGet(() -> {
            Propietario newPropietario = new Propietario(usuario);
            return propietarioDAO.save(newPropietario);
        });

        // Guardamos la propiedad usando el servicio
        boolean ok = propiedadService.registrarPropiedad(titulo, descripcion, direccion, ciudad, tipoInmueble, capacidad, precioNoche, politicaCancelacion, permiteReservaInmediata, propietario);

        if (!ok) {
            model.addAttribute("error", "Datos inválidos o no se pudo crear la propiedad.");
            return "add-propiedad";
        }

        log.info("Propiedad creada por propietarioId={} titulo={}", propietario.getId(), titulo);
        return "redirect:/propiedades/listado";
    }

    @GetMapping("/listado")
    public String listarPropiedades(Model model, HttpSession session) {
        List<Propiedad> propiedades = propiedadService.listarActivas();
        model.addAttribute("propiedades", propiedades);
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("is_inquilino", inquilinoDAO.existsByUsuarioId(userId));
        } else {
            model.addAttribute("is_inquilino", false);
        }
        return "propiedades-list";
    }

    @GetMapping("/buscar")
    public String buscarPropiedades(@RequestParam(value = "ciudad", required = false) String ciudad, Model model, HttpSession session) {
        List<Propiedad> propiedades;
        if (ciudad != null && !ciudad.trim().isEmpty()) {
            propiedades = propiedadService.buscarPropiedadesPorCiudad(ciudad);
            model.addAttribute("ciudadBusqueda", ciudad); // Para mantener el valor en el input de búsqueda
        } else {
            propiedades = propiedadService.listarActivas();
        }
        model.addAttribute("propiedades", propiedades);
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("is_inquilino", inquilinoDAO.existsByUsuarioId(userId));
        } else {
            model.addAttribute("is_inquilino", false);
        }
        return "propiedades-list";
    }

    @GetMapping("/my-properties")
    public String myProperties(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "Debes iniciar sesión para ver tus propiedades.");
            return "login"; 
        }

        Optional<Usuario> usuarioOpt = usuarioPersistence.findById(userId);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado.");
            session.invalidate(); 
            return "login";
        }
        Usuario usuario = usuarioOpt.get();

        Optional<Propietario> propietarioOpt = propietarioDAO.findByUsuarioId(userId);
        if (propietarioOpt.isEmpty()) {
            model.addAttribute("error", "No eres un propietario registrado.");
            return "redirect:/home"; 
        }
        Propietario propietario = propietarioOpt.get();

        List<Propiedad> misPropiedades = propiedadService.obtenerPropiedadesDePropietario(propietario);
        model.addAttribute("misPropiedades", misPropiedades);
        if (userId != null) {
            model.addAttribute("is_inquilino", inquilinoDAO.existsByUsuarioId(userId));
        } else {
            model.addAttribute("is_inquilino", false);
        }
        return "my-properties";
    }
}