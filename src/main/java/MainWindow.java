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

    private Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/User.png"));
    private Image bobaImage = new Image(
            this.getClass().getResourceAsStream("/images/Boba.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(
                dialogContainer.heightProperty());
        addDialogs(DialogBox.getBobaDialog(
                "Hii! I'm Boba \u25D5\u203F\u25D5\n"
                        + "What can I do for you today?",
                bobaImage));
    }

    /**
     * Injects the Boba instance and shows startup reminders.
     *
     * @param b The Boba chatbot instance.
     */
    public void setBoba(Boba b) {
        boba = b;
        String reminders = boba.getReminders();
        if (!reminders.startsWith("\u2705")) {
            addDialogs(DialogBox.getBobaDialog(
                    reminders, bobaImage));
        }
    }

    private void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }

    private boolean isErrorResponse(String response) {
        return response.startsWith("Hmm")
                || response.startsWith("Invalid")
                || response.startsWith("That's not")
                || response.startsWith("What should");
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = boba.getResponse(input);
        DialogBox bobaBox;
        if (isErrorResponse(response)) {
            bobaBox = DialogBox.getBobaErrorDialog(
                    response, bobaImage);
        } else {
            bobaBox = DialogBox.getBobaDialog(
                    response, bobaImage);
        }
        addDialogs(
                DialogBox.getUserDialog(input, userImage),
                bobaBox
        );
        userInput.clear();
    }
}
