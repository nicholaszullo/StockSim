package homemadejson.support;

/**
 * General parsing runtime exception
 */

public class ParserException extends RuntimeException {


    private static final long serialVersionUID = 8426837970131421096L;

    public ParserException() {
        super("");
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
