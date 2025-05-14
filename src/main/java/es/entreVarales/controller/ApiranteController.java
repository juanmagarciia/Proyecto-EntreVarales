package es.entreVarales.controller;

import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;
import es.entreVarales.exception.AspiranteException;
import es.entreVarales.exception.AspiranteException.DniInvalidoException;
import es.entreVarales.exception.AspiranteException.NumTrabajaderaInvalidoException;
import es.entreVarales.exception.AspiranteException.PasoInvalidoException;
import es.entreVarales.exception.AspiranteException.UsuarioInvalidoException;
import es.entreVarales.model.Aspirantes;
import es.entreVarales.model.Costalero;
import es.entreVarales.repository.UserRepository;
import es.entreVarales.service.AspiranteService;
import es.entreVarales.service.PasoService;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

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
    
//    @GetMapping("/mostrarFormularioCrear")
//    public String mostrarFormularioCrear(Model model) {
//        model.addAttribute("aspirante", new Aspirantes());
//        model.addAttribute("pasos", pasoService.findAll()); // Obtiene los pasos
//        List<User> usuariosAspirantes = userRepository.findAll().stream()
//                .filter(u -> u.getRole() == User.Role.ASPIRANTE)
//                .toList();
//
//        model.addAttribute("usuarios", userRepository.findByRole(User.Role.ASPIRANTE));
// // Agrega los usuarios
//
//        return "formularioCrearAspirante";
//    }
    @GetMapping("/mostrarFormularioCrear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("aspirante", new Aspirantes());
        model.addAttribute("pasos", pasoService.findAll()); // Obtiene los pasos

        // 1. Obtener todos los usuarios con rol ASPIRANTE
        List<User> usuariosAspirantes = userRepository.findByRole(User.Role.ASPIRANTE);

        // 2. Obtener usuarios que ya están asignados a un aspirante
        List<User> usuariosYaAsignados = aspiranteService.findAll().stream()
                .map(Aspirantes::getUser)
                .collect(Collectors.toList());

        // 3. Filtrar los usuarios que aún no han sido asignados
        List<User> usuariosDisponibles = usuariosAspirantes.stream()
                .filter(user -> !usuariosYaAsignados.contains(user))
                .collect(Collectors.toList());

        model.addAttribute("usuarios", usuariosDisponibles); // solo los no asignados
        return "formularioCrearAspirante"; // Regresa al formulario para crear un aspirante
    }


//    @PostMapping("/crear")
//    public String crear(@ModelAttribute Aspirantes aspirante, Model model) {
//    	aspiranteService.save(aspirante);
//        model.addAttribute("success", "Aspirante creado correctamente.");
//        return "redirect:/aspirantes";
//    }
    @PostMapping("/crear")
    public String crear(@ModelAttribute Aspirantes aspirante, Model model) {
        try {
            // Validar los datos del aspirante
            validarAspirante(aspirante);
            
            // Si la validación es correcta, guardar el aspirante
            aspiranteService.save(aspirante);
            model.addAttribute("success", "Aspirante creado correctamente.");
            return "redirect:/aspirantes";
        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException e) {
            // Si se lanza alguna excepción de validación, mostrar el mensaje de error
            model.addAttribute("error", e.getMessage());
            return "formularioCrearAspirante";  // Regresar al formulario con el mensaje de error
        }
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


//    @PostMapping("/actualizar")
//    public String actualizar(@ModelAttribute Aspirantes aspirante, Model model) {
//    	aspiranteService.update(aspirante.getDniAspirante(), aspirante);
//        model.addAttribute("success", "Aspirante actualizado.");
//        return "redirect:/aspirantes";
//    }
    
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Aspirantes aspirante, Model model) {
        try {
            // Validar los datos del aspirante antes de actualizar
            validarAspirante(aspirante);
            
            // Si la validación es correcta, actualizar el aspirante
            aspiranteService.update(aspirante.getDniAspirante(), aspirante);
            model.addAttribute("success", "Aspirante actualizado.");
            return "redirect:/aspirantes";
        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException e) {
            // Si se lanza alguna excepción de validación, mostrar el mensaje de error
            model.addAttribute("error", e.getMessage());
            return "formularioEditarAspirante";  // Regresar al formulario de edición con el mensaje de error
        }
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
    public String aspirantevistaPrincipal(Model model, @RequestParam("dni") String dni) {
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

    
    private void validarAspirante(Aspirantes aspirante) {
        // Validación del DNI
        if (aspirante.getDniAspirante() == null || aspirante.getDniAspirante().isEmpty()) {
            throw new DniInvalidoException();
        }
        
        if (aspirante.getNumTrabajadera() < 1 || aspirante.getNumTrabajadera() > 10) {
            throw new NumTrabajaderaInvalidoException(aspirante.getNumTrabajadera());
        }

        // Validación del paso asignado
        if (aspirante.getPaso() == null) {
            throw new PasoInvalidoException();
        }


        // Validación del usuario asignado
        if (aspirante.getUser() == null || aspirante.getUser().getRole() != User.Role.ASPIRANTE) {
            throw new UsuarioInvalidoException();
        }
    }
}
