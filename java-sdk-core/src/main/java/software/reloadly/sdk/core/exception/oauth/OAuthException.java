package software.reloadly.sdk.core.exception.oauth;


import org.apache.commons.lang3.StringUtils;
import software.reloadly.sdk.core.exception.APIException;

import java.util.List;

public class OAuthException extends APIException {

    public OAuthException(String message, int httpStatusCode, String path) {
        super(message, httpStatusCode, path);
    }

    public OAuthException(String message, int httpStatusCode, String path, Throwable cause) {
        super(message, httpStatusCode, path, cause);
    }

    public OAuthException(String message, String path, int httpStatusCode, String errorCode) {
        super(message, path, httpStatusCode, errorCode);
    }

    public OAuthException(String message, String path, int httpStatusCode, String errorCode, Throwable cause) {
        super(message, path, httpStatusCode, errorCode, cause);
    }

    public OAuthException(String message, String path, int httpStatusCode, String errorCode, List<Object> details) {
        super(message, path, httpStatusCode, errorCode, details);
    }

    public OAuthException(String message,
                          String path, int httpStatusCode, String errorCode, List<Object> details, Throwable cause) {
        super(message, path, httpStatusCode, errorCode, details, cause);
    }

    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean isExpiredToken() {
        return (StringUtils.isNotBlank(getErrorCode()) && getErrorCode().equalsIgnoreCase("TOKEN_EXPIRED"));
    }
}
