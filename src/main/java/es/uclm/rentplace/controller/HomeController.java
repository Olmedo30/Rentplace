package es.uclm.rentplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@GetMapping("/")
	public String redirectToHome() {
	    return "redirect:/home";
	}

	@GetMapping("/home")
	public String home(HttpSession session, Model model) {
	    String username = (String) session.getAttribute("username");
	    model.addAttribute("username", username); // puede ser null
	    return "home"; // siempre muestra home.html
	}
}