import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        new PaneOrganizer(stage);
        stage.setTitle("FPS Shooter Practice");
        stage.show();
    }

    /*
     * Mainline
     */
    public static void main(String[] argv) {
        launch(argv);
    }
}