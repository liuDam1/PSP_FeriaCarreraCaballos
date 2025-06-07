package es.etg.psp.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.etg.psp.model.Jugador;

public class Certificado {
    // Constantes para archivos
    private static final String CERTIFICADO_FILE = "certificado_ganador.md";
    private static final String RUTA_BAT = "trasformarPDF.bat";

    // Constantes para formatos
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";
    private static final String FORMATO_CERTIFICADO = "# 🥇 Certificado de Victoria\n\n" +
            "**Fecha:** %s\n\n" +
            "Se certifica que **%s** ha ganado la partida con **%d puntos**\n" +
            "en el juego Carrera de Caballos.\n\n" +
            "### Detalles de la partida:\n" +
            "- Jugador 1: %s (%d puntos)\n" +
            "- Jugador 2: %s (%d puntos)\n\n" +
            "**¡Excelente desempeño!**\n\n" +
            "```\n" +
            "         .-=========-.\n" +
            "         \\'-=======-'/\n" +
            "         _|   .=.   |_\n" +
            "        ((|  {{1}}  |))\n" +
            "         \\|   /|\\   |/\n" +
            "          \\__ '`' __/\n" +
            "            _`) (`_\n" +
            "          _/_______\\_\n" +
            "         /___________\\\n" +
            "```";

    // Constantes para mensajes de log
    private static final String MENSAJE_CERTIFICADO_GENERADO = "Certificado generado: ";
    private static final String ERROR_GENERAR_CERTIFICADO = "Error al generar certificado: ";
    private static final String MENSAJE_PDF_EXITOSO = "PDF generado exitosamente";
    private static final String ERROR_GENERAR_PDF = "Error al generar PDF. Código de salida: ";
    private static final String ERROR_EJECUTAR_BAT = "Excepción al ejecutar .bat: ";
    private static final String INICIO_GENERACION_CERT = "Iniciando generación de certificado para ganador: ";
    private static final String INICIO_CONVERSION_PDF = "Iniciando conversión a PDF";
    private static final String DEBUG_DETALLES_CERT = "Detalles del certificado:\n%s";
    private static final String LOG_SALIDA_PROCESO = "Salida del proceso: %s";

    // Constantes para comandos
    private static final String COMANDO_CMD = "cmd.exe";
    private static final String PARAMETRO_CMD = "/c";

    public static void generarCertificado(Jugador ganador, Jugador jugador1, Jugador jugador2) {
        GestionLog.registrar(TipoLog.INFO, INICIO_GENERACION_CERT + ganador.getNombre());

        try (FileWriter writer = new FileWriter(CERTIFICADO_FILE)) {
            LocalDateTime fecha = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);

            String certificado = String.format(FORMATO_CERTIFICADO,
                    fecha.format(formatter),
                    ganador.getNombre(),
                    ganador.getPuntos(),
                    jugador1.getNombre(), jugador1.getPuntos(),
                    jugador2.getNombre(), jugador2.getPuntos());

            writer.write(certificado);
            GestionLog.registrar(TipoLog.INFO, MENSAJE_CERTIFICADO_GENERADO + CERTIFICADO_FILE);
            GestionLog.registrar(TipoLog.DEBUG, String.format(DEBUG_DETALLES_CERT, certificado));

            convertirMarkdownAPDF();

        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, ERROR_GENERAR_CERTIFICADO + e.getMessage());
        }
    }

    public static void convertirMarkdownAPDF() {
        GestionLog.registrar(TipoLog.INFO, INICIO_CONVERSION_PDF);

        try {
            ProcessBuilder pb = new ProcessBuilder(COMANDO_CMD, PARAMETRO_CMD, RUTA_BAT);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();

            // Registrar salida del proceso
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    GestionLog.registrar(TipoLog.DEBUG, String.format(LOG_SALIDA_PROCESO, linea));
                }
            }

            int exitCode = proceso.waitFor();
            if (exitCode == 0) {
                GestionLog.registrar(TipoLog.INFO, MENSAJE_PDF_EXITOSO);
            } else {
                GestionLog.registrar(TipoLog.ERROR, ERROR_GENERAR_PDF + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            GestionLog.registrar(TipoLog.ERROR, ERROR_EJECUTAR_BAT + e.getMessage());

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
