package software.reloadly.sdk.airtime.operation.integration;

import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.AccountBalanceInfo;
import software.reloadly.sdk.airtime.interfaces.IntegrationTest;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AccountOperationsTest extends BaseIntegrationTest {

    @IntegrationTest
    public void testGetAccountBalance() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<AccountBalanceInfo> request = airtimeAPI.accounts().getBalance();
        assertThat(request, is(notNullValue()));
        AccountBalanceInfo accountBalance = request.execute();

        assertIsValidAccountBalance(accountBalance);
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
