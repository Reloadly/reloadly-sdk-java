package software.reloadly.sdk.airtime.operation.unit;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.util.AirtimeAPIMockServer;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Discount;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;
import software.reloadly.sdk.core.internal.filter.QueryFilter;

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

public class DiscountOperationsTest {

    private static final String PATH = "src/test/resources/discount";
    private static final String DISCOUNT = PATH + "/discount.json";
    private static final String DISCOUNT_PAGE = PATH + "/discount_page.json";

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
    public void testListDiscounts() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<Discount>> request = airtimeAPI.discounts().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(DISCOUNT_PAGE, 200);
        Page<Discount> discountPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", "/operators/commissions"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        discountPage.getContent().forEach(this::assertIsValidDiscount);
    }

    @Test
    public void testListDiscountsWithFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        QueryFilter filter = new QueryFilter().withPage(1, 200);
        Request<Page<Discount>> request = airtimeAPI.discounts().list(filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(DISCOUNT_PAGE, 200);
        Page<Discount> discountPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", "/operators/commissions"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(2));
        discountPage.getContent().forEach(this::assertIsValidDiscount);
    }

    @Test
    public void testGetByOperatorId() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Long operatorId = 174L;
        Request<Discount> request = airtimeAPI.discounts().getByOperatorId(operatorId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(DISCOUNT, 200);
        Discount discount = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(
                recordedRequest, hasMethodAndPath("GET", "/operators/" + operatorId + "/commissions"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidDiscount(discount);
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.discounts().getByOperatorId(null));
        Assertions.assertEquals("'Operator id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.discounts().getByOperatorId(-105L));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.discounts().getByOperatorId(0L));
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    private void assertIsValidDiscount(Discount discount) {

        List<String> countryFields = Arrays.asList(
                "internationalPercentage", "localPercentage", "updatedAt", "operator"
        );

        countryFields.forEach(field -> assertThat(discount, hasProperty(field)));

        assertThat(discount.getPercentage(), is(notNullValue()));
        assertThat(discount.getInternationalPercentage(), is(notNullValue()));
        assertThat(discount.getLocalPercentage(), is(notNullValue()));
        assertThat(discount.getUpdatedAt(), is(notNullValue()));
        assertThat(discount.getOperator(), is(notNullValue()));
        assertThat(discount.getOperator().getId(), is(notNullValue()));
        assertThat(discount.getOperator().getName(), is(notNullValue()));
        assertThat(discount.getOperator().getCountryCode(), is(notNullValue()));
    }
}
