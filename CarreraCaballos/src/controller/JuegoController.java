package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

import client.Cliente;
import model.Jugador;
import model.Operacion;

public class JuegoController {
    @FXML private Label etiquetaJugador1, etiquetaJugador2;
    @FXML private Label etiquetaPuntos1, etiquetaPuntos2;
    @FXML private Label numero1Operacion, numero2Operacion, operador;
    @FXML private TextField campoRespuesta;
    @FXML private Label etiquetaTurno;
    
    private Cliente cliente;
    private Operacion operacionActual;

    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            cliente = new Cliente();
            cliente.enviarJugadores(jugador1, jugador2);
            
            etiquetaJugador1.setText(jugador1.getNombre());
            etiquetaJugador2.setText(jugador2.getNombre());
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
                }
                
                operacionActual = null;
                limpiarOperacion();
                actualizarInterfaz();
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
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
        // Actualizar puntos y turno (requiere métodos adicionales en el servidor)
    }

    private void mostrarError(String mensaje) {
        // Implementar alerta de error
    }
}
