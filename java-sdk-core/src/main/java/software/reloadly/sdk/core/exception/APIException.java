package software.reloadly.sdk.core.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Class that represents a Reloadly Server error captured from a http response. Provides different methods to get a clue of why the request failed.
 * i.e.:
 * <pre>
 * {@code
 * {
 *      "details": [],
 *      "errorCode": null
 *      "httpStatusCode": 400,
 *      "message": "Invalid operator id provided",
 *      "path": "/operators/68695596",
 *      "timeStamp": 1559108814252,
 * }
 * }
 * </pre>
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class APIException extends ReloadlyException {


    /**
     * Additional details that might be helpful in understanding the error(s) that occurred.
     */
    private List<Object> details = new ArrayList<>();

    /**
     * For some errors that could be handled programmatically, a string summarizing the error reported.
     */
    private String errorCode;

    /**
     * HTTP status indicate whether a specific HTTP request has been successfully completed.
     * Responses are grouped in five classes: informational responses, successful responses,
     * redirects, client errors, and servers errors.
     * See <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html</a>
     */
    private int httpStatusCode;

    /**
     * The end-point that was used when the error occurred
     */
    private String path;

    /**
     * "The timestamp when the usage occurred"
     */
    private Date timeStamp;


    public APIException(String message, int httpStatusCode, String path) {
        super(message);
        this.path = path;
        this.httpStatusCode = httpStatusCode;
        timeStamp = new Date();
    }

    public APIException(String message, int httpStatusCode, String path, Throwable cause) {
        super(message, cause);
        this.path = path;
        this.httpStatusCode = httpStatusCode;
    }

    public APIException(String message, String path, int httpStatusCode, String errorCode) {
        super(message);
        this.path = path;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
    }

    public APIException(String message, String path, int httpStatusCode, String errorCode, Throwable cause) {
        super(message, cause);
        this.path = path;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
    }

    public APIException(String message, String path, int httpStatusCode, String errorCode, List<Object> details) {
        super(message);
        this.path = path;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.details = details;
    }

    public APIException(String message, String path, int httpStatusCode, String errorCode, List<Object> details, Throwable cause) {
        super(message, cause);
        this.path = path;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.details = details;
    }

    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }
}
