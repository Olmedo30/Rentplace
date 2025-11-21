package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.PropietarioDAO;
import es.uclm.rentplace.service.PropiedadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {
	
	    private static final Logger log = LoggerFactory.getLogger(PropiedadController.class);

	    @Autowired
	    private PropiedadService propiedadService;

	    @Autowired
	    private PropietarioDAO propietarioDAO;

	    // Mostramos el formulario 
	    @GetMapping("/nueva")
	    public String nuevaPropiedadForm(Model model) {
	        return "add-propiedad";
	    }

	    // Procesamos el POST del formulario
	    @PostMapping
	    public String crearPropiedad(
	            @RequestParam String titulo,
	            @RequestParam(required = false) String ciudad,
	            @RequestParam(required = false) String tipoInmueble,
	            @RequestParam BigDecimal precioNoche,
	            @RequestParam(required = false) Long propietarioId, // id de Propietario opcional
	            HttpSession session,
	            Model model
	    ) {
	        // Intentamos resolver Propietario desde propietarioId (por admin) o desde sesión usuario -> propietario
	        Propietario propietario = null;

	        if (propietarioId != null) {
	            propietario = propietarioDAO.findById(propietarioId).orElse(null);
	        }

	        if (propietario == null) {
	            Long sessionUserId = (Long) session.getAttribute("userId");
	            if (sessionUserId != null) {
	                propietario = propietarioDAO.findByUsuarioId(sessionUserId).orElse(null);
	            }
	        }

	        if (propietario == null) {
	            model.addAttribute("error", "No se encontró el propietario (ni propietarioId ni sesión).");
	            return "add-propiedad";
	        }

	        boolean ok = propiedadService.registrarPropiedad(titulo, ciudad, tipoInmueble, precioNoche, propietario);

	        if (!ok) {
	            model.addAttribute("error", "Datos inválidos o no se pudo crear la propiedad.");
	            return "add-propiedad";
	        }

	        log.info("Propiedad creada por propietarioId={} titulo={}", propietario.getId(), titulo);
	        return "redirect:/propiedades/listado";
	    }
	}

