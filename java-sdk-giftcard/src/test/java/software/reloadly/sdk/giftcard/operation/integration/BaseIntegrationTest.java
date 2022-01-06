package software.reloadly.sdk.giftcard.operation.integration;

import org.junit.jupiter.api.BeforeAll;
import software.reloadly.sdk.authentication.client.AuthenticationAPI;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.exception.ReloadlyException;

public class BaseIntegrationTest {

    protected static String accessToken;
    protected static String sandboxAccessToken;

    @BeforeAll
    static void beforeAll() throws ReloadlyException {
        String clientId = System.getenv("LIVE_CLIENT_ID");
        String clientSecret = System.getenv("LIVE_CLIENT_SECRET");
        String sandboxClientId = System.getenv("SANDBOX_CLIENT_ID");
        String sandboxClientSecret = System.getenv("SANDBOX_CLIENT_SECRET");

        accessToken = AuthenticationAPI.builder().clientId(clientId)
                .clientSecret(clientSecret)
                .service(Service.GIFTCARD)
                .build().clientCredentials().getAccessToken().execute().getToken();

        sandboxAccessToken = AuthenticationAPI.builder().clientId(sandboxClientId)
                .clientSecret(sandboxClientSecret)
                .service(Service.GIFTCARD_SANDBOX)
                .build().clientCredentials().getAccessToken().execute().getToken();
    }
}