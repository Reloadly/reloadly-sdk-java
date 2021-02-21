package software.reloadly.sdk.authentication.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import software.reloadly.sdk.authentication.client.AuthenticationAPI;

/**
 * Class that contains the Tokens obtained after a call to the {@link AuthenticationAPI} methods.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenHolder {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    /**
     * Getter for the Reloadly's access token.
     *
     * @return the access token or null if missing.
     */
    public String getToken() {
        return accessToken;
    }

    /**
     * Getter for the token type.
     *
     * @return the token type or null if missing.
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Getter for the expiration time ('exp' claim) of the recently issued token.
     *
     * @return the expiration time or null if missing.
     */
    public long getExpiresIn() {
        return expiresIn;
    }
}
