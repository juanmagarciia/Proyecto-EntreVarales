package es.entreVarales.controller;

import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;
import es.entreVarales.exception.CostaleroException.DniInvalidoException;
import es.entreVarales.exception.CostaleroException.NumTrabajaderaInvalidoException;
import es.entreVarales.exception.CostaleroException.PasoInvalidoException;
import es.entreVarales.exception.CostaleroException.TipoAlturaInvalidoException;
import es.entreVarales.exception.CostaleroException.UsuarioInvalidoException;
import es.entreVarales.model.Altura;
import es.entreVarales.model.Costalero;
import es.entreVarales.model.Paso;
import es.entreVarales.repository.UserRepository;
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





@Controller
@RequestMapping("/costaleros")
public class CostaleroController {

    @Autowired
    private CostaleroService costaleroService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasoService pasoService;
    
    
    @GetMapping("/inicio")
    public String costaleroInicio() {
        return "indexCostalero";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("costaleros", costaleroService.findAll());
        return "listaCostaleros";
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
        model.addAttribute("costalero", new Costalero());
        model.addAttribute("pasos", pasoService.findAll()); // Obtener pasos

        // 1. Obtener todos los usuarios con rol COSTALERO
        List<User> usuariosCostaleros = userRepository.findByRole(User.Role.COSTALERO);

        // 2. Obtener usuarios que ya están asignados a un costalero
        List<User> usuariosYaAsignados = costaleroService.findAll().stream()
                .map(Costalero::getUser)
                .collect(Collectors.toList());

        // 3. Filtrar los usuarios que aún no han sido asignados
        List<User> usuariosDisponibles = usuariosCostaleros.stream()
                .filter(user -> !usuariosYaAsignados.contains(user))
                .collect(Collectors.toList());

        model.addAttribute("usuarios", usuariosDisponibles); // solo los no asignados
        return "formularioCrearCostalero";
    }


//    @PostMapping("/crear")
//    public String crear(@ModelAttribute Costalero costalero, Model model) {
//        costaleroService.save(costalero);
//        model.addAttribute("success", "Costalero creado correctamente.");
//        return "redirect:/costaleros";
//    }
//    
    @PostMapping("/crear")
    public String crear(@ModelAttribute Costalero costalero, Model model) {
        validarCostalero(costalero);
        costaleroService.save(costalero);
        model.addAttribute("success", "Costalero creado correctamente.");
        return "redirect:/costaleros";
    }
    
   

    @GetMapping("/editar")
    public String mostrarFormularioEditar(@RequestParam("dni") String dni, Model model) {
        Costalero costalero = costaleroService.findById(dni).orElse(null);
        if (costalero == null) {
            model.addAttribute("error", "Costalero no encontrado.");
            return "redirect:/costaleros";
        }
        model.addAttribute("costalero", costalero);
        model.addAttribute("pasos", pasoService.findAll());
        model.addAttribute("usuarios", userRepository.findAll()); // <- esto falta
        return "formularioEditarCostalero";
    }


//    @PostMapping("/actualizar")
//    public String actualizar(@ModelAttribute Costalero costalero, Model model) {
//        costaleroService.update(costalero.getDniCostalero(), costalero);
//        model.addAttribute("success", "Costalero actualizado.");
//        return "redirect:/costaleros";
//    }
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Costalero costalero, Model model) {
        validarCostalero(costalero);
        costaleroService.update(costalero.getDniCostalero(), costalero);
        model.addAttribute("success", "Costalero actualizado correctamente.");
        return "redirect:/costaleros";
    }

//    @GetMapping("/eliminar")
//    public String eliminar(@RequestParam("dni") String dni, Model model) {
//        costaleroService.deleteById(dni);
//        model.addAttribute("success", "Costalero eliminado.");
//        return "redirect:/costaleros";
//    }
    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("dni") String dni, Model model) {
        costaleroService.deleteById(dni);
        model.addAttribute("success", "Costalero eliminado.");
        return "redirect:/costaleros";
    }


    @GetMapping("/mostrarFormularioBusqueda")
    public String mostrarFormularioBusqueda() {
        return "buscarCostalero";
    }

    @GetMapping("/buscar")
    public String buscarPorDni(@RequestParam("dni") String dni, Model model) {
        Costalero costalero = costaleroService.findById(dni).orElse(null);
        if (costalero == null) {
            model.addAttribute("error", "Costalero no encontrado.");
        } else {
            model.addAttribute("costalero", costalero);
        }
        return "verCostalero";
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

        return "cuadranteCostaleros";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.COSTALERO) {
            return "redirect:/login?unauthorized";
        }

        return "costaleros/dashboard";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
    
    private void validarCostalero(Costalero costalero) {
        if (costalero.getDniCostalero() == null || costalero.getDniCostalero().isEmpty()) {
            throw new DniInvalidoException();
        }

        if (costalero.getNumTrabajadera() < 1 || costalero.getNumTrabajadera() > 10) {
            throw new NumTrabajaderaInvalidoException(costalero.getNumTrabajadera());
        }

        if (costalero.getTipoAltura() == null) {
            throw new TipoAlturaInvalidoException();
        }

        if (costalero.getPaso() == null) {
            throw new PasoInvalidoException();
        }

        if (costalero.getUser() == null || costalero.getUser().getRole() != User.Role.COSTALERO) {
            throw new UsuarioInvalidoException();
        }
    }


}
