import boba.Boba;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Boba boba;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image bobaImage = new Image(this.getClass().getResourceAsStream("/images/Boba.png"));

    /**
     * Initializes the controller. Binds the scroll pane to auto-scroll
     * and shows a welcome message from Boba.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getBobaDialog(
                        "Hii! I'm Boba \u25D5\u203F\u25D5\nWhat can I do for you today?",
                        bobaImage));
    }

    /**
     * Injects the Boba instance.
     *
     * @param b The Boba chatbot instance.
     */
    public void setBoba(Boba b) {
        boba = b;
    }

    /**
     * Adds one or more dialog boxes to the dialog container.
     *
     * @param dialogs The dialog boxes to add.
     */
    private void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Boba's reply, and then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = boba.getResponse(input);
        addDialogs(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBobaDialog(response, bobaImage)
        );
        userInput.clear();
    }
}
