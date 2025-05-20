package es.entreVarales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.entreVarales.model.User;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

//    @GetMapping("/")
//    public String home(HttpSession session) {
//        User user = (User) session.getAttribute("user");
//
//        if (user != null) {
//        	model.addAttribute("username", user.getUsername());  // Pasa el nombre del usuario al modelo
//            switch (user.getRole()) {
//                case CAPATAZ:
//                    return "redirect:/capataces/index";
//                case COSTALERO:
//                    return "redirect:/costalero/home";
//                case ASPIRANTE:
//                    return "redirect:/aspirante/bienvenida";
//            }
//        }
//
//        return "PaginaRedirectFormularioRegistro"; // Página de bienvenida si no está logueado
//    }
	
	@GetMapping("/")
	public String home(HttpSession session, Model model) {
	    User user = (User) session.getAttribute("user");

	    if (user != null) {
	        model.addAttribute("username", user.getUsername());  // Pasa el nombre del usuario al modelo
	        switch (user.getRole()) {
	            case CAPATAZ:
	                return "redirect:/capataces/index";
	            case COSTALERO:
	                return "redirect:/costalero/home";
	            case ASPIRANTE:
	                return "redirect:/aspirantes/vista";
	        }
	    }

	    return "PaginaRedirectFormularioRegistro"; // Página de bienvenida si no está logueado
	}

}

