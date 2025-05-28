package es.etg.psp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    // Constantes para rutas y configuración
    public static final String RUTA_INICIO = "/es/etg/psp/view/Inicio.fxml";
    private static final String TITULO_VENTANA = "Carrera de Caballos";
    private static final double ANCHO_VENTANA = 831;
    private static final double ALTO_VENTANA = 446;
    
    // Constantes para mensajes
    private static final String MENSAJE_INICIO_CLIENTE = "Iniciando cliente...";
    private static final String ERROR_INICIO_APLICACION = "Error al iniciar la aplicación: ";

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(RUTA_INICIO));
            Scene scene = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);

            primaryStage.setTitle(TITULO_VENTANA);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println(ERROR_INICIO_APLICACION + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(MENSAJE_INICIO_CLIENTE);
        launch(args);
    }
}
