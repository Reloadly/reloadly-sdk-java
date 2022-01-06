package software.reloadly.sdk.airtime.operation.unit;

import com.neovisionaries.i18n.CountryCode;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.util.AirtimeAPIMockServer;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Country;
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

public class CountryOperationsTest {

    private static final String PATH = "src/test/resources/country";
    private static final String COUNTRY = PATH + "/country.json";
    private static final String COUNTRY_LIST = PATH + "/country_list.json";

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
    public void testListCountries() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<List<Country>> request = airtimeAPI.countries().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(COUNTRY_LIST, 200);
        List<Country> countries = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", "/countries"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        countries.forEach(this::assertIsValidCountry);
    }

    @Test
    public void testGetByCountryCode() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Country> request = airtimeAPI.countries().getByCode(CountryCode.HT);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(COUNTRY, 200);
        Country country = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(
                recordedRequest, hasMethodAndPath("GET", "/countries/" + CountryCode.HT.getAlpha2()));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidCountry(country);
    }

    @Test
    public void testGetByCodeShouldThrowExceptionWhenCountryCodeIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.countries().getByCode(null));
        Assertions.assertEquals("'Country code' cannot be null!", exception.getMessage());
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
