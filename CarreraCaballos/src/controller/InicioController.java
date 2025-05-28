package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Jugador;

import java.io.IOException;

public class InicioController {
    // Constantes para mensajes de error
    private static final String ERROR_TITULO = "Error";
    private static final String ERROR_CABECERA = "Nombre(s) vacío(s)";
    private static final String ERROR_CONTENIDO = "Por favor, introduce el nombre de ambos jugadores.";
    
    // Constantes para rutas de vistas
    private static final String RUTA_VISTA_JUEGO = "/view/Juego.fxml";
    
    // Constantes para dimensiones de ventana
    private static final double ANCHO_VENTANA = 831;
    private static final double ALTO_VENTANA = 446;

    @FXML
    private TextField campoNombreJugador1;

    @FXML
    private TextField campoNombreJugador2;

    @FXML
    private void Aceptar() {
        String nombre1 = campoNombreJugador1.getText().trim();
        String nombre2 = campoNombreJugador2.getText().trim();

        if (nombre1.isEmpty() || nombre2.isEmpty()) {
            mostrarError();
            return;
        }

        try {
            cargarVistaJuego(nombre1, nombre2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_TITULO);
        alert.setHeaderText(ERROR_CABECERA);
        alert.setContentText(ERROR_CONTENIDO);
        alert.showAndWait();
    }

    private void cargarVistaJuego(String nombre1, String nombre2) throws IOException {
        Jugador jugador1 = new Jugador(nombre1);
        Jugador jugador2 = new Jugador(nombre2);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTA_JUEGO));
        Parent root = loader.load();

        JuegoController controller = loader.getController();
        controller.initData(jugador1, jugador2);

        Stage stage = (Stage) campoNombreJugador1.getScene().getWindow();
        stage.setScene(new Scene(root, ANCHO_VENTANA, ALTO_VENTANA));
        stage.show();
    }
}
