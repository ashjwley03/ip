import java.io.IOException;

import boba.Boba;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Boba using FXML.
 */
public class Main extends Application {

    private Boba boba = new Boba("./data/boba.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(
                    Main.class.getResource(
                            "/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Boba");
            stage.setMinWidth(350);
            stage.setMinHeight(400);
            fxmlLoader.<MainWindow>getController().setBoba(boba);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
