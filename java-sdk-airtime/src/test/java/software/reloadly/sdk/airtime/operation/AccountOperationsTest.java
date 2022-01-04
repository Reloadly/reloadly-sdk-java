package software.reloadly.sdk.airtime.operation;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.AirtimeAPIMockServer;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.AccountBalanceInfo;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.util.RecordedRequestMatcher.hasHeader;
import static software.reloadly.sdk.core.internal.util.RecordedRequestMatcher.hasMethodAndPath;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;

public class AccountOperationsTest {

    private static final String PATH = "src/test/resources/account";
    private static final String ACCOUNT_BALANCE = PATH + "/account_balance.json";
    private static final String INVALID_ACCESS_TOKEN = "som-invalid-access-token";

    private AirtimeAPIMockServer server;

    @BeforeEach
    public void setUp() throws Exception {
        server = new AirtimeAPIMockServer();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void testGetAccountBalance() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<AccountBalanceInfo> request = airtimeAPI.accounts().getBalance();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(ACCOUNT_BALANCE, 200);
        AccountBalanceInfo accountBalance = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", "/accounts/balance"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidAccountBalance(accountBalance);
    }

    @Test
    public void shouldThrowExceptionWhenProvidedAccessTokenIsInvalid() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> AirtimeAPI.builder().
                accessToken(INVALID_ACCESS_TOKEN).build().accounts().getBalance());
        String expected = "Either a valid access token or both client id & client secret must be provided";
        Assertions.assertEquals(expected, exception.getMessage());
    }

    private void assertIsValidAccountBalance(AccountBalanceInfo accountBalance) {

        List<String> countryFields = Arrays.asList("amount", "currencyCode", "currencyName", "updatedAt");
        countryFields.forEach(field -> assertThat(accountBalance, hasProperty(field)));
        assertThat(accountBalance.getAmount(), is(notNullValue()));
        assertThat(accountBalance.getCurrencyCode(), is(notNullValue()));
        assertThat(accountBalance.getCurrencyName(), is(notNullValue()));
        assertThat(accountBalance.getUpdatedAt(), is(notNullValue()));
    }
}
