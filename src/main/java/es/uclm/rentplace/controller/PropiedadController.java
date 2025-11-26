package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Propietario;
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
import java.util.Optional;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {

    private static final Logger log = LoggerFactory.getLogger(PropiedadController.class);

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PropietarioDAO propietarioDAO;

    // Mostrar formulario
    @GetMapping("/nueva")
    public String nuevaPropiedadForm(Model model) {
        return "add-propiedad";
    }

    // Procesar el POST del formulario
    @PostMapping
    public String crearPropiedad(
            @RequestParam String titulo,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String tipoInmueble,
            @RequestParam BigDecimal precioNoche,
            @RequestParam(required = false) Long propietarioId, // id de Propietario opcional (admin)
            HttpSession session,
            Model model
    ) {
        // 1) Intentar obtener propietarioId desde la sesión (flujo propietario logado)
        Propietario propietario = null;

        // Preferir propietarioId indicado por el formulario (útil para admin/debug)
        if (propietarioId != null) {
            propietario = propietarioDAO.findById(propietarioId).orElse(null);
        }

        // Si no vino por formulario, intentar obtenerlo desde sesión:
        if (propietario == null) {
            Object sessionPropId = session.getAttribute("propietarioId");
            if (sessionPropId instanceof Number) {
                long pid = ((Number) sessionPropId).longValue();
                propietario = propietarioDAO.findById(pid).orElse(null);
            } else if (sessionPropId instanceof String) {
                try {
                    long pid = Long.parseLong((String) sessionPropId);
                    propietario = propietarioDAO.findById(pid).orElse(null);
                } catch (NumberFormatException ignored) { /* no válido */ }
            }
        }

        // Si no se pudo resolver el propietario, mostramos error en el formulario
        if (propietario == null) {
            model.addAttribute("error", "No se encontró el propietario (ni propietarioId ni sesión). " +
                    "Asegúrate de iniciar sesión como propietario o indicar propietarioId.");
            return "add-propiedad";
        }

        // Guardamos la propiedad usando el servicio (devuelve boolean según tu implementación)
        boolean ok = propiedadService.registrarPropiedad(titulo, ciudad, tipoInmueble, precioNoche, propietario);

        if (!ok) {
            model.addAttribute("error", "Datos inválidos o no se pudo crear la propiedad.");
            return "add-propiedad";
        }

        log.info("Propiedad creada por propietarioId={} titulo={}", propietario.getId(), titulo);
        return "redirect:/propiedades/listado";
    }
}
