package exceptions;

public class SquareMatrixException extends RuntimeException{
    public SquareMatrixException() {
    }

    public SquareMatrixException(String message) {
        super(message);
    }

    public SquareMatrixException(String message, Throwable cause) {
        super(message, cause);
    }

    public SquareMatrixException(Throwable cause) {
        super(cause);
    }
}
