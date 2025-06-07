package es.etg.psp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import es.etg.psp.model.Jugador;
import es.etg.psp.util.TipoLog;
import es.etg.psp.util.GestionLog;

public class InicioController {
    // Constantes para mensajes de error
    private static final String ERROR_TITULO = "Error";
    private static final String ERROR_CABECERA = "Nombre(s) vacío(s)";
    private static final String ERROR_CONTENIDO = "Por favor, introduce el nombre de ambos jugadores.";

    // Constantes para rutas de vistas
    private static final String RUTA_VISTA_JUEGO = "/es/etg/psp/view/Juego.fxml";

    // Constantes para dimensiones de ventana
    private static final double ANCHO_VENTANA = 831;
    private static final double ALTO_VENTANA = 446;

    // Constantes para mensajes de log
    private static final String LOG_INICIO_ACEPTAR = "Inicio del método Aceptar";
    private static final String LOG_NOMBRES_INTRODUCIDOS = "Nombres introducidos - Jugador1: %s, Jugador2: %s";
    private static final String LOG_NOMBRES_VACIOS = "Intento de inicio con nombres vacíos";
    private static final String LOG_CARGANDO_VISTA = "Cargando vista de juego para jugadores: %s y %s";
    private static final String LOG_ERROR_CARGAR_VISTA = "Error al cargar la vista de juego: %s";
    private static final String LOG_MOSTRANDO_ERROR = "Mostrando diálogo de error por nombres vacíos";
    private static final String LOG_CREANDO_JUGADORES = "Creando jugadores";
    private static final String LOG_CARGANDO_FXML = "Cargando FXML desde: %s";
    private static final String LOG_INICIALIZANDO_CONTROLADOR = "Inicializando controlador de juego con datos de jugadores";
    private static final String LOG_MOSTRANDO_ESCENA = "Mostrando escena de juego";

    @FXML
    private TextField campoNombreJugador1;

    @FXML
    private TextField campoNombreJugador2;

    @FXML
    private void Aceptar() {
        GestionLog.registrar(TipoLog.INFO, LOG_INICIO_ACEPTAR);

        String nombre1 = campoNombreJugador1.getText().trim();
        String nombre2 = campoNombreJugador2.getText().trim();
        GestionLog.registrar(TipoLog.DEBUG, String.format(LOG_NOMBRES_INTRODUCIDOS, nombre1, nombre2));

        if (nombre1.isEmpty() || nombre2.isEmpty()) {
            GestionLog.registrar(TipoLog.WARN, LOG_NOMBRES_VACIOS);
            mostrarError();
            return;
        }

        try {
            GestionLog.registrar(TipoLog.INFO, String.format(LOG_CARGANDO_VISTA, nombre1, nombre2));
            cargarVistaJuego(nombre1, nombre2);
        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, String.format(LOG_ERROR_CARGAR_VISTA, e.getMessage()));
            e.printStackTrace();
        }
    }

    private void mostrarError() {
        GestionLog.registrar(TipoLog.DEBUG, LOG_MOSTRANDO_ERROR);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_TITULO);
        alert.setHeaderText(ERROR_CABECERA);
        alert.setContentText(ERROR_CONTENIDO);
        alert.showAndWait();
    }

    private void cargarVistaJuego(String nombre1, String nombre2) throws IOException {
        GestionLog.registrar(TipoLog.INFO, LOG_CREANDO_JUGADORES);
        Jugador jugador1 = new Jugador(nombre1);
        Jugador jugador2 = new Jugador(nombre2);

        GestionLog.registrar(TipoLog.DEBUG, String.format(LOG_CARGANDO_FXML, RUTA_VISTA_JUEGO));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTA_JUEGO));
        Parent root = loader.load();

        JuegoController controller = loader.getController();
        GestionLog.registrar(TipoLog.DEBUG, LOG_INICIALIZANDO_CONTROLADOR);
        controller.initData(jugador1, jugador2);

        Stage stage = (Stage) campoNombreJugador1.getScene().getWindow();
        stage.setScene(new Scene(root, ANCHO_VENTANA, ALTO_VENTANA));
        GestionLog.registrar(TipoLog.INFO, LOG_MOSTRANDO_ESCENA);
        stage.show();
    }
}
