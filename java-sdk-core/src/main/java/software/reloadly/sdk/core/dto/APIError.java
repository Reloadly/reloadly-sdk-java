package software.reloadly.sdk.core.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Class that represents an Reloadly Server error captured from a http response. Provides different methods to get a clue of why the request failed.
 * i.e.:
 * <pre>
 * {@code
 * {
 *      "details": [],
 *      "errorCode": null
 *      "message": "Invalid operator id provided",
 *      "path": "/operators/68695596",
 *      "timeStamp": 1559108814252,
 * }
 * }
 * </pre>
 */
@Getter
@Setter
@EqualsAndHashCode
public class APIError {


    /**
     * Additional details that might be helpful in understanding the error(s) that occurred.
     */
    private List<Object> details = new ArrayList<>();

    /**
     * For some errors that could be handled programmatically, a string summarizing the error reported.
     */
    private String errorCode;

    /**
     * Link to documentations containing additional info regarding the error.
     */
    private String infoLink;

    /**
     * A human-readable message providing more details about the error.
     */
    private String message;

    /**
     * The end-point that was used when the error occurred
     */
    private String path;

    /**
     * "The timestamp when the usage occurred"
     */
    private Date timeStamp;


    public APIError(String message, String path, Date timeStamp) {
        this.message = message;
        this.path = path;
        this.timeStamp = timeStamp;
    }

    public APIError(String message, String path, Date timeStamp, String errorCode) {
        this.message = message;
        this.path = path;
        this.timeStamp = timeStamp;
        this.errorCode = errorCode;
    }

    public APIError(String message, String path, Date timeStamp, String errorCode, List<Object> details) {
        this.message = message;
        this.path = path;
        this.timeStamp = timeStamp;
        this.errorCode = errorCode;
        this.details = details;
    }

    public APIError(String message, String path, Date timeStamp, String errorCode, List<Object> details, String infoLink) {
        this.message = message;
        this.path = path;
        this.timeStamp = timeStamp;
        this.errorCode = errorCode;
        this.details = details;
        this.infoLink = infoLink;
    }
}
