package software.reloadly.sdk.authentication.operation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.authentication.client.AuthenticationAPI;
import software.reloadly.sdk.authentication.dto.request.OAuth2ClientCredentialsRequest;
import software.reloadly.sdk.authentication.dto.response.TokenHolder;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.exception.ReloadlyException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class OAuth2ClientCredentialOperationsTest {

    private static String liveClientId;
    private static String sandboxClientId;
    private static String liveClientSecret;
    private static String sandboxClientSecret;
    private static final String CLAIM_SANDBOX = "https://reloadly.com/sandbox";
    private static final String CLAIM_USER_ID = "https://reloadly.com/prepaidUserId";
    private static final List<String> giftcardScopes = Collections.singletonList("developer");
    private static final List<String> airtimeScopes = Arrays.asList("send-topups", "read-operators", "read-promotions",
            "read-topups-history", "read-prepaid-balance", "read-prepaid-commissions");


    @BeforeAll
    static void beforeAll() {
        liveClientId = System.getenv("LIVE_CLIENT_ID");
        sandboxClientId = System.getenv("SANDBOX_CLIENT_ID");
        liveClientSecret = System.getenv("LIVE_CLIENT_SECRET");
        sandboxClientSecret = System.getenv("SANDBOX_CLIENT_SECRET");
    }

    @Test
    public void testGetAccessTokenForAirtimeServiceLive() throws ReloadlyException {

        AuthenticationAPI api = AuthenticationAPI.builder().clientId(liveClientId)
                .clientSecret(liveClientSecret)
                .service(Service.AIRTIME)
                .build();

        OAuth2ClientCredentialsRequest request = api.clientCredentials().getAccessToken();
        assertThat(request, is(notNullValue()));
        TokenHolder tokenHolder = request.execute();
        assertValidToken(tokenHolder, Service.AIRTIME, airtimeScopes, false);
    }

    @Test
    public void testGetAccessTokenForAirtimeServiceSandbox() throws ReloadlyException {

        AuthenticationAPI api = AuthenticationAPI.builder().clientId(sandboxClientId)
                .clientSecret(sandboxClientSecret)
                .service(Service.AIRTIME_SANDBOX)
                .build();

        OAuth2ClientCredentialsRequest request = api.clientCredentials().getAccessToken();
        assertThat(request, is(notNullValue()));
        TokenHolder tokenHolder = request.execute();
        assertValidToken(tokenHolder, Service.AIRTIME_SANDBOX, airtimeScopes, true);

    }

    @Test
    public void testGetAccessTokenForGiftCardServiceLive() throws ReloadlyException {

        AuthenticationAPI api = AuthenticationAPI.builder().clientId(liveClientId)
                .clientSecret(liveClientSecret)
                .service(Service.GIFTCARD)
                .build();

        OAuth2ClientCredentialsRequest request = api.clientCredentials().getAccessToken();
        assertThat(request, is(notNullValue()));
        TokenHolder tokenHolder = request.execute();
        assertValidToken(tokenHolder, Service.GIFTCARD, giftcardScopes, false);
    }

    @Test
    public void testGetAccessTokenForGiftCardServiceSandbox() throws ReloadlyException {

        AuthenticationAPI api = AuthenticationAPI.builder().clientId(sandboxClientId)
                .clientSecret(sandboxClientSecret)
                .service(Service.GIFTCARD_SANDBOX)
                .build();

        OAuth2ClientCredentialsRequest request = api.clientCredentials().getAccessToken();
        assertThat(request, is(notNullValue()));
        TokenHolder tokenHolder = request.execute();
        assertValidToken(tokenHolder, Service.GIFTCARD_SANDBOX, giftcardScopes, true);

    }

    private void assertValidToken(TokenHolder tokenHolder,
                                  Service service, List<String> expectedScopes, boolean isSandbox) {

        String expectedTokenType = "Bearer";
        assertThat(tokenHolder, is(notNullValue()));
        assertThat(tokenHolder.getTokenType(), is(not(emptyOrNullString())));
        assertEquals(expectedTokenType, tokenHolder.getTokenType());
        assertThat(tokenHolder.getExpiresIn(), is(greaterThan(0L)));
        assertThat(tokenHolder.getToken(), is(not(emptyOrNullString())));

        DecodedJWT decodedToken = JWT.decode(tokenHolder.getToken());
        assertThat(decodedToken, is(notNullValue()));
        assertTrue(decodedToken.getAudience().contains(service.getServiceAudience()) ||
                decodedToken.getAudience().contains(service.getServiceAudience() + "/"));

        Claim userIdClaim = decodedToken.getClaim(CLAIM_USER_ID);
        Claim sandboxClaim = decodedToken.getClaim(CLAIM_SANDBOX);
        Claim scopeClaim = decodedToken.getClaim("scope");

        assertThat(scopeClaim, is(notNullValue()));
        assertThat(userIdClaim, is(notNullValue()));
        assertThat(sandboxClaim, is(notNullValue()));

        assertThat(userIdClaim.asString(), is(not(emptyOrNullString())));
        assertTrue(StringUtils.isNumeric(userIdClaim.asString()));
        assertEquals(isSandbox, sandboxClaim.asBoolean());
        Arrays.stream(scopeClaim.asString().split(" ")).forEach(s -> assertTrue(expectedScopes.contains(s)));
    }
}
