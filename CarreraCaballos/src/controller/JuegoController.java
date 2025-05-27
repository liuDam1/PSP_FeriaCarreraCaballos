package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import client.Cliente;
import model.Carrera;
import model.Jugador;
import model.Operacion;

import java.io.IOException;

public class JuegoController {
    @FXML
    private Label etiquetaJugador1, etiquetaJugador2;
    @FXML
    private Label etiquetaPuntos1, etiquetaPuntos2;
    @FXML
    private Label numero1Operacion, numero2Operacion, operador, etiquetaTurno;
    @FXML
    private TextField campoRespuesta;

    private Cliente cliente;
    private Carrera juego;
    private Operacion operacionActual;
    private boolean operacionTriggered = false;

    public void initData(Jugador jugador1, Jugador jugador2) {
        try {
            this.cliente = new Cliente();
            this.juego = new Carrera(jugador1, jugador2);

            etiquetaJugador1.setText(jugador1.getNombre());
            etiquetaJugador2.setText(jugador2.getNombre());
            actualizarPuntos();
            etiquetaTurno.setText(juego.getTurno().getNombre());

            cliente.enviarJugadores(jugador1.getNombre(), jugador2.getNombre());
            jugarTurno();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void jugarTurno() {
        try {
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
                // 确保显示当前回合玩家
                etiquetaTurno.setText(currentPlayer.getNombre());
            }

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

                actualizarPuntos(); // 更新分数显示
            } catch (NumberFormatException e) {
                mostrarError("Ingrese un número válido");
                return;
            }
        }

        // 统一处理回合切换（核心修复点）
        toggleTurno(); // 触发随机切换回合（50%概率保持当前玩家）
        resetOperacionUI(); // 清空题目显示
        operacionTriggered = false; // 重置题目状态

        if (juego.hayGanador()) {
            mostrarGanador();
            return;
        }

        jugarTurno(); // 生成下一回合的基础分数
    }

    private void toggleTurno() {
        juego.cambiarTurno(); // 模型层处理随机切换
        etiquetaTurno.setText(juego.getTurno().getNombre()); // 更新UI
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

    private void actualizarPuntos() {
        etiquetaPuntos1.setText(String.valueOf(juego.getJugador1().getPuntos()));
        etiquetaPuntos2.setText(String.valueOf(juego.getJugador2().getPuntos()));
    }

    private void mostrarGanador() {
        Jugador ganador = juego.getGanador();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Victoria!");
        alert.setHeaderText(null);
        alert.setContentText(ganador.getNombre() + " gana con " + ganador.getPuntos() + " puntos!");
        alert.showAndWait();
        cliente.cerrarConexion();
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
