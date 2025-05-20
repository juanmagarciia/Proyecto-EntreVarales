package es.entreVarales.controller;

import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;
import es.entreVarales.exception.AspiranteException;
import es.entreVarales.exception.AspiranteException.DniInvalidoException;
import es.entreVarales.exception.AspiranteException.NumTrabajaderaInvalidoException;
import es.entreVarales.exception.AspiranteException.PasoInvalidoException;
import es.entreVarales.exception.AspiranteException.UsuarioInvalidoException;
import es.entreVarales.model.Altura;
import es.entreVarales.model.Aspirantes;
import es.entreVarales.model.Costalero;
import es.entreVarales.model.Paso;
import es.entreVarales.repository.UserRepository;
import es.entreVarales.service.AspiranteService;
import es.entreVarales.service.CostaleroService;
import es.entreVarales.service.PasoService;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;





@Controller
@RequestMapping("/aspirantes")
public class ApiranteController {

    @Autowired
    private AspiranteService aspiranteService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasoService pasoService;
    
    @Autowired
    private CostaleroService costaleroService;
    
    
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
    public String aspirantevistaPrincipal(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.ASPIRANTE) {
            return "redirect:/login?unauthorized";
        }
        
        


        Aspirantes aspirante = aspiranteService.findAll().stream()
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        model.addAttribute("aspirante", aspirante != null ? aspirante : new Aspirantes());
        model.addAttribute("usuarios", userRepository.findByRole(User.Role.ASPIRANTE));
        model.addAttribute("pasos", pasoService.findAll());
        model.addAttribute("user", user);

        // Pasa un flag que diga si ya hay un aspirante para este usuario
        model.addAttribute("yaRegistrado", aspirante != null);

        return "VistaAspirante";
    }

    @PostMapping("/crearAspiranteVista")
    public String crearAspiranteVista(@ModelAttribute Aspirantes aspirante,
                                     @RequestParam("userId") Long userId,
                                     RedirectAttributes redirectAttributes,
                                     HttpSession session) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no válido.");
            return "redirect:/aspirantes/vista";
        }

        aspirante.setUser(user);

        boolean yaRegistrado = aspiranteService.findAll().stream()
                .anyMatch(a -> a.getUser().getId().equals(user.getId()));

        if (yaRegistrado) {
            redirectAttributes.addFlashAttribute("error", "Ya has enviado una petición previamente.");
            redirectAttributes.addFlashAttribute("yaRegistrado", true);
            return "redirect:/aspirantes/vista";
        }

        try {
            validarAspirante(aspirante);
            aspiranteService.save(aspirante);
            redirectAttributes.addFlashAttribute("success", "Aspirante creado correctamente.");
            redirectAttributes.addFlashAttribute("yaRegistrado", true);
            return "redirect:/aspirantes/vista";
        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/aspirantes/vista";
        }
    }


    @PostMapping("/actualizarDatos")
    public String actualizarDatos(@ModelAttribute Aspirantes aspirante, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != Role.ASPIRANTE) {
            return "redirect:/login?unauthorized";
        }

        Aspirantes aspiranteExistente = aspiranteService.findById(aspirante.getDniAspirante()).orElse(null);
        if (aspiranteExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Aspirante no encontrado.");
            return "redirect:/aspirantes/vista";
        }

        aspirante.setUser(user);

        try {
            validarAspirante(aspirante);
            aspiranteService.update(aspirante.getDniAspirante(), aspirante);
            redirectAttributes.addFlashAttribute("success", "Datos actualizados correctamente.");
        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/aspirantes/vista";
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
    
    
    @GetMapping("/cuadrante")
    public String verCuadrante(@RequestParam(required = false) Integer idPaso,
                               @RequestParam(required = false) String tipoAltura,
                               Model model) {
        List<Costalero> costaleros;

        // Convertir tipoAltura a Enum si no es nulo
        Altura.TipoAltura tipoAlturaEnum = null;
        if (tipoAltura != null && !tipoAltura.isEmpty()) {
            try {
                tipoAlturaEnum = Altura.TipoAltura.valueOf(tipoAltura);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Tipo de Altura no válido: " + tipoAltura);
            }
        }

        // Aplicar filtrado con el Enum convertido
        if (idPaso != null && tipoAlturaEnum != null) {
            costaleros = costaleroService.findByPasoAndTipoAltura(idPaso, tipoAlturaEnum);
        } else if (idPaso != null) {
            costaleros = costaleroService.findByPaso(idPaso);
        } else if (tipoAlturaEnum != null) {
            costaleros = costaleroService.findByTipoAltura(tipoAlturaEnum);
        } else {
            costaleros = costaleroService.findAll();
        }

        System.out.println("Filtrando por idPaso=" + idPaso + ", tipoAltura=" + tipoAlturaEnum);
        System.out.println("Costaleros encontrados después del filtro: " + costaleros.size());

        Map<Paso, Map<String, Map<Integer, List<Costalero>>>> cuadrantesPorPaso = new LinkedHashMap<>();

        for (Costalero c : costaleros) {
            cuadrantesPorPaso
                .computeIfAbsent(c.getPaso(), k -> new LinkedHashMap<>())
                .computeIfAbsent(c.getTipoAltura().name(), k -> new TreeMap<>())
                .computeIfAbsent(c.getNumTrabajadera(), k -> new ArrayList<>())
                .add(c);
        }

        model.addAttribute("cuadrantesPorPaso", cuadrantesPorPaso);
        model.addAttribute("pasos", pasoService.findAll());
        model.addAttribute("tiposAltura", List.of("ALTA", "BAJA"));

        return "cuadranteCuadrillaVistaAspCost";
    }
}
