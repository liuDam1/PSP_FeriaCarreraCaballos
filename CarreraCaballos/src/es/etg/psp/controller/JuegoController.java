package es.etg.psp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

import es.etg.psp.client.Cliente;
import es.etg.psp.model.Carrera;
import es.etg.psp.model.Jugador;
import es.etg.psp.model.Operacion;
import es.etg.psp.util.Certificado;
import es.etg.psp.util.GestionLog;
import es.etg.psp.util.Persistencia;
import es.etg.psp.util.TipoLog;

public class JuegoController {
    // Constantes para textos UI
    private static final String TEXTO_CERO = "0";
    private static final String TEXTO_VACIO = "";
    private static final String FORMATO_PUNTOS_BASE = "%s obtiene %d puntos base";
    private static final String MENSAJE_SIN_OPERACION = "No hay operación este turno. Haz clic en Aceptar para continuar.";
    private static final String MENSAJE_RESPUESTA_CORRECTA = "¡Respuesta correcta! +5 puntos";
    private static final String FORMATO_RESPUESTA_INCORRECTA = "Respuesta incorrecta. Resultado correcto: %d";
    private static final String ERROR_NUMERO_INVALIDO = "Ingrese un número válido";
    private static final String SEPARADOR_JUGADORES = " vs ";

    // Constantes para mensajes de log
    private static final String TITULO_ERROR = "Error";
    private static final String TITULO_INFORMACION = "Información";
    private static final String TITULO_VICTORIA = "¡Victoria!";
    private static final String FORMATO_GANADOR = "%s gana con %d puntos!";
    private static final String ERROR_CONEXION = "Error al conectar con el servidor: ";
    private static final String ERROR_TURNO = "Error en el turno: ";
    private static final String MENSAJE_DESPEDIDA = "Cerrando la aplicación...";
    private static final String ERROR_FINALIZAR_JUEGO = "Error al finalizar el juego: ";
    private static final String HILO_CIERRE_INTERRUMPIDO = "Hilo de cierre interrumpido: ";
    private static final String INICIO_JUEGO = "Iniciando juego con jugadores: ";
    private static final String TURNO_INICIADO = "Turno iniciado para: ";
    private static final String OPERACION_GENERADA = "Operación generada: ";
    private static final String RESPUESTA_RECIBIDA = "Respuesta recibida: ";
    private static final String RESPUESTA_VERIFICADA = "Respuesta verificada - Correcta: ";
    private static final String CAMBIO_TURNO = "Cambiando turno a: ";
    private static final String GANADOR_DETECTADO = "Ganador detectado: ";
    private static final String CERTIFICADO_GENERADO = "Certificado generado para ganador: ";
    private static final String PARTIDA_GUARDADA = "Partida guardada en historial";
    private static final String LOG_JUEGO_INICIADO = "Juego iniciado";
    private static final String LOG_TURNO_SIN_OPERACION = "Turno sin operación";

    // Constantes para puntos
    private static final int PUNTOS_EXTRA = 5;
    private static final int TIEMPO_ESPERA_CIERRE = 3000;

    @FXML
    private Label etiquetaJugador1, etiquetaJugador2;
    @FXML
    private Label etiquetaPuntos1, etiquetaPuntos2;
    @FXML
    private Label numero1Operacion, numero2Operacion, operador, etiquetaTurno, igual;
    @FXML
    private TextField campoRespuesta;
    @FXML
    private Button botonAceptar;
    @FXML
    private Button botonComenzar;

