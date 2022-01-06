package software.reloadly.sdk.giftcard.operation.unit;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.RecordedRequestMatcher;
import software.reloadly.sdk.giftcard.util.GiftcardAPIMockServer;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardDiscount;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static software.reloadly.sdk.core.internal.enums.Version.GIFTCARD_V1;

public class GiftcardDiscountsOperationsTests {

    private static final String PATH = "src/test/resources/discount";
    private static final String DISCOUNT_BY_PRODUCT_ID = PATH + "/discount_response.json";
    private static final String PAGED_DISCOUNTS = PATH + "/discounts_paged_unfiltered_response.json";

    private GiftcardAPIMockServer server;

    @BeforeEach
    public void setUp() throws Exception {
        server = new GiftcardAPIMockServer();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void testListGiftcardDiscounts() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<GiftcardDiscount>> request = giftcardAPI.discounts().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PAGED_DISCOUNTS, 200);
        Page<GiftcardDiscount> redeemInstructions = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/discounts"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        redeemInstructions.forEach(this::assertIsValidGiftcardDiscount);
    }

    @Test
    public void testListGiftcardDiscountsByProductId() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        long productId = 100L;
        Request<GiftcardDiscount> request = giftcardAPI.discounts().getByProductId(productId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(DISCOUNT_BY_PRODUCT_ID, 200);
        GiftcardDiscount discount = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/products/" + productId + "/discounts";
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidGiftcardDiscount(discount);
    }

    @Test
    public void testListGiftcardDiscountsByProductIdShouldThrowExceptionWhenProductIdIsNull() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.discounts().getByProductId(null));
        Assertions.assertEquals("'Product id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testListGiftcardDiscountsByProductIdShouldThrowExceptionWhenProductIdIsLessThanZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.discounts().getByProductId(-25L));
        Assertions.assertEquals("'Product id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testListGiftcardDiscountsByProductIdShouldThrowExceptionWhenProductIdIsEqualToZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.discounts().getByProductId(0L));
        Assertions.assertEquals("'Product id' must be greater than zero!", exception.getMessage());
    }

    private void assertIsValidGiftcardDiscount(GiftcardDiscount discount) {

        int expectedDiscountFieldsCount = 2;
        int expectedProductSimplifiedFieldsCount = 4;
        List<String> discountFields = Arrays.asList("percentage", "product");
        List<String> productSimplifiedFields = Arrays.asList("id", "name", "global", "countryCode");

        int actualDiscountFieldsCount = (int) Arrays.stream(discount.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).count();

        int actualProductSimplifiedFieldsCount = (int) Arrays.stream(discount.getProduct().getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).count();

        String errorMsg = "Failed asserting that GiftcardDiscount::class contains " + expectedDiscountFieldsCount;
        errorMsg += " fields. It actually contains " + actualDiscountFieldsCount;
        String errorMsg2 = "Failed asserting that GiftcardProductSimplified::class contains ";
        errorMsg2 += expectedProductSimplifiedFieldsCount + " fields. It actually contains ";
        errorMsg2 += actualProductSimplifiedFieldsCount + " fields.";
        assertThat(errorMsg, expectedDiscountFieldsCount == actualDiscountFieldsCount);
        assertThat(errorMsg2, expectedProductSimplifiedFieldsCount == actualProductSimplifiedFieldsCount);
        assertThat(discount, is(notNullValue()));
        assertThat(discount.getProduct(), is(notNullValue()));

        GiftcardDiscount.GiftcardDiscountProduct product = discount.getProduct();
        discountFields.forEach(field -> assertThat(discount, hasProperty(field)));
        productSimplifiedFields.forEach(field -> assertThat(product, hasProperty(field)));

        assertThat(discount.getPercentage(), is(notNullValue()));
        assertThat(discount.getPercentage(), is(greaterThanOrEqualTo(0F)));

        assertThat(product.getId(), is(notNullValue()));
        assertThat(product.getId(), is(greaterThan(0L)));
        assertThat(product.getName(), is(not(emptyOrNullString())));
        assertThat(product.getCountryCode(), is(not(emptyOrNullString())));
    }
}
