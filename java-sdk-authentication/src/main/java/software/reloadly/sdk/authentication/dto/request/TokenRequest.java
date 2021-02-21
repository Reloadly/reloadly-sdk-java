package software.reloadly.sdk.authentication.dto.request;

import com.fasterxml.jackson.core.type.TypeReference;
import software.reloadly.sdk.authentication.dto.response.TokenHolder;
import software.reloadly.sdk.core.internal.dto.request.CustomRequest;
import okhttp3.OkHttpClient;

public class TokenRequest extends CustomRequest<TokenHolder> implements OAuth2ClientCredentialsRequest {

    public TokenRequest(OkHttpClient client, String url) {
        super(client, url, "POST", new TypeReference<TokenHolder>() {
        });
    }

    @Override
    public TokenRequest setAudience(String audience) {
        super.addParameter("audience", audience);
        return this;
    }
}
