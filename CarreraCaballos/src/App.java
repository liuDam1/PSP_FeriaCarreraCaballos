import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static final String RUTA_INICIO = "/view/Inicio.fxml";
    public static final String RUTA_JUEGO = "/view/Juego.fxml";
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(RUTA_INICIO));
        primaryStage.setScene(new Scene(root, 831, 446));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}    