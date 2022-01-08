package software.reloadly.sdk.airtime.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.GeographicalRechargePlan;
import software.reloadly.sdk.airtime.dto.response.Operator;
import software.reloadly.sdk.airtime.dto.response.OperatorFxRate;
import software.reloadly.sdk.airtime.filter.OperatorFilter;
import software.reloadly.sdk.airtime.interfaces.IntegrationTest;
import software.reloadly.sdk.airtime.interfaces.IntegrationTestWithProxy;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.net.HttpOptions;
import software.reloadly.sdk.core.net.ProxyOptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.airtime.enums.DenominationType.FIXED;
import static software.reloadly.sdk.airtime.enums.DenominationType.RANGE;

public class OperatorOperationsTest extends BaseIntegrationTest {

    @IntegrationTest
    public void testListOperatorsWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<Page<Operator>> request = airtimeAPI.operators().list();
        assertThat(request, is(notNullValue()));
        Page<Operator> operatorsPage = request.execute();
        operatorsPage.getContent().forEach(this::assertIsValidOperator);
    }

    @IntegrationTest
    public void testListOperatorsWithFilters() throws Exception {

        int page = 1;
        int pageSize = 5;
        OperatorFilter filter = new OperatorFilter().withPage(page, pageSize)
                .includePin(false)
                .includeData(false)
                .includeBundles(false)
                .includeSuggestedAmounts(true)
                .includeSuggestedAmountsMap(true)
                .includeFixedDenominationType(true)
                .includeRangeDenominationType(true);

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<Page<Operator>> request = airtimeAPI.operators().list(filter);
        assertThat(request, is(notNullValue()));
        Page<Operator> operatorsPage = request.execute();
        operatorsPage.getContent().forEach(this::assertIsValidOperator);
    }

    @IntegrationTest
    public void testListOperatorsByCountryCodeWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<List<Operator>> request = airtimeAPI.operators().listByCountryCode(CountryCode.HT);
        assertThat(request, is(notNullValue()));
        List<Operator> operators = request.execute();
        operators.forEach(this::assertIsValidOperator);
        operators.forEach(operator -> assertThat(operator.getSuggestedAmountsMap(), is(anEmptyMap())));
    }

    @IntegrationTest
    public void testListOperatorsByCountryCodeWithFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        OperatorFilter filter = new OperatorFilter().includeBundles(false).includeSuggestedAmountsMap(true);
        Request<List<Operator>> request = airtimeAPI.operators().listByCountryCode(CountryCode.HT, filter);
        assertThat(request, is(notNullValue()));
        List<Operator> operators = request.execute();

        operators.forEach(operator -> {
            assertIsValidOperator(operator);
            assertThat(operator.isBundle(), equalTo(false));
            assertThat(operator.getSuggestedAmountsMap(), not(anEmptyMap()));
        });
    }

    @IntegrationTest
    public void testGetOperatorByIdWithNoFilters() throws Exception {

        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<Operator> request = airtimeAPI.operators().getById(operatorId);
        assertThat(request, is(notNullValue()));
        Operator operator = request.execute();
        assertIsValidOperator(operator);
        assertThat(operator.getId(), equalTo(operatorId));
        assertThat(operator.getSuggestedAmountsMap(), is(anEmptyMap()));
    }

    @IntegrationTest
    public void testGetOperatorByIdWithFilters() throws Exception {

        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        OperatorFilter filter = new OperatorFilter().includeSuggestedAmountsMap(true);
        Request<Operator> request = airtimeAPI.operators().getById(operatorId, filter);
        assertThat(request, is(notNullValue()));
        Operator operator = request.execute();
        assertIsValidOperator(operator);
        assertThat(operator.getId(), equalTo(operatorId));
        assertThat(operator.getSuggestedAmountsMap(), not(anEmptyMap()));
    }

    @IntegrationTest
    public void testAutoDetectOperatorWithNoFilters() throws Exception {

        String phone = "+50936377111";
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<Operator> request = airtimeAPI.operators().autoDetect(phone, CountryCode.HT);
        assertThat(request, is(notNullValue()));
        Operator operator = request.execute();
        assertIsValidOperator(operator);
        assertThat(operator.getSuggestedAmountsMap(), anEmptyMap());
    }

    @IntegrationTest
    public void testAutoDetectOperatorWithFilters() throws Exception {

        String phone = "+50936377111";
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        OperatorFilter filter = new OperatorFilter().includeSuggestedAmountsMap(true);
        Request<Operator> request = airtimeAPI.operators().autoDetect(phone, CountryCode.HT, filter);
        assertThat(request, is(notNullValue()));
        Operator operator = request.execute();
        assertIsValidOperator(operator);
        assertThat(operator.getSuggestedAmountsMap(), not(anEmptyMap()));
    }

    @IntegrationTest
    public void testCalculateOperatorFxRate() throws Exception {

        Double amount = 5.00;
        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<OperatorFxRate> request = airtimeAPI.operators().calculateFxRate(operatorId, amount);
        assertThat(request, is(notNullValue()));
        OperatorFxRate operatorFxRate = request.execute();
        assertThat(operatorFxRate.getOperatorId(), notNullValue());
        assertThat(operatorFxRate.getOperatorName(), notNullValue());
        assertThat(operatorFxRate.getFxRate(), notNullValue());
        assertThat(operatorFxRate.getCurrencyCode(), notNullValue());
        assertThat(operatorFxRate.getOperatorId(), equalTo(operatorId));
    }

    @IntegrationTest
    public void testGetOperatorByIdWithGeographicalRechargePlan() throws Exception {

        Long operatorId = 200L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<Operator> request = airtimeAPI.operators().getById(operatorId);
        assertThat(request, is(notNullValue()));
        Operator operator = request.execute();

        assertIsValidOperator(operator);
        assertThat(operator.getId(), equalTo(operatorId));
        assertThat(operator.getGeographicalRechargePlans(), is(notNullValue()));
        assertThat(operator.getGeographicalRechargePlans(), is(not(empty())));

        Set<GeographicalRechargePlan> geoRechargePlans = operator.getGeographicalRechargePlans();
        List<String> geoRechargePlansFields = Arrays.asList("locationCode", "locationName", "fixedAmounts",
                "localAmounts", "fixedAmountsDescriptions", "localFixedAmountsDescriptions");

        geoRechargePlans.forEach(geoRechargePlan -> {
            assertThat(geoRechargePlan.getLocationCode(), is(not(emptyOrNullString())));
            assertThat(geoRechargePlan.getLocationName(), is(not(emptyOrNullString())));
            assertThat(geoRechargePlan.getFixedAmounts(), is(notNullValue()));
            assertThat(geoRechargePlan.getFixedAmounts(), is(not(empty())));
            assertThat(geoRechargePlan.getLocalAmounts(), is(notNullValue()));
            assertThat(geoRechargePlan.getLocalAmounts(), is(not(empty())));
            assertThat(geoRechargePlan.getFixedAmountsDescriptions(), is(notNullValue()));
            assertThat(geoRechargePlan.getFixedAmountsDescriptions(), is(not(anEmptyMap())));
            assertThat(geoRechargePlan.getLocalFixedAmountsDescriptions(), is(notNullValue()));
            assertThat(geoRechargePlan.getLocalFixedAmountsDescriptions(), is(not(anEmptyMap())));
            geoRechargePlansFields.forEach(field -> assertThat(geoRechargePlan, hasProperty(field)));
        });
    }

    @IntegrationTestWithProxy
    public void testRequestWithProxyAuthentication() throws ReloadlyException {

        String host = System.getenv("PROXY_HOST");
        String username = System.getenv("PROXY_USERNAME");
        String password = System.getenv("PROXY_PASSWORD");
        int port = Integer.parseInt(System.getenv("PROXY_PORT"));

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        ProxyOptions options = new ProxyOptions(proxy, username, password.toCharArray());
        AirtimeAPI airtimeAPI = AirtimeAPI.builder()
                .accessToken(accessToken)
                .environment(Environment.LIVE)
                .options(HttpOptions.builder()
                        .readTimeout(Duration.ofSeconds(60))
                        .writeTimeout(Duration.ofSeconds(60))
                        .connectTimeout(Duration.ofSeconds(60))
                        .proxyOptions(options)
                        .build()
                ).build();

        Operator operator = airtimeAPI.operators().getById(174L).execute();
        assertThat(operator, is(notNullValue()));
    }

    @IntegrationTestWithProxy
    public void testRequestWithUnAuthenticatedProxy() {

        String host = System.getenv("PROXY_HOST");
        int port = Integer.parseInt(System.getenv("PROXY_PORT"));

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        ProxyOptions options = new ProxyOptions(proxy);
        AirtimeAPI airtimeAPI = AirtimeAPI.builder()
                .accessToken(accessToken)
                .environment(Environment.LIVE)
                .options(HttpOptions.builder()
                        .readTimeout(Duration.ofSeconds(60))
                        .writeTimeout(Duration.ofSeconds(60))
                        .connectTimeout(Duration.ofSeconds(60))
                        .proxyOptions(options)
                        .build()
                ).build();

        Throwable exception = assertThrows(ReloadlyException.class, () -> airtimeAPI.operators()
                .getById(174L).execute());

        Assertions.assertInstanceOf(IOException.class, exception.getCause());
        String errorMessage = "Failed to execute request";
        String rootErrorMessage = "Failed to authenticate with proxy";
        Assertions.assertEquals(errorMessage, exception.getMessage());
        Assertions.assertEquals(rootErrorMessage, exception.getCause().getMessage());
    }

    private void assertIsValidOperator(Operator operator) {

        List<String> operatorFields = Arrays.asList("id", "name", "bundle", "data", "pinBased", "supportsLocalAmounts",
                "denominationType", "senderCurrencyCode", "senderCurrencySymbol", "destinationCurrencyCode",
                "destinationCurrencySymbol", "internationalDiscount", "localDiscount", "mostPopularInternationalAmount",
                "mostPopularLocalAmount", "country", "fxRate", "suggestedAmounts", "suggestedAmountsMap", "minAmount",
                "maxAmount", "localMinAmount", "localMaxAmount", "fixedAmounts", "localFixedAmounts",
                "fixedAmountsDescriptions", "localFixedAmountsDescriptions", "logoUrls", "promotions",
                "supportsGeographicalRechargePlans", "geographicalRechargePlans");

        operatorFields.forEach(field -> assertThat(operator, hasProperty(field)));

        assertThat(operator.getId(), is(notNullValue()));
        assertThat(operator.getName(), is(notNullValue()));
        assertThat(operator.isBundle(), is(notNullValue()));
        assertThat(operator.isData(), is(notNullValue()));
        assertThat(operator.isPinBased(), is(notNullValue()));
        assertThat(operator.isSupportsLocalAmounts(), is(notNullValue()));
        assertThat(operator.getDenominationType(), is(notNullValue()));
        assertThat(operator.getDenominationType(), anyOf(equalTo(RANGE), equalTo(FIXED)));
        assertThat(operator.getSenderCurrencyCode(), is(notNullValue()));
        assertThat(operator.getSenderCurrencySymbol(), is(notNullValue()));
        assertThat(operator.getDestinationCurrencyCode(), is(notNullValue()));
        assertThat(operator.getDestinationCurrencySymbol(), is(notNullValue()));
        assertThat(operator.getInternationalDiscount(), is(notNullValue()));
        assertThat(operator.getCountry(), is(notNullValue()));
        assertThat(operator.isSupportsGeographicalRechargePlans(), is(notNullValue()));
    }
}
