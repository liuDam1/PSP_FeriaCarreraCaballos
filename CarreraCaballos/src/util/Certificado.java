package util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Jugador;

public class Certificado {
    // Constantes para archivos
    private static final String CERTIFICADO_FILE = "certificado_ganador.md";
    private static final String RUTA_BAT = "D:/Users/jiang/Desktop/PSP_FeriaCarreraCaballos/CarreraCaballos/trasformarPDF.bat";
    
    // Constantes para formatos
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";
    private static final String FORMATO_CERTIFICADO = 
            "# 🥇 Certificado de Victoria\n\n" +
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
    
    // Constantes para mensajes de consola
    private static final String MENSAJE_CERTIFICADO_GENERADO = "Certificado generado: ";
    private static final String ERROR_GENERAR_CERTIFICADO = "Error al generar certificado: ";
    private static final String MENSAJE_PDF_EXITOSO = "PDF generado exitosamente.";
    private static final String ERROR_GENERAR_PDF = "Error al generar PDF. Código de salida: ";
    private static final String ERROR_EJECUTAR_BAT = "Excepción al ejecutar .bat: ";
    
    // Constantes para comandos
    private static final String COMANDO_CMD = "cmd.exe";
    private static final String PARAMETRO_CMD = "/c";

    public static void generarCertificado(Jugador ganador, Jugador jugador1, Jugador jugador2) {
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
            System.out.println(MENSAJE_CERTIFICADO_GENERADO + CERTIFICADO_FILE);

            convertirMarkdownAPDF();

        } catch (IOException e) {
            System.err.println(ERROR_GENERAR_CERTIFICADO + e.getMessage());
        }
    }

    public static void convertirMarkdownAPDF() {
        try {
            ProcessBuilder pb = new ProcessBuilder(COMANDO_CMD, PARAMETRO_CMD, RUTA_BAT);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    System.out.println(linea);
                }
            }

            int exitCode = proceso.waitFor();
            if (exitCode == 0) {
                System.out.println(MENSAJE_PDF_EXITOSO);
            } else {
                System.err.println(ERROR_GENERAR_PDF + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println(ERROR_EJECUTAR_BAT + e.getMessage());
        }
    }
}
