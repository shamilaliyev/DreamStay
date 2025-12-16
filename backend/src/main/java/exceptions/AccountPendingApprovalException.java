package exceptions;

public class AccountPendingApprovalException extends AuthException {
    public AccountPendingApprovalException(String message) {
        super(message);
    }
}
