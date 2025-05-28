package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import client.Cliente;
import model.Carrera;
import model.Jugador;
import model.Operacion;
import util.Certificado;
import util.Persistencia;

import java.io.IOException;

public class JuegoController {
    // Constantes para textos UI
    private static final String TEXTO_CERO = "0";
    private static final String TEXTO_VACIO = "";
    private static final String FORMATO_PUNTOS_BASE = "%s obtiene %d puntos base";
    private static final String MENSAJE_SIN_OPERACION = "No hay operación este turno. Haz clic en Aceptar para continuar.";
    private static final String MENSAJE_RESPUESTA_CORRECTA = "¡Respuesta correcta! +5 puntos";
    private static final String FORMATO_RESPUESTA_INCORRECTA = "Respuesta incorrecta. Resultado correcto: %d";
    private static final String ERROR_NUMERO_INVALIDO = "Ingrese un número válido";
    
    // Constantes para mensajes de alerta
    private static final String TITULO_ERROR = "Error";
    private static final String TITULO_INFORMACION = "Información";
    private static final String TITULO_VICTORIA = "¡Victoria!";
    private static final String FORMATO_GANADOR = "%s gana con %d puntos!";
    private static final String ERROR_CONEXION = "Error al conectar con el servidor: ";
    private static final String ERROR_TURNO = "Error en el turno: ";
    
    // Constantes para puntos
    private static final int PUNTOS_EXTRA = 5;

    @FXML private Label etiquetaJugador1, etiquetaJugador2;
    @FXML private Label etiquetaPuntos1, etiquetaPuntos2;
    @FXML private Label numero1Operacion, numero2Operacion, operador, etiquetaTurno, igual;
    @FXML private TextField campoRespuesta;
    @FXML private Button botonAceptar;
    @FXML private Button botonComenzar;

    private Cliente cliente;
    private Carrera juego;
    private Operacion operacionActual;
    private boolean operacionIniciada = false;
    private boolean juegoIniciado = false;

    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            this.cliente = new Cliente();
            this.juego = new Carrera(jugador1, jugador2);

            etiquetaJugador1.setText(jugador1.getNombre());
            etiquetaJugador2.setText(jugador2.getNombre());
            actualizarPuntos();
            etiquetaTurno.setText(juego.getTurno().getNombre());
            
            ocultarOperacionUI();
            botonComenzar.setVisible(true);

            cliente.enviarJugadores(jugador1.getNombre(), jugador2.getNombre());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError(ERROR_CONEXION + e.getMessage());
        }
    }

    @FXML
    private void ComenzarJuego() {
        if (!juegoIniciado) {
            juegoIniciado = true;
            botonComenzar.setVisible(false);
            mostrarOperacionUI();
            jugarTurno();
        }
    }

    private void jugarTurno() {
        try {
            if (!juegoIniciado) return;
            
            Jugador currentPlayer = juego.getTurno();
            int basePoints = juego.getPuntosRonda();
            currentPlayer.sumarPuntos(basePoints);
            mostrarMensaje(String.format(FORMATO_PUNTOS_BASE, currentPlayer.getNombre(), basePoints));
            actualizarPuntos();

            if (juego.hayOperacion()) {
                operacionActual = juego.generarOperacion();
                mostrarOperacion(operacionActual);
                operacionIniciada = true;
            } else {
                resetearOperacionUI();
                mostrarMensaje(MENSAJE_SIN_OPERACION);
                operacionIniciada = false;
            }

            if (juego.hayGanador()) {
                mostrarGanador();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError(ERROR_TURNO + e.getMessage());
        }
    }

    @FXML
    private void Aceptar() {
        if (!juegoIniciado) return;
        
        if (operacionIniciada) {
            try {
                int respuesta = Integer.parseInt(campoRespuesta.getText());
                boolean correcta = operacionActual.verificarResultado(respuesta);

                if (correcta) {
                    Jugador currentPlayer = juego.getTurno();
                    currentPlayer.sumarPuntos(PUNTOS_EXTRA);
                    mostrarMensaje(MENSAJE_RESPUESTA_CORRECTA);
                } else {
                    mostrarMensaje(String.format(FORMATO_RESPUESTA_INCORRECTA, operacionActual.getResultado()));
                }

                actualizarPuntos();
            } catch (NumberFormatException e) {
                mostrarError(ERROR_NUMERO_INVALIDO);
                return;
            }
        }

        cambiarTurno();
        resetearOperacionUI();
        operacionIniciada = false;

        if (juego.hayGanador()) {
            mostrarGanador();
            return;
        }

        jugarTurno();
    }

    private void cambiarTurno() {
        juego.cambiarTurno();
        etiquetaTurno.setText(juego.getTurno().getNombre());
    }

    private void mostrarOperacion(Operacion op) {
        numero1Operacion.setText(String.valueOf(op.getNum1()));
        numero2Operacion.setText(String.valueOf(op.getNum2()));
        operador.setText(String.valueOf(op.getOperador()));
        campoRespuesta.requestFocus();
    }

    private void resetearOperacionUI() {
        numero1Operacion.setText(TEXTO_CERO);
        numero2Operacion.setText(TEXTO_CERO);
        operador.setText(TEXTO_VACIO);
        campoRespuesta.clear();
    }

    private void ocultarOperacionUI() {
        numero1Operacion.setVisible(false);
        numero2Operacion.setVisible(false);
        operador.setVisible(false);
        igual.setVisible(false);
        campoRespuesta.setVisible(false);
        botonAceptar.setVisible(false);
    }

    private void mostrarOperacionUI() {
        numero1Operacion.setVisible(true);
        numero2Operacion.setVisible(true);
        operador.setVisible(true);
        igual.setVisible(true);
        campoRespuesta.setVisible(true);
        botonAceptar.setVisible(true);
    }

    private void actualizarPuntos() {
        etiquetaPuntos1.setText(String.valueOf(juego.getJugador1().getPuntos()));
        etiquetaPuntos2.setText(String.valueOf(juego.getJugador2().getPuntos()));
    }

    private void mostrarGanador() {
        Jugador ganador = juego.getGanador();
        if (ganador != null) {
            Certificado.generarCertificado(ganador, juego.getJugador1(), juego.getJugador2());
            Persistencia.guardarPartida(juego.getJugador1(), juego.getJugador2(), ganador);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(TITULO_VICTORIA);
            alert.setHeaderText(null);
            alert.setContentText(String.format(FORMATO_GANADOR, ganador.getNombre(), ganador.getPuntos()));
            alert.showAndWait();
            
            cliente.cerrarConexion();
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(TITULO_ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarMensaje(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(TITULO_INFORMACION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
