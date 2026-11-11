package com.mechanicsoft.Features.ConsultaPublica.controller;

import com.mechanicsoft.Features.ConsultaPublica.dto.ConsultaRequest;
import com.mechanicsoft.Features.ConsultaPublica.dto.ConsultaResponse;
import com.mechanicsoft.Features.ConsultaPublica.service.ConsultaPublicaService;
import com.mechanicsoft.security.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/publico")
public class ConsultaPublicaController {

    private final ConsultaPublicaService service;
    private final RateLimitService rateLimitService;

    public ConsultaPublicaController(ConsultaPublicaService service, RateLimitService rateLimitService) {
        this.service = service;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/moto")
    public ResponseEntity<?> consultar(@Valid @RequestBody ConsultaRequest request,
                                        HttpServletRequest httpRequest) {
        String ip = resolverIp(httpRequest);

        if (!rateLimitService.intentoPermitido(ip)) {
            long espera = rateLimitService.segundosParaReset(ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                        "mensaje", "Has superado el límite de consultas. Intenta de nuevo en " + espera + " segundos.",
                        "esperarSegundos", espera
                    ));
        }

        ConsultaResponse respuesta = service.consultar(
                request.placa(), request.tipoDocumento(), request.numeroDocumento());
        return ResponseEntity.ok(respuesta);
    }

    private String resolverIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
