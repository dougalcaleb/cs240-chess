package exception;

public class RequestError extends RuntimeException {
    public int status = -1;

    public RequestError(int status, String message) {
        super(message);

        this.status = status;
    }
}
