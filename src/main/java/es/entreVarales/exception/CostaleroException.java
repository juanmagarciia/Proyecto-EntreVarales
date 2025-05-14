package es.entreVarales.exception;

/**
 * Clase base y agrupadora para todas las excepciones específicas de Costalero.
 */
public class CostaleroException extends RuntimeException {
    public CostaleroException(String message) {
        super(message);
    }

    // Excepción: Costalero no encontrado
    public static class CostaleroNotFoundException extends CostaleroException {
        public CostaleroNotFoundException(String dni) {
            super("No se ha encontrado un costalero con DNI: " + dni);
        }
    }

    // Excepción: Número de trabajadera inválido
    public static class NumTrabajaderaInvalidoException extends CostaleroException {
        public NumTrabajaderaInvalidoException(int num) {
            super("El número de trabajadera debe estar entre 1 y 10. Valor recibido: " + num);
        }
    }

    // Excepción: Tipo de altura nulo o inválido
    public static class TipoAlturaInvalidoException extends CostaleroException {
        public TipoAlturaInvalidoException() {
            super("El tipo de altura es obligatorio y debe ser ALTA o BAJA.");
        }
    }

    // Excepción: Paso no asignado
    public static class PasoInvalidoException extends CostaleroException {
        public PasoInvalidoException() {
            super("El paso asignado no puede estar vacío.");
        }
    }

    // Excepción: Usuario no válido o sin rol adecuado
    public static class UsuarioInvalidoException extends CostaleroException {
        public UsuarioInvalidoException() {
            super("Debe seleccionar un usuario válido con rol COSTALERO.");
        }
    }

    // Excepción: DNI faltante o vacío
    public static class DniInvalidoException extends CostaleroException {
        public DniInvalidoException() {
            super("El DNI del costalero es obligatorio.");
        }
    }
    

public class DniExistenteException extends RuntimeException {
    public DniExistenteException(String dni) {
        super("El DNI " + dni + " ya está asociado a otro costalero.");
    }
}
}
