package software.reloadly.sdk.airtime.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Country;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CountryOperationsTest extends BaseIntegrationTest {

    @Test
    public void testListCountries() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<List<Country>> request = airtimeAPI.countries().list();
        assertThat(request, is(notNullValue()));
        List<Country> countries = request.execute();

        countries.forEach(this::assertIsValidCountry);
    }

    @Test
    public void testGetByCountryCode() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<Country> request = airtimeAPI.countries().getByCode(CountryCode.HT);
        assertThat(request, is(notNullValue()));
        Country country = request.execute();
        assertIsValidCountry(country);
    }

    private void assertIsValidCountry(Country country) {

        List<String> countryFields = Arrays.asList("isoName", "name", "currencyCode", "currencyName", "currencySymbol",
                "flag", "callingCodes"
        );

        countryFields.forEach(field -> assertThat(country, hasProperty(field)));

        assertThat(country.getIsoName(), is(notNullValue()));
        assertThat(country.getName(), is(notNullValue()));
        assertThat(country.getCurrencyCode(), is(notNullValue()));
        assertThat(country.getCurrencyName(), is(notNullValue()));
        assertThat(country.getCurrencySymbol(), is(notNullValue()));
        assertThat(country.getFlag(), is(notNullValue()));
        assertThat(country.getCallingCodes(), is(notNullValue()));
    }
}
