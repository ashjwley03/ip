package boba.exception;

/**
 * Represents exceptions specific to the Boba chatbot.
 * Used for handling user input errors and application-specific error conditions.
 */
public class BobaException extends Exception {

    /**
     * Creates a new BobaException with the specified message.
     *
     * @param message The error message describing what went wrong.
     */
    public BobaException(String message) {
        super(message);
    }
}
