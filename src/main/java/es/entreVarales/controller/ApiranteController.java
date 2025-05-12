package es.entreVarales.controller;

import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;
import es.entreVarales.model.Aspirantes;
import es.entreVarales.repository.UserRepository;
import es.entreVarales.service.AspiranteService;
import es.entreVarales.service.PasoService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;





@Controller
@RequestMapping("/aspirantes")
public class ApiranteController {

    @Autowired
    private AspiranteService aspiranteService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasoService pasoService;
    
    
    @GetMapping("/inicio")
    public String aspiranteInicio() {
        return "indexAspirante";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("aspirantes", aspiranteService.findAll());
        return "listaAspirantes";
    }

//    @GetMapping("/mostrarFormularioCrear")
//    public String mostrarFormularioCrear(Model model) {
//        model.addAttribute("costalero", new Costalero());
//        model.addAttribute("pasos", pasoService.findAll()); // Obtiene los pasos
//        Costalero costalero = new Costalero();
//        return "formularioCrearCostalero"; // Cambiado a formularioCrearCostalero.html
//    }
    
    @GetMapping("/mostrarFormularioCrear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("aspirante", new Aspirantes());
        model.addAttribute("pasos", pasoService.findAll()); // Obtiene los pasos
        List<User> usuariosAspirantes = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ASPIRANTE)
                .toList();

        model.addAttribute("usuarios", userRepository.findByRole(User.Role.ASPIRANTE));
 // Agrega los usuarios

        return "formularioCrearAspirante";
    }


    @PostMapping("/crear")
    public String crear(@ModelAttribute Aspirantes aspirante, Model model) {
    	aspiranteService.save(aspirante);
        model.addAttribute("success", "Aspirante creado correctamente.");
        return "redirect:/aspirantes";
    }
    
    
   

    @GetMapping("/editar")
    public String mostrarFormularioEditar(@RequestParam("dni") String dni, Model model) {
        Aspirantes aspirante = aspiranteService.findById(dni).orElse(null);
        if (aspirante == null) {
            model.addAttribute("error", "Aspirante no encontrado.");
            return "redirect:/aspirantes";
        }
        model.addAttribute("aspirante", aspirante);
        model.addAttribute("pasos", pasoService.findAll());
        model.addAttribute("usuarios", userRepository.findAll()); // <- esto falta
        return "formularioEditarAspirante";
    }


    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Aspirantes aspirante, Model model) {
    	aspiranteService.update(aspirante.getDniAspirante(), aspirante);
        model.addAttribute("success", "Aspirante actualizado.");
        return "redirect:/aspirantes";
    }

//    @GetMapping("/eliminar")
//    public String eliminar(@RequestParam("dni") String dni, Model model) {
//        costaleroService.deleteById(dni);
//        model.addAttribute("success", "Costalero eliminado.");
//        return "redirect:/costaleros";
//    }
    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("dni") String dni, Model model) {
    	aspiranteService.deleteById(dni);
        model.addAttribute("success", "Aspirante eliminado.");
        return "redirect:/aspirantes";
    }


    @GetMapping("/mostrarFormularioBusqueda")
    public String mostrarFormularioBusqueda() {
        return "buscarAspirante";
    }

    @GetMapping("/buscar")
    public String buscarPorDni(@RequestParam("dni") String dni, Model model) {
        Aspirantes aspirante = aspiranteService.findById(dni).orElse(null);
        if (aspirante == null) {
            model.addAttribute("error", "Aspirante no encontrado.");
        } else {
            model.addAttribute("aspirante", aspirante);
        }
        return "verAspirante";
    }
    
    @GetMapping("/vista")
    public String aspiranteInicio(Model model, @RequestParam("dni") String dni) {
        Aspirantes aspirante = aspiranteService.findById(dni).orElse(null);
        model.addAttribute("aspirante", aspirante);
        return "VistaAspirante";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.ASPIRANTE) {
            return "redirect:/login?unauthorized";
        }

        return "aspirantes/dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }


}
