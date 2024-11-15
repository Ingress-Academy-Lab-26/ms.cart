package org.ingress.cartms.exception;

public class DataAccessException extends RuntimeException {

    /**
     * Constructs a new DataAccessException with {@code null} as its detail message.
     */
    public DataAccessException() {
        super();
    }

    /**
     * Constructs a new DataAccessException with the specified detail message.
     *
     * @param message the detail message.
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataAccessException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataAccessException with the specified cause.
     *
     * @param cause the cause of the exception.
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
