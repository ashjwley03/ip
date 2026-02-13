package boba.exception;

/**
 * Represents exceptions specific to the Boba chatbot.
 * Used for handling user input errors and application-specific error conditions.
 */
public class BobException extends Exception {

    /**
     * Creates a new BobException with the specified message.
     *
     * @param message The error message describing what went wrong.
     */
    public BobException(String message) {
        super(message);
    }
}
