package software.reloadly.sdk.authentication.client;

import software.reloadly.sdk.authentication.dto.request.OAuth2ClientCredentialsRequest;
import software.reloadly.sdk.authentication.dto.request.TokenRequest;
import software.reloadly.sdk.core.internal.constant.GrantType;
import software.reloadly.sdk.core.internal.constant.HttpHeader;
import software.reloadly.sdk.core.internal.constant.MediaType;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.internal.util.Asserter;
import lombok.AllArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@AllArgsConstructor
public class OAuth2ClientCredentialsOperation {

    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_GRANT_TYPE = "grant_type";
    private static final String KEY_AUDIENCE = "audience";

    private static final String PATH_OAUTH = "oauth";
    private static final String PATH_TOKEN = "token";

    private final HttpUrl baseUrl;
    private final String clientId;
    private final Service service;
    private final String clientSecret;
    private final OkHttpClient client;

    /**
     * Creates a request to get a Token for the given audience using the 'Client Credentials' grant.
     * <pre>
     * {@code
     * AuthAPI auth = new AuthenticationAPI("B3c6RYhk1v9SbIJcRIOwu62gIUGsnze", "2679NfkaBn62e6w5E8zNEzjr-yWfkaBne");
     * try {
     *      TokenHolder result = auth.requestToken("https://topups.reloadly.com").execute();
     * } catch (APIException e) {
     *      //api error
     * } catch (ReloadlyException e) {
     *     // request error
     * } catch (Exception e) {
     *     // all other errors
     * }
     * }
     * </pre>
     *
     * @return a Request to configure and execute.
     */
    public OAuth2ClientCredentialsRequest getAccessToken() {

        Asserter.assertNotNull(service, "Service");
        String audience = service.getAudience();

        if (!audience.startsWith("https://") || audience.startsWith("http://")) {
            audience = "https://" + audience;
        }

        String url = baseUrl.newBuilder().addPathSegment(PATH_OAUTH).addPathSegment(PATH_TOKEN).build().toString();

        TokenRequest request = new TokenRequest(client, url);
        request.addParameter(KEY_CLIENT_ID, clientId)
                .addParameter(KEY_CLIENT_SECRET, clientSecret)
                .addParameter(KEY_GRANT_TYPE, GrantType.CLIENT_CREDENTIALS)
                .addParameter(KEY_AUDIENCE, audience)
                .addHeader(HttpHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .addHeader(HttpHeader.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        return request;
    }
}
