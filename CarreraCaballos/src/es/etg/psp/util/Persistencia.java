package es.etg.psp.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import es.etg.psp.model.Jugador;

public class Persistencia {
    // Archivo y formato
    private static final String ARCHIVO_HISTORIAL = "historial_carreras.txt";
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";

    // Mensajes de log
    private static final String ERROR_GUARDAR_HISTORIAL = "Error al guardar el historial: ";
    private static final String MENSAJE_GUARDANDO_PARTIDA = "Guardando resultado de partida en historial";
    private static final String MENSAJE_PARTIDA_GUARDADA = "Partida guardada exitosamente";
    private static final String DEBUG_DETALLES_PARTIDA = "Detalles partida - Ganador: %s (%d pts) vs Perdedor: %s (%d pts)";

    // Textos para el archivo de historial
    private static final String CABECERA_PARTIDA = "=== NUEVA PARTIDA ===";
    private static final String PREFIX_FECHA = "Fecha: ";
    private static final String PREFIX_JUGADORES = "Jugadores: ";
    private static final String VS = " vs ";
    private static final String PREFIX_GANADOR = "Ganador: ";
    private static final String PREFIX_PERDEDOR = "Perdedor: ";
    private static final String PUNTOS_FORMAT = " (%d puntos)";
    private static final String SEPARADOR = "----------------------------------------";

    public static void guardarPartida(Jugador jugador1, Jugador jugador2, Jugador ganador) {
        GestionLog.registrar(TipoLog.INFO, MENSAJE_GUARDANDO_PARTIDA);

        int puntosPerdedor = (ganador == jugador1) ? jugador2.getPuntos() : jugador1.getPuntos();

        guardarPartida(jugador1.getNombre(), jugador2.getNombre(),
                ganador.getNombre(), ganador.getPuntos(), puntosPerdedor);
    }

    public static void guardarPartida(String jugador1, String jugador2, String ganador,
            int puntosGanador, int puntosPerdedor) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_HISTORIAL, true))) {
            LocalDateTime fecha = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);

            // Registramos los detalles de la partida
            writer.write(CABECERA_PARTIDA);
            writer.newLine();
            writer.write(PREFIX_FECHA + fecha.format(formatter));
            writer.newLine();
            writer.write(PREFIX_JUGADORES + jugador1 + VS + jugador2);
            writer.newLine();
            writer.write(PREFIX_GANADOR + ganador + String.format(PUNTOS_FORMAT, puntosGanador));
            writer.newLine();
            writer.write(PREFIX_PERDEDOR + (ganador.equals(jugador1) ? jugador2 : jugador1) +
                    String.format(PUNTOS_FORMAT, puntosPerdedor));
            writer.newLine();
            writer.write(SEPARADOR);
            writer.newLine();

            GestionLog.registrar(TipoLog.INFO, MENSAJE_PARTIDA_GUARDADA);
            GestionLog.registrar(TipoLog.DEBUG, String.format(DEBUG_DETALLES_PARTIDA,
                    ganador, puntosGanador,
                    (ganador.equals(jugador1) ? jugador2 : jugador1),
                    puntosPerdedor));

        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, ERROR_GUARDAR_HISTORIAL + e.getMessage());
        }
    }
}
