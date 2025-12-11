package es.uclm.rentplace.controller;

import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.entity.Usuario; // Import Usuario
import es.uclm.rentplace.persistence.usuarioDAO; // Import usuarioDAO
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

    @Autowired
    private usuarioDAO usuarioPersistence;

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

        if (usuarioPersistence.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email ya registrado.");
            return "register-propietario";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setTelefono(telefono);

        usuarioPersistence.save(nuevoUsuario); // Save the new user

        // Create Propietario linked to the new Usuario
        Propietario p = new Propietario(nuevoUsuario);
        propietarioService.crearPropietario(p);
        model.addAttribute("mensaje", "Registro de propietario completado.");
        return "login"; // o página específica
    }
}
