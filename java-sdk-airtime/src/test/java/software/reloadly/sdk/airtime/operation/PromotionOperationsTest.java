package software.reloadly.sdk.airtime.operation;

import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.AirtimeAPIMockServer;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Promotion;
import software.reloadly.sdk.airtime.util.RecordedRequestMatcher;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;
import software.reloadly.sdk.core.internal.filter.QueryFilter;
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

import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PromotionOperationsTest {

    private static final String PATH = "src/test/resources/promotion";
    private static final String PROMOTION_BY_ID = PATH + "/promotion_by_id.json";
    private static final String PROMOTION_BY_COUNTRY = PATH + "/promotion_list_by_country.json";
    private static final String PROMOTION_FILTERED_PAGE = PATH + "/promotion_filtered_page.json";
    private static final String PROMOTION_BY_OPERATOR = PATH + "/promotion_list_by_operator.json";
    private static final String PROMOTION_UNFILTERED_PAGE = PATH + "/promotion_unfiltered_page.json";

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
    public void testListPromotionsWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<Promotion>> request = airtimeAPI.promotions().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PROMOTION_UNFILTERED_PAGE, 200);
        Page<Promotion> promotionPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/promotions"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        promotionPage.getContent().forEach(this::assertIsValidPromotion);
    }

    @Test
    public void testListPromotionsWithFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        int page = 1;
        int pageSize = 5;
        QueryFilter filter = new QueryFilter().withPage(page, pageSize);
        Request<Page<Promotion>> request = airtimeAPI.promotions().list(filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PROMOTION_FILTERED_PAGE, 200);
        Page<Promotion> promotionPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/promotions"));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(2));
        assertThat(promotionPage.getContent().size(), equalTo(pageSize));
        promotionPage.getContent().forEach(this::assertIsValidPromotion);
    }

    @Test
    public void testGetById() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Long promotionId = 7016L;
        Request<Promotion> request = airtimeAPI.promotions().getById(promotionId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PROMOTION_BY_ID, 200);
        Promotion promotion = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/promotions/" + promotionId));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidPromotion(promotion);
    }

    @Test
    public void testGetByCountryCode() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        CountryCode countryCode = CountryCode.HT;
        Request<List<Promotion>> request = airtimeAPI.promotions().getByCountryCode(countryCode);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PROMOTION_BY_COUNTRY, 200);
        List<Promotion> promotions = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(
                recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/promotions/countries/" + countryCode));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        promotions.forEach(this::assertIsValidPromotion);
    }

    @Test
    public void testGetByOperatorId() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Long operatorId = 173L;
        Request<List<Promotion>> request = airtimeAPI.promotions().getByOperatorId(operatorId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PROMOTION_BY_OPERATOR, 200);
        List<Promotion> promotions = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(
                recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/promotions/operators/" + operatorId));
        MatcherAssert.assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        promotions.forEach(promotion -> {
            assertIsValidPromotion(promotion);
            assertThat(promotion.getOperatorId(), equalTo(operatorId));
        });
    }

    @Test
    public void testGetByCodeShouldThrowExceptionWhenCountryCodeIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getByCountryCode(null));
        Assertions.assertEquals("'Country code' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getByOperatorId(null));
        Assertions.assertEquals("'Operator id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getByOperatorId(-105L));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getByOperatorId(0L));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }


    @Test
    public void testGetByIdShouldThrowExceptionWhenPromotionIdIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getById(null));
        Assertions.assertEquals("'Promotion id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetByIdShouldThrowExceptionWhenPromotionIdIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getById(-105L));
        Assertions.assertEquals("'Promotion id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetByIdShouldThrowExceptionWhenPromotionIdIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.promotions().getById(0L));
        Assertions.assertEquals("'Promotion id' must be greater than zero!", exception.getMessage());
    }

    private void assertIsValidPromotion(Promotion promotion) {

        List<String> countryFields = Arrays.asList("id", "operatorId", "title", "title2", "description", "startDate",
                "endDate", "denominations", "localDenominations"
        );

        countryFields.forEach(field -> assertThat(promotion, hasProperty(field)));

        assertThat(promotion.getId(), is(notNullValue()));
        assertThat(promotion.getOperatorId(), is(notNullValue()));
        assertThat(promotion.getTitle(), is(notNullValue()));
        assertThat(promotion.getTitle2(), is(notNullValue()));
        assertThat(promotion.getDescription(), is(notNullValue()));
        assertThat(promotion.getStartDate(), is(notNullValue()));
        assertThat(promotion.getEndDate(), is(notNullValue()));
    }
}