    private Cliente cliente;
    private Carrera juego;
    private Operacion operacionActual;
    private boolean operacionIniciada = false;
    private boolean juegoIniciado = false;

    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            GestionLog.registrar(TipoLog.INFO,
                    INICIO_JUEGO + jugador1.getNombre() + SEPARADOR_JUGADORES + jugador2.getNombre());

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
            GestionLog.registrar(TipoLog.ERROR, ERROR_CONEXION + e.getMessage());
            mostrarError(ERROR_CONEXION + e.getMessage());
        }
    }

    @FXML
    private void ComenzarJuego() {
        if (!juegoIniciado) {
            juegoIniciado = true;
            botonComenzar.setVisible(false);
            mostrarOperacionUI();
            GestionLog.registrar(TipoLog.INFO, LOG_JUEGO_INICIADO);
            jugarTurno();
        }
    }

    private void jugarTurno() {
        try {
            if (!juegoIniciado)
                return;

            Jugador currentPlayer = juego.getTurno();
            GestionLog.registrar(TipoLog.INFO, TURNO_INICIADO + currentPlayer.getNombre());

            int basePoints = juego.getPuntosRonda();
            currentPlayer.sumarPuntos(basePoints);
            mostrarMensaje(String.format(FORMATO_PUNTOS_BASE, currentPlayer.getNombre(), basePoints));
            actualizarPuntos();

            if (juego.hayOperacion()) {
                operacionActual = juego.generarOperacion();
                mostrarOperacion(operacionActual);
                operacionIniciada = true;
                GestionLog.registrar(TipoLog.INFO, OPERACION_GENERADA + operacionActual);
            } else {
                resetearOperacionUI();
                mostrarMensaje(MENSAJE_SIN_OPERACION);
                operacionIniciada = false;
                GestionLog.registrar(TipoLog.INFO, LOG_TURNO_SIN_OPERACION);
            }

            if (juego.hayGanador()) {
                mostrarGanador();
                return;
            }
        } catch (Exception e) {
            GestionLog.registrar(TipoLog.ERROR, ERROR_TURNO + e.getMessage());
            mostrarError(ERROR_TURNO + e.getMessage());
        }
    }

    @FXML
    private void Aceptar() {
        if (!juegoIniciado)
            return;

        if (operacionIniciada) {
            try {
                int respuesta = Integer.parseInt(campoRespuesta.getText());
                GestionLog.registrar(TipoLog.INFO, RESPUESTA_RECIBIDA + respuesta);

                boolean correcta = operacionActual.verificarResultado(respuesta);
                GestionLog.registrar(TipoLog.INFO, RESPUESTA_VERIFICADA + correcta);

                if (correcta) {
                    Jugador currentPlayer = juego.getTurno();
                    currentPlayer.sumarPuntos(PUNTOS_EXTRA);
                    mostrarMensaje(MENSAJE_RESPUESTA_CORRECTA);
                } else {
                    mostrarMensaje(String.format(FORMATO_RESPUESTA_INCORRECTA, operacionActual.getResultado()));
                }

                actualizarPuntos();
            } catch (NumberFormatException e) {
                GestionLog.registrar(TipoLog.WARN, ERROR_NUMERO_INVALIDO);
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
        GestionLog.registrar(TipoLog.INFO, CAMBIO_TURNO + juego.getTurno().getNombre());
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
            try {
                GestionLog.registrar(TipoLog.INFO, GANADOR_DETECTADO + ganador.getNombre());

                Certificado.generarCertificado(ganador, juego.getJugador1(), juego.getJugador2());
                GestionLog.registrar(TipoLog.INFO, CERTIFICADO_GENERADO + ganador.getNombre());

                Persistencia.guardarPartida(juego.getJugador1(), juego.getJugador2(), ganador);
                GestionLog.registrar(TipoLog.INFO, PARTIDA_GUARDADA);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(TITULO_VICTORIA);
                alert.setHeaderText(null);
                alert.setContentText(String.format(FORMATO_GANADOR, ganador.getNombre(), ganador.getPuntos()));
                alert.showAndWait();

                if (cliente != null) {
                    cliente.cerrarConexion();
                }

                GestionLog.registrar(TipoLog.INFO, MENSAJE_DESPEDIDA);

                Thread shutdownThread = new Thread(() -> {
                    try {
                        Thread.sleep(TIEMPO_ESPERA_CIERRE);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        GestionLog.registrar(TipoLog.ERROR, HILO_CIERRE_INTERRUMPIDO + e.getMessage());
                    } finally {
                        Platform.exit();
                        System.exit(0);
                    }
                });

                shutdownThread.setDaemon(true);
                shutdownThread.start();

            } catch (Exception e) {
                GestionLog.registrar(TipoLog.ERROR, ERROR_FINALIZAR_JUEGO + e.getMessage());
                Platform.exit();
                System.exit(1);
            }
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
