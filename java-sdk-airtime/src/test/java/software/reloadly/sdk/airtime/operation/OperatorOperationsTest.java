package software.reloadly.sdk.airtime.operation;

import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.AirtimeAPIMockServer;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Operator;
import software.reloadly.sdk.airtime.dto.response.OperatorFxRate;
import software.reloadly.sdk.airtime.filter.OperatorFilter;
import software.reloadly.sdk.airtime.enums.DenominationType;
import software.reloadly.sdk.airtime.util.RecordedRequestMatcher;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.constant.MediaType;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.CONTENT_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OperatorOperationsTest {


    private static final String PAGE = "page";
    private static final String PAGE_SIZE = "size";
    private static final String INCLUDE_PIN = "includePin";
    private static final String INCLUDE_DATA = "includeData";
    private static final String INCLUDE_BUNDLES = "includeBundles";
    private static final String INCLUDE_SUGGESTED_AMOUNTS = "suggestedAmounts";
    private static final String INCLUDE_RANGE_DENOMINATION_TYPE = "includeRange";
    private static final String INCLUDE_FIXED_DENOMINATION_TYPE = "includeFixed";
    private static final String INCLUDE_SUGGESTED_AMOUNTS_MAP = "suggestedAmountsMap";
    private static final List<String> validFilters = Arrays.asList(PAGE.toLowerCase(), PAGE_SIZE.toLowerCase(),
            INCLUDE_PIN.toLowerCase(), INCLUDE_DATA.toLowerCase(), INCLUDE_BUNDLES.toLowerCase(),
            INCLUDE_SUGGESTED_AMOUNTS.toLowerCase(), INCLUDE_SUGGESTED_AMOUNTS_MAP.toLowerCase(),
            INCLUDE_RANGE_DENOMINATION_TYPE.toLowerCase(), INCLUDE_FIXED_DENOMINATION_TYPE.toLowerCase()
    );

    private static final String PATH = "src/test/resources/operator";
    private static final String OPERATORS_PAGED_UNFILTERED = PATH + "/operators_paged_unfiltered_response.json";
    private static final String OPERATOR_UNFILTERED = PATH + "/operator_unfiltered_response.json";
    private static final String OPERATOR_FILTERED = PATH + "/operator_filtered_response.json";
    private static final String OPERATORS_BY_COUNTRY_UNFILTERED = PATH + "/operators_by_country_code_unfiltered.json";
    private static final String OPERATORS_BY_COUNTRY_FILTERED_EXCLUDE_BUNDLES = PATH +
            "/operators_by_country_code_filtered_exclude_bundles.json";
    private static final String OPERATOR_AUTO_DETECT_UNFILTERED = PATH + "/operator_auto_detect_unfiltered.json";
    private static final String OPERATOR_AUTO_DETECT_FILTERED = PATH + "/operator_auto_detect_filtered.json";
    private static final String OPERATOR_FX_RATE = PATH + "/operator_fx_rate.json";

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
    public void testListOperatorsWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<Operator>> request = airtimeAPI.operators().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATORS_PAGED_UNFILTERED, 200);
        Page<Operator> operatorsPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/operators"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        operatorsPage.getContent().forEach(this::assertIsValidOperator);
    }

    @Test
    public void testListOperatorsWithFilters() throws Exception {

        int page = 1;
        int pageSize = 5;
        OperatorFilter filter = new OperatorFilter().withPage(page, pageSize)
                .includePin(false)
                .includeData(false)
                .includeBundles(false)
                .includeSuggestedAmounts(true)
                .includeSuggestedAmountsMap(true);

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();

        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<Operator>> request = airtimeAPI.operators().list(filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATORS_PAGED_UNFILTERED, 200);
        Page<Operator> operatorsPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        Set<String> queryParameterNames = Objects.requireNonNull(requestUrl).queryParameterNames();
        queryParameterNames.forEach(param -> Assertions.assertTrue(validFilters.contains(param.toLowerCase())));
        assertThat(requestUrl.querySize(), equalTo(9));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/operators"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PAGE, String.valueOf(page)));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PAGE_SIZE, String.valueOf(pageSize)));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_PIN, "false"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_DATA, "false"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_BUNDLES, "false"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_SUGGESTED_AMOUNTS, "true"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_SUGGESTED_AMOUNTS_MAP, "true"));
        operatorsPage.getContent().forEach(this::assertIsValidOperator);
    }

    @Test
    public void testListOperatorsByCountryCodeWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<List<Operator>> request = airtimeAPI.operators().listByCountryCode(CountryCode.HT);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATORS_BY_COUNTRY_UNFILTERED, 200);
        List<Operator> operators = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest,
                RecordedRequestMatcher.hasMethodAndPath("GET", "/operators/countries/" + CountryCode.HT.getAlpha2()));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        operators.forEach(this::assertIsValidOperator);
        operators.forEach(operator -> assertThat(operator.getSuggestedAmountsMap(), is(anEmptyMap())));
    }

    @Test
    public void testListOperatorsByCountryCodeWithFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        OperatorFilter filter = new OperatorFilter().includeBundles(false).includeSuggestedAmountsMap(true);
        Request<List<Operator>> request = airtimeAPI.operators().listByCountryCode(CountryCode.HT, filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATORS_BY_COUNTRY_FILTERED_EXCLUDE_BUNDLES, 200);
        List<Operator> operators = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest,
                RecordedRequestMatcher.hasMethodAndPath("GET", "/operators/countries/" + CountryCode.HT.getAlpha2()));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        operators.forEach(operator -> {
            assertIsValidOperator(operator);
            assertThat(operator.isBundle(), equalTo(false));
            assertThat(operator.getSuggestedAmountsMap(), not(anEmptyMap()));
        });
    }

    @Test
    public void testGetOperatorByIdWithNoFilters() throws Exception {

        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Operator> request = airtimeAPI.operators().getById(operatorId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATOR_UNFILTERED, 200);
        Operator operator = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/operators/" + operatorId));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));
        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidOperator(operator);
        assertThat(operator.getId(), equalTo(operatorId));
        assertThat(operator.getSuggestedAmountsMap(), is(anEmptyMap()));
    }

    @Test
    public void testGetOperatorByIdWithFilters() throws Exception {

        Long operatorId = 174L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        OperatorFilter filter = new OperatorFilter().includeSuggestedAmountsMap(true);
        Request<Operator> request = airtimeAPI.operators().getById(operatorId, filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATOR_FILTERED, 200);
        Operator operator = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/operators/" + operatorId));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));
        assertIsValidOperator(operator);
        assertThat(operator.getId(), equalTo(operatorId));
        assertThat(operator.getSuggestedAmountsMap(), not(anEmptyMap()));
    }

    @Test
    public void testAutoDetectOperatorWithNoFilters() throws Exception {

        String phone = "+50936377111";
        String countryCode = CountryCode.HT.getAlpha2();
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Operator> request = airtimeAPI.operators().autoDetect(phone, CountryCode.HT);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATOR_AUTO_DETECT_UNFILTERED, 200);
        Operator operator = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String path = "/operators/auto-detect/phone/" + phone + "/countries/" + countryCode;
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", path));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));
        assertIsValidOperator(operator);
        assertThat(operator.getSuggestedAmountsMap(), anEmptyMap());
    }

    @Test
    public void testAutoDetectOperatorWithFilters() throws Exception {

        String phone = "+50936377111";
        String countryCode = CountryCode.HT.getAlpha2();
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        OperatorFilter filter = new OperatorFilter().includeSuggestedAmountsMap(true);
        Request<Operator> request = airtimeAPI.operators().autoDetect(phone, CountryCode.HT, filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATOR_AUTO_DETECT_FILTERED, 200);
        Operator operator = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String path = "/operators/auto-detect/phone/" + phone + "/countries/" + countryCode;
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", path));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));
        assertIsValidOperator(operator);
        assertThat(operator.getSuggestedAmountsMap(), not(anEmptyMap()));
    }

    @Test
    public void testCalculateOperatorFxRate() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Double amount = 5.00;
        Long operatorId = 174L;
        Request<OperatorFxRate> request = airtimeAPI.operators().calculateFxRate(operatorId, amount);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(OPERATOR_FX_RATE, 200);
        OperatorFxRate operatorFxRate = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String path = "/operators/" + operatorId + "/fx-rate";
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("POST", path));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON));
        assertThat(operatorFxRate.getOperatorId(), notNullValue());
        assertThat(operatorFxRate.getOperatorName(), notNullValue());
        assertThat(operatorFxRate.getFxRate(), notNullValue());
        assertThat(operatorFxRate.getCurrencyCode(), notNullValue());
        assertThat(operatorFxRate.getOperatorId(), equalTo(operatorId));
    }

    @Test
    public void testCalculateFxRateShouldThrowExceptionWhenOperatorIdIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().calculateFxRate(0L, 5.00));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testCalculateFxRateShouldThrowExceptionWhenOperatorIdIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().calculateFxRate(null, 5.00));
        Assertions.assertEquals("'Operator id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testCalculateFxRateShouldThrowExceptionWhenAmountIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().calculateFxRate(174L, -5.00));
        Assertions.assertEquals("'Amount' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testCalculateFxRateShouldThrowExceptionWhenAmountIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().calculateFxRate(174L, 0.00));
        Assertions.assertEquals("'Amount' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testCalculateFxRateShouldThrowExceptionWhenAmountIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().calculateFxRate(174L, null));
        Assertions.assertEquals("'Amount' cannot be null!", exception.getMessage());
    }

    @Test
    public void testAutoDetectOperatorShouldThrowExceptionWhenPhoneIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().autoDetect(null, CountryCode.CO));
        Assertions.assertEquals("'Phone' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testAutoDetectOperatorShouldThrowExceptionWhenPhoneIsEmpty() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().autoDetect(" ", CountryCode.CO));
        Assertions.assertEquals("'Phone' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testAutoDetectOperatorShouldThrowExceptionWhenCountryCodeIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().autoDetect("50936377111", null));
        Assertions.assertEquals("'Country code' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetOperatorByIdShouldThrowExceptionWhenOperatorIdIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().getById(-5L));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetOperatorByIdShouldThrowExceptionWhenOperatorIdIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().getById(0L));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetOperatorByIdShouldThrowExceptionWhenOperatorIdIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().getById(null));
        Assertions.assertEquals("'Operator id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetOperatorByCountryCodeShouldThrowExceptionWhenCountryCodeIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.operators().listByCountryCode(null));
        Assertions.assertEquals("'Country code' cannot be null!", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenRequestFilterPageIsLessThanOrEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> airtimeAPI.operators()
                .list(new OperatorFilter().withPage(0, 5)));
        Assertions.assertEquals("Filter page number must greater than zero", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenRequestFilterPageSizeIsLessThanOrEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> airtimeAPI.operators()
                .list(new OperatorFilter().withPage(1, 0)));
        Assertions.assertEquals("Filter page size must greater than zero", exception.getMessage());
    }

    private void assertIsValidOperator(Operator operator) {

        List<String> operatorFields = Arrays.asList("id", "name", "bundle", "data", "pinBased", "supportsLocalAmounts",
                "denominationType", "senderCurrencyCode", "senderCurrencySymbol", "destinationCurrencyCode",
                "destinationCurrencySymbol", "internationalDiscount", "localDiscount", "mostPopularInternationalAmount",
                "mostPopularLocalAmount", "country", "fxRate", "suggestedAmounts", "suggestedAmountsMap", "minAmount",
                "maxAmount", "localMinAmount", "localMaxAmount", "fixedAmounts", "localFixedAmounts",
                "fixedAmountsDescriptions", "localFixedAmountsDescriptions", "logoUrls", "promotions");

        operatorFields.forEach(field -> assertThat(operator, hasProperty(field)));

        assertThat(operator.getId(), is(notNullValue()));
        assertThat(operator.getName(), is(notNullValue()));
        assertThat(operator.isBundle(), is(notNullValue()));
        assertThat(operator.isData(), is(notNullValue()));
        assertThat(operator.isPinBased(), is(notNullValue()));
        assertThat(operator.isSupportsLocalAmounts(), is(notNullValue()));
        assertThat(operator.getDenominationType(), is(notNullValue()));
        assertThat(operator.getDenominationType(), anyOf(equalTo(DenominationType.RANGE), equalTo(DenominationType.FIXED)));
        assertThat(operator.getSenderCurrencyCode(), is(notNullValue()));
        assertThat(operator.getSenderCurrencySymbol(), is(notNullValue()));
        assertThat(operator.getDestinationCurrencyCode(), is(notNullValue()));
        assertThat(operator.getDestinationCurrencySymbol(), is(notNullValue()));
        assertThat(operator.getInternationalDiscount(), is(notNullValue()));
        assertThat(operator.getCountry(), is(notNullValue()));
    }
}
