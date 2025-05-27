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
    @FXML
    private TextField campoNombreJugador1;

    @FXML
    private TextField campoNombreJugador2;

    @FXML
    private void handleAceptar() {
        String nombre1 = campoNombreJugador1.getText().trim();
        String nombre2 = campoNombreJugador2.getText().trim();

        if (nombre1.isEmpty() || nombre2.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nombre(s) vacío(s)");
            alert.setContentText("Por favor, introduce el nombre de ambos jugadores.");
            alert.showAndWait();
            return;
        }

        try {
            Jugador jugador1 = new Jugador(nombre1);
            Jugador jugador2 = new Jugador(nombre2);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Juego.fxml"));
            Parent root = loader.load();

            JuegoController controller = loader.getController();
            controller.initData(jugador1, jugador2);

            Stage stage = (Stage) campoNombreJugador1.getScene().getWindow();
            stage.setScene(new Scene(root, 831, 446));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}    
