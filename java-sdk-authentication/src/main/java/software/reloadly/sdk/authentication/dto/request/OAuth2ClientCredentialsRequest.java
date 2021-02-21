package software.reloadly.sdk.authentication.dto.request;


import software.reloadly.sdk.authentication.dto.response.TokenHolder;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

/**
 * Class that represents an OAuth 2.0 Authentication/Authorization request. The execution will return a {@link TokenHolder}.
 */
public interface OAuth2ClientCredentialsRequest extends Request<TokenHolder> {

    /**
     * Setter for the audience value to request
     *
     * @param audience the audience to request
     * @return this request instance.
     */
    OAuth2ClientCredentialsRequest setAudience(String audience);
}
