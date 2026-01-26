package exception;

public class QuantidadeInvalidaException extends IllegalArgumentException {
    public QuantidadeInvalidaException(String msg) {
        super(msg);
    }
}