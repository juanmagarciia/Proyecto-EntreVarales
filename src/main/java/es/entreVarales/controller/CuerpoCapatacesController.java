package es.entreVarales.controller;

import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;
import es.entreVarales.model.Aspirantes;
import es.entreVarales.model.CuerpoCapataces;
import es.entreVarales.repository.UserRepository;
import es.entreVarales.service.CuerpoCapatacesService;
import es.entreVarales.service.PasoService;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/capataces")
public class CuerpoCapatacesController {

    @Autowired
    private CuerpoCapatacesService capatazService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasoService pasoService;

    @GetMapping("/inicio")
    public String capatazInicio() {
        return "indexCapataz";
    }
    
    @GetMapping("/index")
    public String capatazIndex() {
        return "index";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("capataces", capatazService.findAll());
        return "listaCapataces";
    }

    @GetMapping("/mostrarFormularioCrear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("capataz", new CuerpoCapataces());
        model.addAttribute("pasos", pasoService.findAll());

//        List<User> usuariosCapataz = userRepository.findAll().stream()
//                .filter(u -> u.getRole() == User.Role.CAPATAZ)
//                .toList();
//
//        model.addAttribute("usuarios", userRepository.findByRole(User.Role.CAPATAZ));
        
     // 1. Obtener todos los usuarios con rol ASPIRANTE
        List<User> usuariosCapataces = userRepository.findByRole(User.Role.CAPATAZ);

        // 2. Obtener usuarios que ya están asignados a un aspirante
        List<User> usuariosYaAsignados = capatazService.findAll().stream()
                .map(CuerpoCapataces::getUser)
                .collect(Collectors.toList());

        // 3. Filtrar los usuarios que aún no han sido asignados
        List<User> usuariosDisponibles = usuariosCapataces.stream()
                .filter(user -> !usuariosYaAsignados.contains(user))
                .collect(Collectors.toList());

        model.addAttribute("usuarios", usuariosDisponibles); // solo los no asignado

        return "formularioCrearCapataz";
    }

//    @PostMapping("/crear")
//    public String crear(@ModelAttribute CuerpoCapataces capataz, Model model) {
//        capatazService.save(capataz);
//        model.addAttribute("success", "Capataz creado correctamente.");
//        return "redirect:/capataces";
//    }
    
    @PostMapping("/crear")
    public String crear(@ModelAttribute CuerpoCapataces capataz, Model model) {
        
        // Comprobar si ya existe un capataz con ese DNI
        if (capatazService.findById(capataz.getDniCapataz()).isPresent()) {
            model.addAttribute("error", "Ya existe un capataz con ese DNI.");
            
            // 💡 IMPORTANTE: Añadir el capataz de nuevo para que Thymeleaf no falle
            model.addAttribute("capataz", capataz);

            // Volver a cargar los datos necesarios para el formulario
            model.addAttribute("pasos", pasoService.findAll());

            List<User> usuariosCapataces = userRepository.findByRole(User.Role.CAPATAZ);
            List<User> usuariosYaAsignados = capatazService.findAll().stream()
                    .map(CuerpoCapataces::getUser)
                    .collect(Collectors.toList());
            List<User> usuariosDisponibles = usuariosCapataces.stream()
                    .filter(user -> !usuariosYaAsignados.contains(user))
                    .collect(Collectors.toList());

            model.addAttribute("usuarios", usuariosDisponibles);

            return "formularioCrearCapataz";
        }
        
        capatazService.save(capataz);
        model.addAttribute("success", "Capataz creado correctamente.");
        return "redirect:/capataces";
    }


    @GetMapping("/editar")
    public String mostrarFormularioEditar(@RequestParam("dni") String dni, Model model) {
        CuerpoCapataces capataz = capatazService.findById(dni).orElse(null);
        if (capataz == null) {
            model.addAttribute("error", "Capataz no encontrado.");
            return "redirect:/capataces";
        }
        model.addAttribute("capataz", capataz);
        model.addAttribute("pasos", pasoService.findAll());
        model.addAttribute("usuarios", userRepository.findAll());
        return "formularioEditarCapataz";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute CuerpoCapataces capataz, Model model) {
        capatazService.update(capataz.getDniCapataz(), capataz);
        model.addAttribute("success", "Capataz actualizado.");
        return "redirect:/capataces";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("dni") String dni, Model model) {
        capatazService.deleteById(dni);
        model.addAttribute("success", "Capataz eliminado.");
        return "redirect:/capataces";
    }

    @GetMapping("/mostrarFormularioBusqueda")
    public String mostrarFormularioBusqueda() {
        return "buscarCapataz";
    }

    @GetMapping("/buscar")
    public String buscarPorDni(@RequestParam("dni") String dni, Model model) {
        CuerpoCapataces capataz = capatazService.findById(dni).orElse(null);
        if (capataz == null) {
            model.addAttribute("error", "Capataz no encontrado.");
        } else {
            model.addAttribute("capataz", capataz);
        }
        return "verCapataz";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.CAPATAZ) {
            return "redirect:/login?unauthorized";
        }

        return "index"; // Vista específica del capataz
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

}
