package software.reloadly.sdk.airtime.client.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Operator;
import software.reloadly.sdk.airtime.util.ExpiredToken;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AirtimeAPITest {

    private static String clientId;
    private static String clientSecret;

    @BeforeAll
    static void beforeAll() {
        clientId = System.getenv("LIVE_CLIENT_ID");
        clientSecret = System.getenv("LIVE_CLIENT_SECRET");
    }

    @Test
    public void testObtainingNewTokenAutomaticallyWhenSpecifiedTokenIsExpiredOrInvalid() throws ReloadlyException {
        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.LIVE)
                .accessToken(ExpiredToken.AIRTIME)
                .build();

        Request<Operator> request = airtimeAPI.operators().getById(operatorId);
        Operator operator = request.execute();
        assertThat(operator, is(notNullValue()));
    }

    @Test
    public void testRefreshToken() throws ReloadlyException {

        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.LIVE)
                .accessToken(ExpiredToken.AIRTIME)
                .build();

        Request<Operator> request = airtimeAPI.operators().getById(operatorId);
        airtimeAPI.refreshAccessToken(request);
        Operator operator = request.execute();
        assertThat(operator, is(notNullValue()));
    }

}
