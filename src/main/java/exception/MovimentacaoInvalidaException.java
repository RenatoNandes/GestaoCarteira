package exception;

public class MovimentacaoInvalidaException extends RuntimeException {
    public MovimentacaoInvalidaException(String message) {
        super(message);
    }
}
