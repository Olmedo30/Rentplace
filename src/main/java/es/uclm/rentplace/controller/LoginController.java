package es.uclm.rentplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@GetMapping("/")
	public String root() {
	    return "redirect:/login";
	}

    @GetMapping("/login")
    public String login() {
        return "login"; // muestra login.html
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, Model model) {
        // Aquí iría la lógica real de autenticación (por ahora, ejemplo simple)
    	if ("admin".equals(username) && "1234".equals(password)) {
    	    return "home";
    	} else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login"; // login fallido → vuelve al login con mensaje
        }
    }
}