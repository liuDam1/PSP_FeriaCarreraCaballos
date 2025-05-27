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
import util.CertificadoGenerator;
import util.Persistencia;

import java.io.IOException;

public class JuegoController {
    @FXML private Label etiquetaJugador1, etiquetaJugador2;
    @FXML private Label etiquetaPuntos1, etiquetaPuntos2;
    @FXML private Label numero1Operacion, numero2Operacion, operador, etiquetaTurno, igual;
    @FXML private TextField campoRespuesta;
    @FXML private Button botonAceptar;
    @FXML private Button botonComenzar; // 新增：开始游戏按钮

    private Cliente cliente;
    private Carrera juego;
    private Operacion operacionActual;
    private boolean operacionTriggered = false;
    private boolean isGameStarted = false; // 新增：游戏是否已开始

    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            this.cliente = new Cliente();
            this.juego = new Carrera(jugador1, jugador2);

            // 初始化UI但不开始游戏
            etiquetaJugador1.setText(jugador1.getNombre());
            etiquetaJugador2.setText(jugador2.getNombre());
            actualizarPuntos();
            etiquetaTurno.setText(juego.getTurno().getNombre());
            
            // 隐藏题目区域，显示开始按钮
            ocultarOperacionUI();
            botonComenzar.setVisible(true);

            cliente.enviarJugadores(jugador1.getNombre(), jugador2.getNombre());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    // 新增：开始游戏按钮的点击事件
    @FXML
    private void handleComenzarJuego() {
        if (!isGameStarted) {
            isGameStarted = true;
            botonComenzar.setVisible(false);
            mostrarOperacionUI();
            jugarTurno();
        }
    }

    private void jugarTurno() {
        try {
            if (!isGameStarted) return; // 确保游戏已开始
            
            Jugador currentPlayer = juego.getTurno();
            int basePoints = juego.getPuntosRonda();
            currentPlayer.sumarPuntos(basePoints);
            mostrarMensaje(currentPlayer.getNombre() + " obtiene " + basePoints + " puntos base");
            actualizarPuntos();

            if (juego.hayOperacion()) {
                operacionActual = juego.generarOperacion();
                mostrarOperacion(operacionActual);
                operacionTriggered = true;
            } else {
                resetOperacionUI();
                mostrarMensaje("No hay operación este turno. Haz clic en Aceptar para continuar.");
                operacionTriggered = false;
            }

            // 每回合结束后检查是否有玩家达到100分
            if (juego.hayGanador()) {
                mostrarGanador();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error en el turno: " + e.getMessage());
        }
    }

    @FXML
    private void handleAceptar() {
        if (!isGameStarted) return; // 确保游戏已开始
        
        if (operacionTriggered) {
            try {
                int respuesta = Integer.parseInt(campoRespuesta.getText());
                boolean correcta = operacionActual.verificarResultado(respuesta);

                if (correcta) {
                    Jugador currentPlayer = juego.getTurno();
                    currentPlayer.sumarPuntos(5);
                    mostrarMensaje("¡Respuesta correcta! +5 puntos");
                } else {
                    mostrarMensaje("Respuesta incorrecta. Resultado correcto: " + operacionActual.getResultado());
                }

                actualizarPuntos();
            } catch (NumberFormatException e) {
                mostrarError("Ingrese un número válido");
                return;
            }
        }

        toggleTurno();
        resetOperacionUI();
        operacionTriggered = false;

        if (juego.hayGanador()) {
            mostrarGanador();
            return;
        }

        jugarTurno();
    }

    private void toggleTurno() {
        juego.cambiarTurno();
        etiquetaTurno.setText(juego.getTurno().getNombre());
    }

    private void mostrarOperacion(Operacion op) {
        numero1Operacion.setText(String.valueOf(op.getNum1()));
        numero2Operacion.setText(String.valueOf(op.getNum2()));
        operador.setText(String.valueOf(op.getOperador()));
        campoRespuesta.requestFocus();
    }

    private void resetOperacionUI() {
        numero1Operacion.setText("0");
        numero2Operacion.setText("0");
        operador.setText("");
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
            // 生成证书和记录对局
            CertificadoGenerator.generarCertificado(ganador, juego.getJugador1(), juego.getJugador2());
            Persistencia.guardarPartida(juego.getJugador1(), juego.getJugador2(), ganador);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Victoria!");
            alert.setHeaderText(null);
            alert.setContentText(ganador.getNombre() + " gana con " + ganador.getPuntos() + " puntos!");
            alert.showAndWait();
            
            cliente.cerrarConexion();
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarMensaje(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
