package es.entreVarales.controller;

import es.entreVarales.model.User;
import es.entreVarales.model.User.Role;
import es.entreVarales.exception.AspiranteException;
import es.entreVarales.exception.AspiranteException.AlturaInvalidaException;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;





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

//    @GetMapping
//    public String listar(Model model) {
//        model.addAttribute("aspirantes", aspiranteService.findAll());
//        return "listaAspirantes";
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
        
        List<Aspirantes> aspirantes = aspiranteService.findAll();
        
        // Aplicar filtros si existen
        if (dni != null && !dni.isEmpty()) {
            aspirantes = aspirantes.stream()
                .filter(a -> a.getDniAspirante().toLowerCase().contains(dni.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (nombre != null && !nombre.isEmpty()) {
            aspirantes = aspirantes.stream()
                .filter(a -> a.getNombreAspirante().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (apellido != null && !apellido.isEmpty()) {
            aspirantes = aspirantes.stream()
                .filter(a -> a.getApellidoAspirante().toLowerCase().contains(apellido.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (numTrabajadera != null) {
            aspirantes = aspirantes.stream()
                .filter(a -> a.getNumTrabajadera() == numTrabajadera)
                .collect(Collectors.toList());
        }
        
        if (tipoAltura != null && !tipoAltura.isEmpty()) {
            aspirantes = aspirantes.stream()
                .filter(a -> a.getTipoAltura().name().equals(tipoAltura))
                .collect(Collectors.toList());
        }
        
        if (paso != null) {
            aspirantes = aspirantes.stream()
                .filter(a -> a.getPaso().getIdPaso() == paso)
                .collect(Collectors.toList());
        }
        
        model.addAttribute("aspirantes", aspirantes);
        model.addAttribute("pasos", pasoService.findAll()); // Para el dropdown de pasos
        return "listaAspirantes";
    }
    
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
//        try {
//            // Validar los datos del aspirante
//            validarAspirante(aspirante);
//            
//            // Si la validación es correcta, guardar el aspirante
//            aspiranteService.save(aspirante);
//            model.addAttribute("success", "Aspirante creado correctamente.");
//            return "redirect:/aspirantes";
//        } catch (DniInvalidoException | PasoInvalidoException | UsuarioInvalidoException e) {
//            // Si se lanza alguna excepción de validación, mostrar el mensaje de error
//            model.addAttribute("error", e.getMessage());
//            return "formularioCrearAspirante";  // Regresar al formulario con el mensaje de error
//        }
//    }
    
    
    @PostMapping("/crear")
    public String crear(@ModelAttribute Aspirantes aspirante, Model model) {
        
        // Comprobar si ya existe un capataz con ese DNI
        if (aspiranteService.findById(aspirante.getDniAspirante()).isPresent()) {
            model.addAttribute("error", "Ya existe un aspirante con ese DNI.");
            
            
            model.addAttribute("aspirante", aspirante);

            // Volver a cargar los datos necesarios para el formulario
            model.addAttribute("pasos", pasoService.findAll());

            List<User> usuariosAspirante = userRepository.findByRole(User.Role.ASPIRANTE);
            List<User> usuariosYaAsignados = aspiranteService.findAll().stream()
                    .map(Aspirantes::getUser)
                    .collect(Collectors.toList());
            List<User> usuariosDisponibles = usuariosAspirante.stream()
                    .filter(user -> !usuariosYaAsignados.contains(user))
                    .collect(Collectors.toList());

            model.addAttribute("usuarios", usuariosDisponibles);

            return "formularioCrearAspirante";
        }
        
     
	     validarAspirante(aspirante);
	   
	
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
        model.addAttribute("usuarios", userRepository.findAll()); 
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
        if (aspirante.getPaso() == null || aspirante.getPaso().getIdPaso() == null) {
            throw new PasoInvalidoException();
        }


//        // Validación del usuario asignado
//        if (aspirante.getUser() == null || aspirante.getUser().getRole() != User.Role.ASPIRANTE || aspirante.getUser().getId() == null) {
//            throw new UsuarioInvalidoException();
//        }
        
        if (aspirante.getTipoAltura() == null) {
            throw new AlturaInvalidaException();
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
        List<Aspirantes> aspirantes = aspiranteService.findAll();
        
        if (dni != null && !dni.isEmpty()) {
            aspirantes = aspirantes.stream()
                .filter(c -> c.getDniAspirante().toLowerCase().contains(dni.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (nombre != null && !nombre.isEmpty()) {
        	aspirantes = aspirantes.stream()
                .filter(c -> c.getNombreAspirante().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (apellido != null && !apellido.isEmpty()) {
        	aspirantes = aspirantes.stream()
                .filter(c -> c.getApellidoAspirante().toLowerCase().contains(apellido.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (numTrabajadera != null) {
        	aspirantes = aspirantes.stream()
                .filter(c -> c.getNumTrabajadera() == numTrabajadera)
                .collect(Collectors.toList());
        }
        
        if (tipoAltura != null && !tipoAltura.isEmpty()) {
        	aspirantes = aspirantes.stream()
                .filter(c -> c.getTipoAltura().name().equals(tipoAltura))
                .collect(Collectors.toList());
        }
        
        if (paso != null) {
        	aspirantes = aspirantes.stream()
                .filter(c -> c.getPaso().getIdPaso() == paso)
                .collect(Collectors.toList());
        }

        // Configurar la respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"aspirantes_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf\"");

        // Crear el documento PDF
        Document document = new Document(PageSize.A4.rotate()); // Formato horizontal para mejor visualización
        PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();

        // Título del documento
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Lista de Aspirantes", titleFont);
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
        for (Aspirantes aspirante : aspirantes) {
            BaseColor rowColor = (rowIndex % 2 == 0) ? BaseColor.WHITE : alternateRowColor;
            
            // Datos de cada costalero
            String[] rowData = {
            		aspirante.getUser() != null ? aspirante.getUser().getUsername() : "N/A",
            				aspirante.getDniAspirante(),
            				aspirante.getNombreAspirante(),
            				aspirante.getApellidoAspirante(),
            				aspirante.getTelefonoAspirante(),
                String.valueOf(aspirante.getNumTrabajadera()),
                aspirante.getTipoAltura().toString(),
                aspirante.getPaso() != null ? aspirante.getPaso().getNombreTitular() : "N/A"
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
        Paragraph resumen = new Paragraph("\nTotal de costaleros: " + aspirantes.size(), 
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
//            Paso pasoObj = pasoService.findById(paso).orElse(null);
       	 Paso pasoObj = pasoService.findById(paso.longValue()).orElse(null);
            String nombrePaso = pasoObj != null ? pasoObj.getNombreTitular() : String.valueOf(paso);
            filtros.add("Paso: " + nombrePaso);
        }
        
        return String.join(", ", filtros);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
