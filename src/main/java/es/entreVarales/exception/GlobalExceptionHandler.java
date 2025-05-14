package es.entreVarales.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejador de excepciones para Costalero
    @ExceptionHandler(CostaleroException.class)
    public String handleCostaleroException(CostaleroException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error"; // Vista de error genérica
    }

    // Manejador de excepciones específicas para Aspirante
    @ExceptionHandler(AspiranteException.class)
    public String handleAspiranteException(AspiranteException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error"; // Vista de error genérica
    }

    // Manejador de excepciones generales
    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception ex, Model model) {
        model.addAttribute("error", "Error inesperado: " + ex.getMessage());
        return "error"; // Vista de error genérica
    }
}
