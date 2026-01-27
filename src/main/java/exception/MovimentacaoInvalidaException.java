package exception;

public class MovimentacaoInvalidaException extends RuntimeException {
    public MovimentacaoInvalidaException(String msg) {
        super(msg);
    }
}
