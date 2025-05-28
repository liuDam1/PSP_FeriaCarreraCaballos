package util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Jugador;

public class Certificado {
    // Constantes para archivo y formato
    private static final String CERTIFICADO_FILE = "certificado_ganador.md";
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";

    // Constantes para mensajes
    private static final String MENSAJE_CERTIFICADO_GENERADO = "Certificado generado: ";
    private static final String ERROR_GENERAR_CERTIFICADO = "Error al generar certificado: ";

    private static final String FORMATO = "# 🥇 Certificado de Victoria\n\n" +
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

    public static void generarCertificado(Jugador ganador, Jugador jugador1, Jugador jugador2) {
        try (FileWriter writer = new FileWriter(CERTIFICADO_FILE)) {
            LocalDateTime fecha = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);

            String certificado = String.format(FORMATO,
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
        String rutaBat = "D:/Users/jiang/Desktop/PSP_FeriaCarreraCaballos/CarreraCaballos/trasformarPDF.bat";

        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", rutaBat);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();

            // Leer la salida del proceso (consola)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    System.out.println(linea);
                }
            }

            int exitCode = proceso.waitFor();
            if (exitCode == 0) {
                System.out.println("PDF generado exitosamente.");
            } else {
                System.err.println("Error al generar PDF. Código de salida: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Excepción al ejecutar .bat: " + e.getMessage());
        }
    }
}
