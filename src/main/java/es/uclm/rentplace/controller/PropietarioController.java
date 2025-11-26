package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.service.PropietarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/propietarios")
public class PropietarioController {

    @Autowired
    private PropietarioService propietarioService;

    // Mostrar formulario de registro de propietario
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("propietario", new Propietario());
        return "register-propietario";
    }

    // Procesar registro
    @PostMapping("/register")
    public String register(@RequestParam String nombre,
                           @RequestParam String email,
                           @RequestParam String telefono,
                           Model model) {

        if (propietarioService.existsByEmail(email)) {
            model.addAttribute("error", "Email ya registrado como propietario.");
            return "register-propietario";
        }

        Propietario p = new Propietario(nombre, email, telefono);
        propietarioService.crearPropietario(p);
        model.addAttribute("mensaje", "Registro de propietario completado.");
        return "login"; // o página específica
    }
}
