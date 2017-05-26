package excel2db;

/**
 * Created by Andrey on 5/26/2017.
 */

public class ApplicationException extends Exception {
    static final long serialVersionUID = 1L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
