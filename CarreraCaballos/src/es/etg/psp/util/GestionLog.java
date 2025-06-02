package es.etg.psp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GestionLog {
    private static final String ARCHIVO_LOG = "carrera_caballos.log";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

    public static void registrar(TipoLog tipo, String mensaje) {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(ARCHIVO_LOG, true))) {
            String nivel = obtenerNivelLog(tipo);
            String entrada = String.format("[%s] [%s] %s%n",
                    FORMATO_FECHA.format(LocalDateTime.now()),
                    nivel,
                    mensaje);
            writer.write(entrada);
        } catch (java.io.IOException e) {
            System.err.println("Error al registrar en el log: " + e.getMessage());
        }
    }

    private static String obtenerNivelLog(TipoLog tipo) {
        switch (tipo) {
            case TRACE:
                return "TRACE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            case FATAL:
                return "FATAL";
            default:
                return "INFO";
        }
    }
}
