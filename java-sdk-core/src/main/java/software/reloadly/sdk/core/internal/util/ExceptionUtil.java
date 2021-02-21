package software.reloadly.sdk.core.internal.util;

import software.reloadly.sdk.core.dto.APIError;
import software.reloadly.sdk.core.exception.APIException;
import software.reloadly.sdk.core.exception.oauth.OAuthException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Getter
@Setter
@EqualsAndHashCode
public class ExceptionUtil {

    public static APIException convert(APIError apiError, int httpStatusCode) {
        return setAdditionalFields(apiError, doGetApiException(apiError, httpStatusCode));
    }

    public static APIException convert(APIError apiError, int httpStatusCode, Throwable cause) {
        return setAdditionalFields(apiError, doGetApiException(apiError, httpStatusCode, cause));
    }

    private static APIException doGetApiException(APIError apiError, int httpStatusCode) {
        if (isAuthenticationError(apiError)) {
            return new OAuthException(apiError.getMessage(), httpStatusCode, apiError.getPath());
        }
        return new APIException(apiError.getMessage(), httpStatusCode, apiError.getPath());
    }

    private static APIException doGetApiException(APIError apiError, int httpStatusCode, Throwable cause) {
        if (isAuthenticationError(apiError)) {
            return new OAuthException(apiError.getMessage(), httpStatusCode, apiError.getPath(), cause);
        }
        return new APIException(apiError.getMessage(), httpStatusCode, apiError.getPath(), cause);
    }

    private static APIException setAdditionalFields(APIError apiError, APIException apiException) {
        if (apiError.getErrorCode() != null && !apiError.getErrorCode().trim().isEmpty()) {
            apiException.setErrorCode(apiError.getErrorCode());
        }

        if (apiError.getTimeStamp() != null) {
            apiException.setTimeStamp(apiError.getTimeStamp());
        }

        if (apiError.getDetails() != null) {
            apiException.setDetails(apiError.getDetails());
        }

        return apiException;
    }

    private static boolean isAuthenticationError(APIError apiError) {
        return isNotBlank(apiError.getPath()) && apiError.getPath().equalsIgnoreCase("/oauth/token");
    }
}
