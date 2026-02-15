import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Main GUI class for the Boba chatbot application using JavaFX.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello from Boba! 🧋"); // Creating a new Label control
        Scene scene = new Scene(helloWorld); // Setting the scene to be our Label

        stage.setTitle("Boba - Your Bubbly Assistant");
        stage.setScene(scene); // Setting the stage to show our scene
        stage.show(); // Render the stage.
    }
}
