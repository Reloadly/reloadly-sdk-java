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
import software.reloadly.sdk.giftcard.dto.response.GiftcardTransaction;
import software.reloadly.sdk.giftcard.filter.GiftcardTransactionFilter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static software.reloadly.sdk.core.internal.enums.Version.GIFTCARD_V1;

public class GiftcardTransactionsOperationsTests {


    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String STATUS = "status";
    private static final String BRAND_ID = "brandId";
    private static final String END_DATE = "endDate";
    private static final String BRAND_NAME = "brandName";
    private static final String PRODUCT_ID = "productId";
    private static final String START_DATE = "startDate";
    private static final String PRODUCT_NAME = "productName";
    private static final String RECIPIENT_EMAIL = "recipientEmail";
    private static final String CUSTOM_IDENTIFIER = "customIdentifier";
    private static final List<String> validFilters = Arrays.asList(PAGE.toLowerCase(), SIZE.toLowerCase(),
            STATUS.toLowerCase(), BRAND_ID.toLowerCase(), END_DATE.toLowerCase(), BRAND_NAME.toLowerCase(),
            PRODUCT_ID.toLowerCase(), START_DATE.toLowerCase(), PRODUCT_NAME.toLowerCase(),
            RECIPIENT_EMAIL.toLowerCase(), CUSTOM_IDENTIFIER.toLowerCase());

    private static final String END_POINT = "/reports/transactions";
    private static final String PATH = "src/test/resources/transaction";
    private static final String TRANSACTION_BY_ID = PATH + "/transaction_by_id_response.json";
    private static final String TRANSACTIONS_PAGED_FILTERED = PATH + "/transactions_paged_filtered_response.json";
    private static final String TRANSACTIONS_PAGED_UNFILTERED = PATH + "/transactions_paged_unfiltered_response.json";

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
    public void testListGiftcardTransactionsWithNoFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<GiftcardTransaction>> request = giftcardAPI.transactionsHistory().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTIONS_PAGED_UNFILTERED, 200);
        Page<GiftcardTransaction> transactionsPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", END_POINT));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        transactionsPage.forEach(this::assertIsValidGiftcardTransaction);
    }

    @Test
    public void testListGiftcardTransactionsWithFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        int page = 1;
        int pageSize = 200;
        GiftcardTransactionFilter filter = new GiftcardTransactionFilter().withPage(page, pageSize)
                .productName("itunes");

        Request<Page<GiftcardTransaction>> request = giftcardAPI.transactionsHistory().list(filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTIONS_PAGED_FILTERED, 200);
        Page<GiftcardTransaction> transactionsPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        Set<String> queryParameterNames = Objects.requireNonNull(requestUrl).queryParameterNames();
        queryParameterNames.forEach(param -> Assertions.assertTrue(validFilters.contains(param.toLowerCase())));
        assertThat(requestUrl.querySize(), equalTo(3));
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", END_POINT));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter("page", String.valueOf(page)));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter("size", String.valueOf(pageSize)));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PRODUCT_NAME, "itunes"));
        transactionsPage.getContent().forEach(this::assertIsValidGiftcardTransaction);

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", END_POINT));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));
        transactionsPage.forEach(this::assertIsValidGiftcardTransaction);
    }

    @Test
    public void testGetGiftcardTransactionById() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Long transactionId = 813L;
        Request<GiftcardTransaction> request = giftcardAPI.transactionsHistory().getById(transactionId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_BY_ID, 200);
        GiftcardTransaction transaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = END_POINT + "/" + transactionId;
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidGiftcardTransaction(transaction);
    }

    @Test
    public void testGetGiftcardTransactionByIdShouldThrowExceptionWhenProductIdIsNull() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.transactionsHistory().getById(null));
        Assertions.assertEquals("'Transaction id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetGiftcardTransactionByIdShouldThrowExceptionWhenProductIdIsLessThanZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.transactionsHistory().getById(-25L));
        Assertions.assertEquals("'Transaction id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetGiftcardTransactionByIdShouldThrowExceptionWhenProductIdIsEqualToZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.transactionsHistory().getById(0L));
        Assertions.assertEquals("'Transaction id' must be greater than zero!", exception.getMessage());
    }

    private void assertIsValidGiftcardTransaction(GiftcardTransaction transaction) {

        int expectedFieldsCount = 12;
        List<String> transactionFields = Arrays.asList("id", "fee", "smsFee", "discount", "amount", "currencyCode",
                "recipientEmail", "recipientPhone", "customIdentifier", "status", "product");

        List<String> fields = Arrays.stream(transaction.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());

        int actualFieldsCount = fields.size();
        String errorMsg = "Failed asserting that GiftcardTransaction::class contains " + expectedFieldsCount;
        errorMsg += " fields. It actually contains " + actualFieldsCount + " fields";
        assertThat(errorMsg, expectedFieldsCount == actualFieldsCount);
        assertThat(transaction, is(notNullValue()));
        transactionFields.forEach(field -> assertThat(transaction, hasProperty(field)));

        assertThat(transaction.getId(), is(notNullValue()));
        assertThat(transaction.getId(), is(greaterThan(0L)));

        assertThat(transaction.getFee(), is(notNullValue()));
        assertThat(transaction.getFee(), is(greaterThanOrEqualTo(0F)));
        assertThat(transaction.getSmsFee(), is(notNullValue()));
        assertThat(transaction.getSmsFee(), is(greaterThanOrEqualTo(0F)));
        assertThat(transaction.getDiscount(), is(notNullValue()));
        assertThat(transaction.getDiscount(), is(greaterThanOrEqualTo(0F)));

        assertThat(transaction.getAmount(), is(greaterThan(BigDecimal.ZERO)));
        assertThat(transaction.getCurrencyCode(), is(not(emptyOrNullString())));
        assertThat(transaction.getStatus(), is(notNullValue()));
        assertThat(transaction.getProduct(), is(notNullValue()));

        expectedFieldsCount = 8;
        fields = Arrays.stream(transaction.getProduct().getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());

        actualFieldsCount = fields.size();
        errorMsg = "Failed asserting that GiftcardTransactionProduct::class contains " + expectedFieldsCount;
        errorMsg += " fields. It actually contains " + actualFieldsCount + " fields";
        assertThat(errorMsg, expectedFieldsCount == actualFieldsCount);

        List<String> transactionProductFields = Arrays.asList("id", "name", "quantity", "countryCode", "currencyCode",
                "brand", "unitPrice", "totalPrice");

        transactionProductFields.forEach(field -> assertThat(transaction.getProduct(), hasProperty(field)));
    }
}
