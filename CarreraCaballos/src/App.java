import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static final String RUTA_INICIO = "/view/Inicio.fxml";
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar la pantalla de inicio
            Parent root = FXMLLoader.load(getClass().getResource(RUTA_INICIO));
            Scene scene = new Scene(root, 831, 446);
            
            primaryStage.setTitle("Carrera Caballo");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Mensaje útil para diagnóstico
        System.out.println("Iniciando cliente...");
        launch(args);
    }
}
