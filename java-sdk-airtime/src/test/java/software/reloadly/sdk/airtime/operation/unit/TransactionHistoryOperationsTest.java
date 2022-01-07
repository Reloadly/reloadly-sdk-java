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
import software.reloadly.sdk.airtime.dto.response.TopupTransaction;
import software.reloadly.sdk.airtime.filter.TransactionHistoryFilter;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.util.RecordedRequestMatcher.hasHeader;
import static software.reloadly.sdk.core.internal.util.RecordedRequestMatcher.hasMethodAndPath;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;

public class TransactionHistoryOperationsTest {

    private static final String PATH = "src/test/resources/report";
    private static final String TRANSACTION_BY_ID = PATH + "/transaction_history_by_id.json";
    private static final String TRANSACTION_HISTORY_FILTERED_PAGE = PATH + "/transaction_history_filtered_page.json";
    private static final String TRANSACTION_HISTORY_UNFILTERED_PAGE = PATH + "/transaction_history_unfiltered_page.json";

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
    public void testListTransactionHistoryWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<TopupTransaction>> request = airtimeAPI.reports().transactionsHistory().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_HISTORY_UNFILTERED_PAGE, 200);
        Page<TopupTransaction> transactionHistoryPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", "/topups/reports/transactions"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        transactionHistoryPage.getContent().forEach(this::assertIsValidTransactionHistory);
    }

    @Test
    public void testListTransactionHistoryWithFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        int page = 1;
        int pageSize = 5;
        TransactionHistoryFilter filter = new TransactionHistoryFilter().withPage(page, pageSize)
                .startDate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0))
                .endDate(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));

        Request<Page<TopupTransaction>> request = airtimeAPI.reports().transactionsHistory().list(filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_HISTORY_FILTERED_PAGE, 200);
        Page<TopupTransaction> transactionHistoryPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", "/topups/reports/transactions"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(4));
        assertThat(transactionHistoryPage.getContent().size(), equalTo(pageSize));
        transactionHistoryPage.getContent().forEach(this::assertIsValidTransactionHistory);
    }

    @Test
    public void testGetById() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Long transactionId = 10657L;
        Request<TopupTransaction> request = airtimeAPI.reports().transactionsHistory().getById(transactionId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_BY_ID, 200);
        TopupTransaction topupTransaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(
                recordedRequest, hasMethodAndPath("GET", "/topups/reports/transactions/" + transactionId));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidTransactionHistory(topupTransaction);
    }

    @Test
    public void testGetByIdShouldThrowExceptionWhenTransactionIdIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.reports().transactionsHistory().getById(null));
        Assertions.assertEquals("'Transaction id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.reports().transactionsHistory().getById(-2375L));
        Assertions.assertEquals("'Transaction id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testGetByOperatorIdShouldThrowExceptionWhenOperatorIdIsEqualToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.reports().transactionsHistory().getById(0L));
        Assertions.assertEquals("'Transaction id' must be greater than zero!", exception.getMessage());
    }

    private void assertIsValidTransactionHistory(TopupTransaction topupTransaction) {

        List<String> topupTransactionFields = Arrays.asList("id", "operatorTransactionId", "customIdentifier",
                "recipientPhone", "recipientEmail", "senderPhone", "countryCode", "operatorId", "operatorName",
                "discount", "discountCurrencyCode", "requestedAmount", "requestedAmountCurrencyCode",
                "deliveredAmount", "deliveredAmountCurrencyCode", "date", "pinDetail"
        );

        topupTransactionFields.forEach(field -> assertThat(topupTransaction, hasProperty(field)));

        assertThat(topupTransaction.getId(), is(notNullValue()));
        if (topupTransaction.getOperatorName().contains("Nauta Cuba")) {
            assertThat(topupTransaction.getRecipientEmail(), is(notNullValue()));
        } else {
            assertThat(topupTransaction.getRecipientPhone(), is(notNullValue()));
        }
        assertThat(topupTransaction.getCountryCode(), is(notNullValue()));
        assertThat(topupTransaction.getOperatorId(), is(notNullValue()));
        assertThat(topupTransaction.getOperatorName(), is(notNullValue()));
        assertThat(topupTransaction.getDiscount(), is(notNullValue()));
        assertThat(topupTransaction.getDiscountCurrencyCode(), is(notNullValue()));
        assertThat(topupTransaction.getRequestedAmount(), is(notNullValue()));
        assertThat(topupTransaction.getRequestedAmountCurrencyCode(), is(notNullValue()));
        assertThat(topupTransaction.getDeliveredAmount(), is(notNullValue()));
        assertThat(topupTransaction.getDeliveredAmountCurrencyCode(), is(notNullValue()));
        assertThat(topupTransaction.getDate(), is(notNullValue()));
    }
}
