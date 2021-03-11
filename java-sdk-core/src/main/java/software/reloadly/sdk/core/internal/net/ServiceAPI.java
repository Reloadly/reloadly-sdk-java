package software.reloadly.sdk.core.internal.net;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.core.net.HttpOptions;

import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public abstract class ServiceAPI extends API {

    protected String accessToken;
    @Getter(AccessLevel.NONE)
    protected boolean cacheAccessToken;

    public ServiceAPI(String clientId, String clientSecret, String accessToken,
                      boolean enableLogging, List<String> redactHeaders, HttpOptions options,
                      Boolean enableTelemetry, String sdkVersion, @Nullable String apiVersion) {

        super(clientId, clientSecret, enableLogging, redactHeaders, options, enableTelemetry, sdkVersion, apiVersion);

        if (StringUtils.isNotBlank(accessToken)) {
            this.accessToken = validateAccessToken(accessToken);
            if (StringUtils.isNotBlank(this.accessToken)) {
                this.cacheAccessToken = true;
            }
        }
    }

    /**
     * Retrieve a new API access token to use on new calls.
     * This is useful when the token is about to expire or already has.
     *
     * @param request - The request to refresh the token for
     * @throws ReloadlyException - Error captured when executing an http request to the Reloadly Server
     */
    public abstract void refreshAccessToken(Request<?> request) throws ReloadlyException;

    public boolean isAccessTokenCached() {
        return cacheAccessToken;
    }

    protected void validateCredentials() {

        if (isBlank(accessToken) && isBlank(clientId) && isBlank(clientSecret)) {
            throw new IllegalArgumentException(
                    "Either a valid access token or both client id & client secret must be provided"
            );
        } else if (isBlank(accessToken)) {
            Asserter.assertNotNull(clientId, "Client id");
            Asserter.assertNotNull(clientSecret, "Client secret");
        } else if (isBlank(clientId) && isBlank(clientSecret)) {
            Asserter.assertNotNull(accessToken, "Access token");
        }
    }

    /**
     * Update the API access token to use on new calls.
     * This is useful when the token is about to expire or already has.
     *
     * @param accessToken the token to authenticate the calls with.
     */
    protected void setAccessToken(String accessToken) {
        Asserter.assertNotNull(accessToken, "Access token");
        this.accessToken = validateAccessToken(accessToken);
    }

    @Nullable
    private String validateAccessToken(String accessToken) {

        DecodedJWT decodedToken;
        try {
            decodedToken = JWT.decode(accessToken);
        } catch (Exception e) {
            return null;
        }

        Date expirationDate = decodedToken.getExpiresAt();

        Date now = new Date();
        long timeLeftInSeconds = (expirationDate.getTime() - now.getTime()) / 1000;

        //Returns true if comparison is 0 or negative or about to expire in 5 minutes or less
        return (expirationDate.compareTo(now) <= 0 || (timeLeftInSeconds <= 300)) ? null : accessToken;
    }
}
