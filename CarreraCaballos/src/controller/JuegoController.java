package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Carrera;
import model.Jugador;
import model.Operacion;

public class JuegoController {
    @FXML
    private Label etiquetaJugador1;
    
    @FXML
    private Label etiquetaJugador2;
    
    @FXML
    private Label etiquetaPuntos1;
    
    @FXML
    private Label etiquetaPuntos2;
    
    @FXML
    private Label numero1Operacion;
    
    @FXML
    private Label numero2Operacion;
    
    @FXML
    private Label operador;
    
    @FXML
    private Label igual;
    
    @FXML
    private TextField campoRespuesta;
    
    @FXML
    private Label etiquetaTurno;
    
    private Carrera carrera;
    private Operacion operacionActual;
    
    public void initData(Jugador jugador1, Jugador jugador2) {
        carrera = new Carrera(jugador1, jugador2);
        
        etiquetaJugador1.setText(jugador1.getNombre());
        etiquetaJugador2.setText(jugador2.getNombre());
        actualizarPuntuaciones();
        actualizarEtiquetaTurno();
        
        numero1Operacion.setVisible(false);
        numero2Operacion.setVisible(false);
        operador.setVisible(false);
        igual.setVisible(false);
        campoRespuesta.setVisible(false);
    }
    
    private void actualizarPuntuaciones() {
        etiquetaPuntos1.setText(String.valueOf(carrera.getJugador1().getPuntos()));
        etiquetaPuntos2.setText(String.valueOf(carrera.getJugador2().getPuntos()));
    }
    
    private void actualizarEtiquetaTurno() {
        etiquetaTurno.setText(carrera.getTurno().getNombre());
    }
    
    @FXML
    private void handleAceptar() {
        Jugador jugadorActual = carrera.getTurno();
        int puntosBase = carrera.getPuntosRonda();
        
        if (operacionActual != null) {
            try {
                int respuesta = Integer.parseInt(campoRespuesta.getText());
                int puntos = puntosBase;
                
                if (operacionActual.verificarResultado(respuesta)) {
                    puntos += 5;
                }
                
                jugadorActual.sumarPuntos(puntos);
                actualizarPuntuaciones();
                
                if (carrera.hayGanador()) {
                    mostrarMensajeGanador();
                    return;
                }
                
                operacionActual = null;
                numero1Operacion.setVisible(false);
                numero2Operacion.setVisible(false);
                operador.setVisible(false);
                igual.setVisible(false);
                campoRespuesta.setVisible(false);
                campoRespuesta.setText("");
                
                carrera.cambiarTurno();
                actualizarEtiquetaTurno();
            } catch (NumberFormatException e) {
                campoRespuesta.setText("");
            }
        } else {
            if (carrera.hayOperacion()) {
                operacionActual = carrera.generarOperacion();
                
                numero1Operacion.setText(String.valueOf(operacionActual.getNum1()));
                numero2Operacion.setText(String.valueOf(operacionActual.getNum2()));
                operador.setText(String.valueOf(operacionActual.getOperador()));
                
                numero1Operacion.setVisible(true);
                numero2Operacion.setVisible(true);
                operador.setVisible(true);
                igual.setVisible(true);
                campoRespuesta.setVisible(true);
                campoRespuesta.requestFocus();
            } else {
                jugadorActual.sumarPuntos(puntosBase);
                actualizarPuntuaciones();
                
                if (carrera.hayGanador()) {
                    mostrarMensajeGanador();
                    return;
                }
                
                carrera.cambiarTurno();
                actualizarEtiquetaTurno();
            }
        }
    }
    
    private void mostrarMensajeGanador() {
        Jugador ganador = carrera.getGanador();
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Fin del juego");
        alert.setHeaderText("¡Tenemos un ganador!");
        alert.setContentText("El jugador " + ganador.getNombre() + " ha ganado con " + 
                             ganador.getPuntos() + " puntos.");
        
        alert.showAndWait();
    }
}    
