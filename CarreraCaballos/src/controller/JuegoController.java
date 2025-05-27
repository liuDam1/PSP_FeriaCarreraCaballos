package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

import client.Cliente;
import model.Jugador;
import model.Operacion;
import util.Persistencia;

public class JuegoController {
    @FXML
    private Label etiquetaJugador1, etiquetaJugador2;
    @FXML
    private Label etiquetaPuntos1, etiquetaPuntos2;
    @FXML
    private Label numero1Operacion, numero2Operacion, operador;
    @FXML
    private TextField campoRespuesta;
    @FXML
    private Label etiquetaTurno;

    private Cliente cliente;
    private Operacion operacionActual;
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador jugadorTurno;

    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            this.jugador1 = jugador1;
            this.jugador2 = jugador2;
            cliente = new Cliente();
            cliente.enviarJugadores(jugador1, jugador2);

            etiquetaJugador1.setText(jugador1.getNombre());
            etiquetaJugador2.setText(jugador2.getNombre());
            jugadorTurno = (Math.random() < 0.5)? jugador1 : jugador2;
            etiquetaTurno.setText("Turno de: " + jugadorTurno.getNombre());
            actualizarInterfaz();

        } catch (IOException e) {
            mostrarError("Error al conectar con el servidor");
        }
    }

    @FXML
    private void handleAceptar() {
        try {
            if (operacionActual == null) {
                operacionActual = cliente.solicitarOperacion();
                mostrarOperacion(operacionActual);
            } else {
                int respuesta = Integer.parseInt(campoRespuesta.getText());
                boolean correcto = cliente.verificarRespuesta(respuesta, operacionActual);

                if (correcto) {
                    // Lógica para sumar puntos
                    int puntosRonda = 1; 
                    jugadorTurno.sumarPuntos(puntosRonda);
                }

                cambiarTurno();

                if (jugador1.getPuntos() >= 100 || jugador2.getPuntos() >= 100) {
                    Jugador ganador = (jugador1.getPuntos() >= 100)? jugador1 : jugador2;
                    Jugador perdedor = (ganador == jugador1)? jugador2 : jugador1;
                    Persistencia.guardarPartida(jugador1.getNombre(), jugador2.getNombre(), ganador.getNombre(), ganador.getPuntos(), perdedor.getPuntos());
                    mostrarMensajeGanador(ganador);
                }

                operacionActual = null;
                limpiarOperacion();
                actualizarInterfaz();
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void cambiarTurno() {
        jugadorTurno = (jugadorTurno == jugador1)? jugador2 : jugador1;
        etiquetaTurno.setText("Turno de: " + jugadorTurno.getNombre());
    }

    private void mostrarOperacion(Operacion op) {
        numero1Operacion.setText(String.valueOf(op.getNum1()));
        numero2Operacion.setText(String.valueOf(op.getNum2()));
        operador.setText(String.valueOf(op.getOperador()));
    }

    private void limpiarOperacion() {
        numero1Operacion.setText("");
        numero2Operacion.setText("");
        operador.setText("");
        campoRespuesta.clear();
    }

    private void actualizarInterfaz() {
        etiquetaPuntos1.setText("Puntos: " + jugador1.getPuntos());
        etiquetaPuntos2.setText("Puntos: " + jugador2.getPuntos());
        etiquetaTurno.setText("Turno de: " + jugadorTurno.getNombre());
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensajeGanador(Jugador ganador) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Fin del juego");
        alert.setHeaderText(null);
        alert.setContentText("El ganador es: " + ganador.getNombre());
        alert.showAndWait();
    }
}
