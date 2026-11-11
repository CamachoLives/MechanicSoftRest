package com.mechanicsoft.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Limita consultas públicas a MAX_INTENTOS por IP dentro de una ventana de VENTANA_MS.
 * Diseñado para un único nodo; migrar a Bucket4j+Redis si se escala horizontalmente.
 */
@Service
public class RateLimitService {

    private static final int MAX_INTENTOS = 10;
    private static final long VENTANA_MS = 60L * 60 * 1000; // 1 hora

    // [0] = contador de intentos, [1] = inicio de la ventana en milisegundos
    private final ConcurrentHashMap<String, long[]> registros = new ConcurrentHashMap<>();

    /**
     * Registra un intento para la IP dada y devuelve true si aún está dentro del límite.
     */
    public boolean intentoPermitido(String ip) {
        long ahora = System.currentTimeMillis();
        long[] registro = registros.compute(ip, (k, v) -> {
            if (v == null || ahora - v[1] > VENTANA_MS) {
                return new long[]{1, ahora};
            }
            v[0]++;
            return v;
        });
        return registro[0] <= MAX_INTENTOS;
    }

    /** Segundos que faltan para que se libere el bloqueo de esta IP. */
    public long segundosParaReset(String ip) {
        long[] registro = registros.get(ip);
        if (registro == null) return 0;
        long restante = VENTANA_MS - (System.currentTimeMillis() - registro[1]);
        return Math.max(0, restante / 1000);
    }
}
