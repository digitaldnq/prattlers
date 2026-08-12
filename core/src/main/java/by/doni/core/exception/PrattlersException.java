package by.doni.core.exception;

public class PrattlersException extends RuntimeException{
    public PrattlersException() {
    }

    public PrattlersException(String message) {
        super(message);
    }

    public PrattlersException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrattlersException(Throwable cause) {
        super(cause);
    }

    public PrattlersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
