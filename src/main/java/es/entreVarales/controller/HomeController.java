package es.entreVarales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.entreVarales.model.User;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            switch (user.getRole()) {
                case CAPATAZ:
                    return "redirect:/index";
                case COSTALERO:
                    return "redirect:/costalero/home";
                case ASPIRANTE:
                    return "redirect:/aspirante/bienvenida";
            }
        }

        return "PaginaRedirectFormularioRegistro"; // Página de bienvenida si no está logueado
    }
}

