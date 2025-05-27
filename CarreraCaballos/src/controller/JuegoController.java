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

/**
 * Controlador para la vista del juego.
 * Gestiona la interacción entre la interfaz de usuario y la lógica del juego.
 */
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

    /**
     * Inicializa los datos del juego y establece la conexión con el servidor.
     * @param jugador1 Primer jugador.
     * @param jugador2 Segundo jugador.
     */
    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            this.jugador1 = jugador1;
            this.jugador2 = jugador2;
            cliente = new Cliente();
            cliente.enviarJugadores(jugador1.getNombre(), jugador2.getNombre());

            // Configurar la interfaz de usuario con los nombres de los jugadores
            etiquetaJugador1.setText(jugador1.getNombre());
            etiquetaJugador2.setText(jugador2.getNombre());
            
            // Elegir aleatoriamente el jugador que comienza
            jugadorTurno = (Math.random() < 0.5) ? jugador1 : jugador2;
            etiquetaTurno.setText("Turno de: " + jugadorTurno.getNombre());
            
            // Actualizar la interfaz con los puntos iniciales
            actualizarInterfaz();

            // Solicitar la primera operación
            solicitarNuevaOperacion();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al conectar con el servidor: " + e.getMessage());
            
            // Cerrar la conexión en caso de error
            if (cliente != null) {
                cliente.cerrarConexion();
            }
        }
    }

    /**
     * Maneja el evento del botón Aceptar.
     * Verifica la respuesta del jugador y gestiona el flujo del juego.
     */
    @FXML
    private void handleAceptar() {
        if (campoRespuesta.getText().isEmpty()) {
            mostrarError("Debes ingresar una respuesta");
            return;
        }
        
        try {
            int respuesta = Integer.parseInt(campoRespuesta.getText());
            
            if (operacionActual == null) {
                mostrarError("No hay una operación activa");
                return;
            }
            
            // Verificar la respuesta con el servidor
            boolean correcto = cliente.verificarRespuesta(respuesta, operacionActual);

            if (correcto) {
                // Sumar puntos al jugador actual
                int puntosRonda = 1;
                jugadorTurno.sumarPuntos(puntosRonda);
                mostrarMensaje("¡Respuesta correcta! +" + puntosRonda + " puntos para " + jugadorTurno.getNombre());
            } else {
                mostrarMensaje("Respuesta incorrecta");
            }

            // Cambiar el turno al siguiente jugador
            cambiarTurno();

            // Limpiar la operación actual antes de verificar si hay un ganador
            limpiarOperacion();

            // Verificar si hay un ganador
            if (jugador1.getPuntos() >= 100 || jugador2.getPuntos() >= 100) {
                Jugador ganador = (jugador1.getPuntos() >= 100) ? jugador1 : jugador2;
                Jugador perdedor = (ganador == jugador1) ? jugador2 : jugador1;
                
                // Guardar el resultado de la partida
                Persistencia.guardarPartida(jugador1.getNombre(), jugador2.getNombre(), ganador.getNombre(),
                        ganador.getPuntos(), perdedor.getPuntos());
                
                // Mostrar mensaje de victoria y cerrar la conexión
                mostrarMensajeGanador(ganador);
                if (cliente != null) {
                    cliente.cerrarConexion();
                }
            } else {
                // Continuar el juego y solicitar una nueva operación
                solicitarNuevaOperacion();
            }

            // Actualizar la interfaz con los nuevos puntos y turno
            actualizarInterfaz();
        } catch (NumberFormatException e) {
            mostrarError("Debes ingresar un número válido");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error: " + e.getMessage());
            
            // Cerrar la conexión en caso de error
            if (cliente != null) {
                cliente.cerrarConexion();
            }
        }
    }

    /**
     * Solicita una nueva operación matemática al servidor.
     */
    private void solicitarNuevaOperacion() {
        try {
            operacionActual = cliente.solicitarOperacion();
            mostrarOperacion(operacionActual);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            mostrarError("Error al obtener nueva operación: " + e.getMessage());
        }
    }

    /**
     * Cambia el turno al siguiente jugador.
     */
    private void cambiarTurno() {
        jugadorTurno = (jugadorTurno == jugador1) ? jugador2 : jugador1;
        etiquetaTurno.setText("Turno de: " + jugadorTurno.getNombre());
    }

    /**
     * Muestra la operación matemática en la interfaz de usuario.
     * @param op Operación a mostrar.
     */
    private void mostrarOperacion(Operacion op) {
        numero1Operacion.setText(String.valueOf(op.getNum1()));
        numero2Operacion.setText(String.valueOf(op.getNum2()));
        operador.setText(String.valueOf(op.getOperador()));
        campoRespuesta.requestFocus();
    }

    /**
     * Limpia la operación mostrada en la interfaz de usuario.
     */
    private void limpiarOperacion() {
        numero1Operacion.setText("");
        numero2Operacion.setText("");
        operador.setText("");
        campoRespuesta.clear();
        operacionActual = null;
    }

    /**
     * Actualiza la interfaz de usuario con los puntos actuales y el turno.
     */
    private void actualizarInterfaz() {
        etiquetaPuntos1.setText("Puntos: " + jugador1.getPuntos());
        etiquetaPuntos2.setText("Puntos: " + jugador2.getPuntos());
        etiquetaTurno.setText("Turno de: " + jugadorTurno.getNombre());
    }

    /**
     * Muestra un mensaje de error en una ventana emergente.
     * @param mensaje Mensaje de error a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje informativo en una ventana emergente.
     * @param mensaje Mensaje a mostrar.
     */
    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Resultado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de victoria en una ventana emergente.
     * @param ganador Jugador ganador.
     */
    private void mostrarMensajeGanador(Jugador ganador) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Fin del juego");
        alert.setHeaderText(null);
        alert.setContentText("El ganador es: " + ganador.getNombre() + 
                             "\nPuntos: " + ganador.getPuntos());
        alert.showAndWait();
    }
}
