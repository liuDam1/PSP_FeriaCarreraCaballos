package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Jugador;

public class Persistencia {
    private static final String ARCHIVO_HISTORIAL = "historial_carreras.txt";

    public static void guardarPartida(Jugador jugador1, Jugador jugador2, Jugador ganador) {
        int puntosPerdedor = (ganador == jugador1) ? jugador2.getPuntos() : jugador1.getPuntos();
        
        guardarPartida(jugador1.getNombre(), jugador2.getNombre(), 
                      ganador.getNombre(), ganador.getPuntos(), puntosPerdedor);
    }

    public static void guardarPartida(String jugador1, String jugador2, String ganador, int puntosGanador, int puntosPerdedor) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_HISTORIAL, true))) {
            LocalDateTime fecha = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            writer.write("=== NUEVA PARTIDA ===");
            writer.newLine();
            writer.write("Fecha: " + fecha.format(formatter));
            writer.newLine();
            writer.write("Jugadores: " + jugador1 + " vs " + jugador2);
            writer.newLine();
            writer.write("Ganador: " + ganador + " (" + puntosGanador + " puntos)");
            writer.newLine();
            writer.write("Perdedor: " + (ganador.equals(jugador1) ? jugador2 : jugador1) + " (" + puntosPerdedor + " puntos)");
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar el historial: " + e.getMessage());
        }
    }
}
