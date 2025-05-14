package es.entreVarales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import es.entreVarales.model.User;
import es.entreVarales.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpSession session) {
        User loggedInUser = userService.loginUser(user.getUsername(), user.getPassword());

        if (loggedInUser != null) {
            session.setAttribute("user", loggedInUser);

            // Redirección basada en rol
            switch (loggedInUser.getRole()) {
                case CAPATAZ:
                    return "redirect:/capataces/index";
                case COSTALERO:
                    return "redirect:/costaleros/dashboard";
                case ASPIRANTE:
                    return "redirect:/aspirantes/dashboard";
            }
        }

        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Invalida la sesión
        return "redirect:/";    // Redirige al home después de hacer logout
    }

}
