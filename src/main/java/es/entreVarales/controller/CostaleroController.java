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
import es.entreVarales.model.CuerpoCapataces;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


//Añadir estas importaciones al inicio de tu CostaleroController
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




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

//    @GetMapping
//    public String listar(Model model) {
//        model.addAttribute("costaleros", costaleroService.findAll());
//        return "listaCostaleros";
//    }
    @GetMapping
    public String listar(
        @RequestParam(required = false) String dni,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) Integer numTrabajadera,
        @RequestParam(required = false) String tipoAltura,
        @RequestParam(required = false) Integer paso,
        Model model) {
        
        List<Costalero> costaleros = costaleroService.findAll();
        
        // Aplicar filtros si existen
        if (dni != null && !dni.isEmpty()) {
            costaleros = costaleros.stream()
                .filter(c -> c.getDniCostalero().toLowerCase().contains(dni.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (nombre != null && !nombre.isEmpty()) {
            costaleros = costaleros.stream()
                .filter(c -> c.getNombreCostalero().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (apellido != null && !apellido.isEmpty()) {
            costaleros = costaleros.stream()
                .filter(c -> c.getApellidoCostalero().toLowerCase().contains(apellido.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (numTrabajadera != null) {
            costaleros = costaleros.stream()
                .filter(c -> c.getNumTrabajadera() == numTrabajadera)
                .collect(Collectors.toList());
        }
        
        if (tipoAltura != null && !tipoAltura.isEmpty()) {
            costaleros = costaleros.stream()
                .filter(c -> c.getTipoAltura().name().equals(tipoAltura))
                .collect(Collectors.toList());
        }
        
        if (paso != null) {
            costaleros = costaleros.stream()
                .filter(c -> c.getPaso().getIdPaso() == paso)
                .collect(Collectors.toList());
        }
        
        model.addAttribute("costaleros", costaleros);
        model.addAttribute("pasos", pasoService.findAll()); // Para el dropdown de pasos
        return "listaCostaleros";
    }

    
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
//        validarCostalero(costalero);
//        costaleroService.save(costalero);
//        model.addAttribute("success", "Costalero creado correctamente.");
//        return "redirect:/costaleros";
//    }
    
    @PostMapping("/crear")
    public String crear(@ModelAttribute Costalero costalero, Model model) {
        
        // Comprobar si ya existe un capataz con ese DNI
        if (costaleroService.findById(costalero.getDniCostalero()).isPresent()) {
            model.addAttribute("error", "Ya existe un cotalero con ese DNI.");
            
            
            model.addAttribute("costalero", costalero);

            // Volver a cargar los datos necesarios para el formulario
            model.addAttribute("pasos", pasoService.findAll());

            List<User> usuariosCostalero = userRepository.findByRole(User.Role.COSTALERO);
            List<User> usuariosYaAsignados = costaleroService.findAll().stream()
                    .map(Costalero::getUser)
                    .collect(Collectors.toList());
            List<User> usuariosDisponibles = usuariosCostalero.stream()
                    .filter(user -> !usuariosYaAsignados.contains(user))
                    .collect(Collectors.toList());

            model.addAttribute("usuarios", usuariosDisponibles);

            return "formularioCrearCostalero";
        }
        
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

//        if (costalero.getUser() == null || costalero.getUser().getRole() != User.Role.COSTALERO) {
//            throw new UsuarioInvalidoException();
//        }
    }
    
    
    
    @GetMapping("/vista")
    public String costaleroVistaPrincipal(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.COSTALERO) {
            return "redirect:/login?unauthorized";
        }

        Costalero costalero = costaleroService.findAll().stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        model.addAttribute("costalero", costalero != null ? costalero : new Costalero());
        model.addAttribute("usuarios", userRepository.findByRole(User.Role.COSTALERO));
        model.addAttribute("pasos", pasoService.findAll());
        model.addAttribute("user", user);
        model.addAttribute("yaRegistrado", costalero != null);

        return "VistaCostalero";
    }

    
    @PostMapping("/crearCostaleroVista")
    public String crearCostaleroVista(@ModelAttribute Costalero costalero,
                                     @RequestParam("userId") Long userId,
                                     RedirectAttributes redirectAttributes,
                                     HttpSession session) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no válido.");
            return "redirect:/costaleros/vista";
        }

        costalero.setUser(user);

        boolean yaRegistrado = costaleroService.findAll().stream()
                .anyMatch(c -> c.getUser().getId().equals(user.getId()));

        if (yaRegistrado) {
            redirectAttributes.addFlashAttribute("error", "Ya has enviado una petición previamente.");
            redirectAttributes.addFlashAttribute("yaRegistrado", true);
            return "redirect:/costaleros/vista";
        }

        try {
            validarCostalero(costalero);
            costaleroService.save(costalero);
            redirectAttributes.addFlashAttribute("success", "Costalero creado correctamente.");
            redirectAttributes.addFlashAttribute("yaRegistrado", true);
            return "redirect:/costaleros/vista";
        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException |
                 NumTrabajaderaInvalidoException | TipoAlturaInvalidoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/costaleros/vista";
        }
    }

    
    @PostMapping("/actualizarDatos")
    public String actualizarDatos(@ModelAttribute Costalero costalero,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.COSTALERO) {
            return "redirect:/login?unauthorized";
        }

        Costalero costaleroExistente = costaleroService.findById(costalero.getDniCostalero()).orElse(null);
        if (costaleroExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Costalero no encontrado.");
            return "redirect:/costaleros/vista";
        }

        costalero.setUser(user);

        try {
            validarCostalero(costalero);
            costaleroService.update(costalero.getDniCostalero(), costalero);
            redirectAttributes.addFlashAttribute("success", "Datos actualizados correctamente.");
        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException |
                 NumTrabajaderaInvalidoException | TipoAlturaInvalidoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/costaleros/vista";
    }

    
    @GetMapping("/cuadranteVistaUsuario")
    public String verCuadranteCostalero(@RequestParam(required = false) Integer idPaso,
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
    
    
    
    
    
   
 @GetMapping("/descargar-pdf")
 public void descargarPDF(
     @RequestParam(required = false) String dni,
     @RequestParam(required = false) String nombre,
     @RequestParam(required = false) String apellido,
     @RequestParam(required = false) Integer numTrabajadera,
     @RequestParam(required = false) String tipoAltura,
     @RequestParam(required = false) Integer paso,
     HttpServletResponse response) throws IOException, DocumentException {
     
     // Aplicar los mismos filtros que en el método listar
     List<Costalero> costaleros = costaleroService.findAll();
     
     if (dni != null && !dni.isEmpty()) {
         costaleros = costaleros.stream()
             .filter(c -> c.getDniCostalero().toLowerCase().contains(dni.toLowerCase()))
             .collect(Collectors.toList());
     }
     
     if (nombre != null && !nombre.isEmpty()) {
         costaleros = costaleros.stream()
             .filter(c -> c.getNombreCostalero().toLowerCase().contains(nombre.toLowerCase()))
             .collect(Collectors.toList());
     }
     
     if (apellido != null && !apellido.isEmpty()) {
         costaleros = costaleros.stream()
             .filter(c -> c.getApellidoCostalero().toLowerCase().contains(apellido.toLowerCase()))
             .collect(Collectors.toList());
     }
     
     if (numTrabajadera != null) {
         costaleros = costaleros.stream()
             .filter(c -> c.getNumTrabajadera() == numTrabajadera)
             .collect(Collectors.toList());
     }
     
     if (tipoAltura != null && !tipoAltura.isEmpty()) {
         costaleros = costaleros.stream()
             .filter(c -> c.getTipoAltura().name().equals(tipoAltura))
             .collect(Collectors.toList());
     }
     
     if (paso != null) {
         costaleros = costaleros.stream()
             .filter(c -> c.getPaso().getIdPaso() == paso)
             .collect(Collectors.toList());
     }

     // Configurar la respuesta HTTP
     response.setContentType("application/pdf");
     response.setHeader("Content-Disposition", "attachment; filename=\"costaleros_" + 
         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf\"");

     // Crear el documento PDF
     Document document = new Document(PageSize.A4.rotate()); // Formato horizontal para mejor visualización
     PdfWriter.getInstance(document, response.getOutputStream());
     
     document.open();

     // Título del documento
     Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
     Paragraph title = new Paragraph("Lista de Costaleros", titleFont);
     title.setAlignment(Element.ALIGN_CENTER);
     title.setSpacingAfter(20);
     document.add(title);

     // Información de filtros aplicados
     if (hayFiltrosAplicados(dni, nombre, apellido, numTrabajadera, tipoAltura, paso)) {
         Font filterFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
         Paragraph filtros = new Paragraph("Filtros aplicados: " + construirTextoFiltros(dni, nombre, apellido, numTrabajadera, tipoAltura, paso), filterFont);
         filtros.setSpacingAfter(15);
         document.add(filtros);
     }

     // Fecha de generación
     Font dateFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
     Paragraph fecha = new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), dateFont);
     fecha.setAlignment(Element.ALIGN_RIGHT);
     fecha.setSpacingAfter(20);
     document.add(fecha);

     // Crear tabla
     PdfPTable table = new PdfPTable(8); // 8 columnas
     table.setWidthPercentage(100);
     
     // Definir anchos de columnas
     float[] columnWidths = {15f, 12f, 15f, 15f, 12f, 8f, 8f, 15f};
     table.setWidths(columnWidths);

     // Estilo para headers
     Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
     BaseColor headerBackground = new BaseColor(255, 123, 0); // Color naranja

     // Headers de la tabla
     String[] headers = {"Usuario", "DNI", "Nombre", "Apellido", "Teléfono", "Nº Trab.", "Altura", "Paso"};
     
     for (String header : headers) {
         PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
         cell.setBackgroundColor(headerBackground);
         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell.setPadding(8);
         table.addCell(cell);
     }

     // Estilo para datos
     Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
     BaseColor alternateRowColor = new BaseColor(245, 245, 245);

     // Agregar datos
     int rowIndex = 0;
     for (Costalero costalero : costaleros) {
         BaseColor rowColor = (rowIndex % 2 == 0) ? BaseColor.WHITE : alternateRowColor;
         
         // Datos de cada costalero
         String[] rowData = {
             costalero.getUser() != null ? costalero.getUser().getUsername() : "N/A",
             costalero.getDniCostalero(),
             costalero.getNombreCostalero(),
             costalero.getApellidoCostalero(),
             costalero.getTelefonoCostalero(),
             String.valueOf(costalero.getNumTrabajadera()),
             costalero.getTipoAltura().toString(),
             costalero.getPaso() != null ? costalero.getPaso().getNombreTitular() : "N/A"
         };

         for (String data : rowData) {
             PdfPCell cell = new PdfPCell(new Phrase(data != null ? data : "N/A", dataFont));
             cell.setBackgroundColor(rowColor);
             cell.setHorizontalAlignment(Element.ALIGN_CENTER);
             cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
             cell.setPadding(5);
             table.addCell(cell);
         }
         rowIndex++;
     }

     document.add(table);

     // Resumen
     Paragraph resumen = new Paragraph("\nTotal de costaleros: " + costaleros.size(), 
         new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
     resumen.setSpacingBefore(20);
     document.add(resumen);

     document.close();
 }

 // Métodos auxiliares
 private boolean hayFiltrosAplicados(String dni, String nombre, String apellido, 
                                    Integer numTrabajadera, String tipoAltura, Integer paso) {
     return (dni != null && !dni.isEmpty()) ||
            (nombre != null && !nombre.isEmpty()) ||
            (apellido != null && !apellido.isEmpty()) ||
            numTrabajadera != null ||
            (tipoAltura != null && !tipoAltura.isEmpty()) ||
            paso != null;
 }

 private String construirTextoFiltros(String dni, String nombre, String apellido, 
                                    Integer numTrabajadera, String tipoAltura, Integer paso) {
     List<String> filtros = new ArrayList<>();
     
     if (dni != null && !dni.isEmpty()) filtros.add("DNI: " + dni);
     if (nombre != null && !nombre.isEmpty()) filtros.add("Nombre: " + nombre);
     if (apellido != null && !apellido.isEmpty()) filtros.add("Apellido: " + apellido);
     if (numTrabajadera != null) filtros.add("Nº Trabajadera: " + numTrabajadera);
     if (tipoAltura != null && !tipoAltura.isEmpty()) filtros.add("Altura: " + tipoAltura);
     if (paso != null) {
         // Buscar el nombre del paso
//         Paso pasoObj = pasoService.findById(paso).orElse(null);
    	 Paso pasoObj = pasoService.findById(paso.longValue()).orElse(null);
         String nombrePaso = pasoObj != null ? pasoObj.getNombreTitular() : String.valueOf(paso);
         filtros.add("Paso: " + nombrePaso);
     }
     
     return String.join(", ", filtros);
 }
    
    
   
 
 @GetMapping("/descargar-cuadrante-pdf")
 public void descargarCuadrantePDF(
     @RequestParam(required = false) Integer idPaso,
     @RequestParam(required = false) String tipoAltura,
     HttpServletResponse response) throws IOException, DocumentException {
     
     // Convertir tipoAltura a Enum si no es nulo
     Altura.TipoAltura tipoAlturaEnum = null;
     if (tipoAltura != null && !tipoAltura.isEmpty()) {
         try {
             tipoAlturaEnum = Altura.TipoAltura.valueOf(tipoAltura);
         } catch (IllegalArgumentException e) {
             System.out.println("⚠️ Tipo de Altura no válido: " + tipoAltura);
         }
     }

     // Aplicar filtrado (misma lógica que en verCuadrante)
     List<Costalero> costaleros;
     if (idPaso != null && tipoAlturaEnum != null) {
         costaleros = costaleroService.findByPasoAndTipoAltura(idPaso, tipoAlturaEnum);
     } else if (idPaso != null) {
         costaleros = costaleroService.findByPaso(idPaso);
     } else if (tipoAlturaEnum != null) {
         costaleros = costaleroService.findByTipoAltura(tipoAlturaEnum);
     } else {
         costaleros = costaleroService.findAll();
     }

     // Organizar los datos en la estructura de cuadrantes
     Map<Paso, Map<String, Map<Integer, List<Costalero>>>> cuadrantesPorPaso = new LinkedHashMap<>();
     for (Costalero c : costaleros) {
         cuadrantesPorPaso
             .computeIfAbsent(c.getPaso(), k -> new LinkedHashMap<>())
             .computeIfAbsent(c.getTipoAltura().name(), k -> new TreeMap<>())
             .computeIfAbsent(c.getNumTrabajadera(), k -> new ArrayList<>())
             .add(c);
     }

     // Configurar la respuesta HTTP
     response.setContentType("application/pdf");
     response.setHeader("Content-Disposition", "attachment; filename=\"cuadrante_costaleros_" + 
         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf\"");

     // Crear el documento PDF
     Document document = new Document(PageSize.A4.rotate()); // Formato horizontal
     PdfWriter.getInstance(document, response.getOutputStream());
     
     document.open();

     // Título del documento
     Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
     Paragraph title = new Paragraph("Cuadrante de Costaleros", titleFont);
     title.setAlignment(Element.ALIGN_CENTER);
     title.setSpacingAfter(20);
     document.add(title);

     // Información de filtros aplicados
     if (idPaso != null || tipoAltura != null) {
         Font filterFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
         
         StringBuilder filtrosText = new StringBuilder("Filtros aplicados: ");
         if (idPaso != null) {
             Paso paso = pasoService.findById(idPaso.longValue()).orElse(null);
             filtrosText.append("Paso: ").append(paso != null ? paso.getNombreTitular() : idPaso);
             if (tipoAltura != null) filtrosText.append(", ");
         }
         if (tipoAltura != null) {
             filtrosText.append("Tipo Altura: ").append(tipoAltura);
         }
         
         Paragraph filtros = new Paragraph(filtrosText.toString(), filterFont);
         filtros.setSpacingAfter(15);
         document.add(filtros);
     }

     // Fecha de generación
     Font dateFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
     Paragraph fecha = new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), dateFont);
     fecha.setAlignment(Element.ALIGN_RIGHT);
     fecha.setSpacingAfter(20);
     document.add(fecha);

     // Generar contenido del PDF
     for (Map.Entry<Paso, Map<String, Map<Integer, List<Costalero>>>> pasoEntry : cuadrantesPorPaso.entrySet()) {
         // Título del paso
         Font pasoFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
         Paragraph pasoTitle = new Paragraph("Paso: " + pasoEntry.getKey().getNombreTitular(), pasoFont);
         pasoTitle.setSpacingAfter(10);
         document.add(pasoTitle);

         for (Map.Entry<String, Map<Integer, List<Costalero>>> alturaEntry : pasoEntry.getValue().entrySet()) {
             // Subtipo de altura
             Font alturaFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
             Paragraph alturaTitle = new Paragraph("Tipo de Altura: " + alturaEntry.getKey(), alturaFont);
             alturaTitle.setSpacingAfter(10);
             document.add(alturaTitle);

             // Crear tabla
             PdfPTable table = new PdfPTable(6); // 6 columnas
             table.setWidthPercentage(100);
             
             // Definir anchos de columnas
             float[] columnWidths = {20f, 20f, 20f, 20f, 20f, 10f};
             table.setWidths(columnWidths);

             // Estilo para headers
             Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
             BaseColor headerBackground = new BaseColor(255, 123, 0); // Color naranja

             // Headers de la tabla
             String[] headers = {"Costero Izq", "Fijador Izq", "Corriente", "Fijador Dcho", "Costero Dcho", "Trab."};
             
             for (String header : headers) {
                 PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                 cell.setBackgroundColor(headerBackground);
                 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 cell.setPadding(8);
                 table.addCell(cell);
             }

             // Estilo para datos
             Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
             BaseColor alternateRowColor = new BaseColor(245, 245, 245);

             // Agregar datos
             int rowIndex = 0;
             for (Map.Entry<Integer, List<Costalero>> trabajaderaEntry : alturaEntry.getValue().entrySet()) {
                 BaseColor rowColor = (rowIndex % 2 == 0) ? BaseColor.WHITE : alternateRowColor;
                 
                 // Agregar celdas para cada posición (0-4) y la trabajadera
                 for (int i = 0; i < 5; i++) {
                     String nombre = "";
                     if (trabajaderaEntry.getValue().size() > i) {
                         Costalero c = trabajaderaEntry.getValue().get(i);
                         nombre = c.getNombreCostalero() + " " + c.getApellidoCostalero();
                     }
                     
                     PdfPCell cell = new PdfPCell(new Phrase(nombre, dataFont));
                     cell.setBackgroundColor(rowColor);
                     cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                     cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                     cell.setPadding(5);
                     table.addCell(cell);
                 }
                 
                 // Celda de trabajadera
                 PdfPCell trabajaderaCell = new PdfPCell(new Phrase(trabajaderaEntry.getKey() + "°", dataFont));
                 trabajaderaCell.setBackgroundColor(rowColor);
                 trabajaderaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                 trabajaderaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 trabajaderaCell.setPadding(5);
                 table.addCell(trabajaderaCell);
                 
                 rowIndex++;
             }

             document.add(table);
             document.add(new Paragraph("\n")); // Espacio entre tablas
         }
     }

     document.close();
 }
    
    


}
