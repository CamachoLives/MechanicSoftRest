package com.mechanicsoft.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return cuerpo(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarTransicionInvalida(TransicionInvalidaException ex) {
        return cuerpo(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> manejarStockInsuficiente(StockInsuficienteException ex) {
        return cuerpo(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        return cuerpo(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // DataIntegrityViolationException extiende RuntimeException, así que sin este
    // manejador caía en el genérico de abajo: 500 con el mensaje crudo del driver
    // JDBC (nombre de constraint, tabla, etc.) filtrado tal cual al cliente. Un
    // teléfono/placa/código/usuario duplicado es un conflicto del cliente (409),
    // no un error del servidor.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridadDatos(DataIntegrityViolationException ex) {
        return cuerpo(HttpStatus.CONFLICT, "El registro entra en conflicto con datos existentes (¿un valor único duplicado?).");
    }

    // Se dispara cuando dos solicitudes casi simultáneas modifican el mismo
    // registro con @Version (hoy, Repuesto): la que confirma segunda pierde
    // la carrera. 409 con un mensaje accionable, no un 500 genérico.
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> manejarBloqueoOptimista(ObjectOptimisticLockingFailureException ex) {
        return cuerpo(HttpStatus.CONFLICT, "Otra operación modificó este registro al mismo tiempo. Vuelve a intentarlo.");
    }

    // Cuerpo JSON malformado o con un valor de enum que no existe (ej. "metodoPago":
    // "BITCOIN" contra MetodoPago). Jackson lo envuelve en esta excepción antes de
    // que el controlador la vea; sin manejador propio caía en el 500 genérico con
    // el mensaje interno de Jackson (nombres de clase incluidos).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
        return cuerpo(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido.");
    }

    // Un id no numérico en la URL (GET /api/clientes/abc) o un valor que no
    // matchea un enum en un query param (?estado=BOGUS) también los envuelve
    // Spring antes de llegar al controlador. Mismo motivo que arriba: sin esto
    // caía en 500 con el mensaje interno del framework.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return cuerpo(HttpStatus.BAD_REQUEST, "El valor de '" + ex.getName() + "' no es válido.");
    }

    // Para los pocos endpoints que reciben un Map<String,?> en vez de una entidad
    // validable con @Valid (cambiar estado, entradas/salidas de stock): un campo
    // ausente/nulo dentro del Map no lo detecta Bean Validation, y desembocaba en
    // NullPointerException al des-boxear un Integer o al pasarle null a
    // EstadoOrden.valueOf — ambos caían en el 500 genérico.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return cuerpo(HttpStatus.BAD_REQUEST, ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido",
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Object> cuerpo = cuerpoBase(HttpStatus.BAD_REQUEST, "Uno o más campos no son válidos.");
        cuerpo.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarGenerico(RuntimeException ex) {
        return cuerpo(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> cuerpo(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(cuerpoBase(status, mensaje));
    }

    private Map<String, Object> cuerpoBase(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("message", mensaje);
        return cuerpo;
    }
}
