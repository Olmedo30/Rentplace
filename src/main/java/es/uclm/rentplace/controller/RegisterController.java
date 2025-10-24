package es.uclm.rentplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    // Muestra el formulario de registro
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Carga templates/register.html
    }

    // Procesa el formulario de registro
    @PostMapping("/register")
    public String doRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // 1️⃣ Validaciones básicas
        if (username.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Todos los campos son obligatorios");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }

        // 2️⃣ Aquí podrías guardar el usuario en base de datos
        // (por ahora simulamos que se registra correctamente)
        model.addAttribute("message", "Registro exitoso. ¡Ya puedes iniciar sesión!");

        // 3️⃣ Redirige al login
        return "login";
    }
}
