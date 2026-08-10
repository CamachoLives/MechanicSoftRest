package com.mechanicsoft.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
