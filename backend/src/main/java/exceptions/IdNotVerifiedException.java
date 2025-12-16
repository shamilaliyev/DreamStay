package exceptions;

public class IdNotVerifiedException extends AuthException {
    public IdNotVerifiedException(String message) {
        super(message);
    }
}
