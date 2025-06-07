package es.entreVarales.exception;

/**
 * Clase base y agrupadora para todas las excepciones específicas de Aspirante.
 */
public class AspiranteException extends RuntimeException {
    public AspiranteException(String message) {
        super(message);
    }

    // Excepción: Aspirante no encontrado
    public static class AspiranteNotFoundException extends AspiranteException {
        public AspiranteNotFoundException(String dni) {
            super("No se ha encontrado un aspirante con DNI: " + dni);
        }
    }

    // Excepción: DNI inválido o vacío
    public static class DniInvalidoException extends AspiranteException {
        public DniInvalidoException() {
            super("El DNI del aspirante es obligatorio y no puede estar vacío.");
        }
    }

    // Excepción: DNI ya existente
    public static class DniExistenteException extends AspiranteException {
        public DniExistenteException(String dni) {
            super("El DNI " + dni + " ya está asociado a otro aspirante.");
        }
    }

    // Excepción: Paso no asignado
    public static class PasoInvalidoException extends AspiranteException {
        public PasoInvalidoException() {
            super("El paso asignado al aspirante no puede estar vacío.");
        }
    }

    // Excepción: Usuario no válido o sin rol adecuado
    public static class UsuarioInvalidoException extends AspiranteException {
        public UsuarioInvalidoException() {
            super("Debe seleccionar un usuario válido con rol ASPIRANTE.");
        }
    }

    // Excepción: Aspirante sin paso asignado
    public static class AspiranteSinPasoException extends AspiranteException {
        public AspiranteSinPasoException() {
            super("El aspirante debe tener un paso asignado.");
        }
    }

    // Excepción: Usuario no es aspirante
    public static class UsuarioNoAspiranteException extends AspiranteException {
        public UsuarioNoAspiranteException() {
            super("El usuario asociado no tiene el rol de aspirante.");
        }
    }
 // Excepción: Número de trabajadera inválido
    public static class NumTrabajaderaInvalidoException extends AspiranteException {
        public NumTrabajaderaInvalidoException(int num) {
            super("El número de trabajadera debe estar entre 1 y 10. Valor recibido: " + num);
        }
    }
    
    // Excepción: Número de trabajadera inválido
    public static class AlturaInvalidaException extends AspiranteException {
        public AlturaInvalidaException() {
        	 super("Debe seleccionar una altura.");
        }
    }
    
}
