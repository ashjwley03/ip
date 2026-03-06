import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents a styled chat bubble with a small circular avatar.
 * User and Boba dialogs are visually distinct.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView displayPicture;
    @FXML
    private VBox bubbleBox;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource(
                            "/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        Circle clip = new Circle(18, 18, 18);
        displayPicture.setClip(clip);
    }

    private void applyUserStyle() {
        setAlignment(Pos.TOP_RIGHT);
        nameLabel.setText("You");
        nameLabel.getStyleClass().add("name-user");
        dialog.getStyleClass().add("bubble-user");
        bubbleBox.setAlignment(Pos.TOP_RIGHT);
    }

    private void applyBobaStyle() {
        ObservableList<Node> tmp =
                FXCollections.observableArrayList(
                        this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        nameLabel.setText("Boba");
        nameLabel.getStyleClass().add("name-boba");
        dialog.getStyleClass().add("bubble-boba");
        bubbleBox.setAlignment(Pos.TOP_LEFT);
    }

    private void applyErrorStyle() {
        dialog.getStyleClass().add("bubble-error");
    }

    /**
     * Creates a dialog box for user messages.
     *
     * @param text The user's text.
     * @param img The user's avatar image.
     * @return A DialogBox with user styling.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.applyUserStyle();
        return db;
    }

    /**
     * Creates a dialog box for Boba's responses.
     *
     * @param text Boba's response text.
     * @param img Boba's avatar image.
     * @return A DialogBox with Boba styling.
     */
    public static DialogBox getBobaDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.applyBobaStyle();
        return db;
    }

    /**
     * Creates a dialog box for Boba's error responses.
     *
     * @param text The error text.
     * @param img Boba's avatar image.
     * @return A DialogBox with error styling.
     */
    public static DialogBox getBobaErrorDialog(
            String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.applyBobaStyle();
        db.applyErrorStyle();
        return db;
    }
}
