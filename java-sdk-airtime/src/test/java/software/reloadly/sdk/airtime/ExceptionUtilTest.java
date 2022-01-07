package software.reloadly.sdk.airtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.dto.APIError;
import software.reloadly.sdk.core.exception.APIException;
import software.reloadly.sdk.core.exception.oauth.OAuthException;
import software.reloadly.sdk.core.internal.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.Date;

public class ExceptionUtilTest {

    @Test
    public void testConvert1_Happy_Path() {
        String message = "Some error has occurred";
        String path = "/some-api-end-point";
        Date timeStamp = new Date();
        APIError apiError = new APIError(message, path, timeStamp);
        APIException apiException = ExceptionUtil.convert(apiError, 500);
        Assertions.assertNotNull(apiException);
    }

    @Test
    public void testConvert2_Happy_Path() {
        String message = "Some error has occurred";
        String path = "/some-api-end-point";
        Date timeStamp = new Date();
        Throwable throwable = new Throwable(message);
        APIError apiError = new APIError(message, path, timeStamp);
        APIException apiException = ExceptionUtil.convert(apiError, 500, throwable);
        Assertions.assertNotNull(apiException);
    }

    @Test
    public void testConvertReturnsOAuthExceptionType() {
        String message = "Some error has occurred";
        String path = "/oauth/token";
        Date timeStamp = new Date();
        Throwable throwable = new Throwable(message);
        APIError apiError = new APIError(message, path, timeStamp);
        APIException apiException = ExceptionUtil.convert(apiError, 500, throwable);
        Assertions.assertTrue(apiException instanceof OAuthException);
    }

    @Test
    public void testConvertReturnsAdditionalFields() {
        String message = "Some error has occurred";
        String path = "/oauth/token";
        Date timeStamp = new Date();
        Throwable throwable = new Throwable(message);
        APIError apiError = new APIError(message, path, timeStamp);
        apiError.setErrorCode("TOKEN_EXPIRED");
        apiError.setDetails(new ArrayList<>());
        APIException apiException = ExceptionUtil.convert(apiError, 500, throwable);
        Assertions.assertTrue(apiException instanceof OAuthException);
    }
}
