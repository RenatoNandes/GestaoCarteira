package exception;

public class QuantidadeInsuficienteException extends IllegalArgumentException {
    public QuantidadeInsuficienteException(String msg) {
        super(msg);
    }
}