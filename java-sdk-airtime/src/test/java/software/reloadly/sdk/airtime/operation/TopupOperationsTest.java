package software.reloadly.sdk.airtime.operation;

import com.neovisionaries.i18n.CountryCode;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.AirtimeAPIMockServer;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.Phone;
import software.reloadly.sdk.airtime.dto.request.EmailTopupRequest;
import software.reloadly.sdk.airtime.dto.request.PhoneTopupRequest;
import software.reloadly.sdk.airtime.dto.response.*;
import software.reloadly.sdk.airtime.enums.AirtimeTransactionStatus;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.util.RecordedRequestMatcher.hasHeader;
import static software.reloadly.sdk.core.internal.util.RecordedRequestMatcher.hasMethodAndPath;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;

public class TopupOperationsTest {

    private static final String PATH = "src/test/resources/topup";
    private static final String PHONE_TOPUP = PATH + "/phone_topup_transaction.json";
    private static final String EMAIL_TOPUP = PATH + "/email_topup_transaction.json";
    private static final String EMAIL_TOPUP_ASYNC = PATH + "/email_topup_transaction_async.json";
    private static final String PHONE_TOPUP_ASYNC = PATH + "/phone_topup_transaction_async.json";
    private static final String PHONE_TOPUP_STATUS = PATH + "/phone_topup_transaction_status.json";
    private static final String EMAIL_TOPUP_STATUS = PATH + "/email_topup_transaction_status.json";
    private static final String PHONE_TOPUP_PIN_DETAIL = PATH + "/phone_topup_transaction_with_pin_detail.json";

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
    public void testSendPhoneTopup() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+50936377111", CountryCode.HT))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(15.00)
                .operatorId(173L).build();

        Request<TopupTransaction> request = airtimeAPI.topups().send(phoneTopupRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PHONE_TOPUP, 200);
        TopupTransaction topupTransaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/topups"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidTransaction(topupTransaction);
        assertThat(topupTransaction.getRecipientEmail(), is(nullValue()));
    }

    @Test
    public void testSendPinBasedPhoneTopup() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(18.24)
                .operatorId(672L).build();

        Request<TopupTransaction> request = airtimeAPI.topups().send(phoneTopupRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PHONE_TOPUP_PIN_DETAIL, 200);
        TopupTransaction topupTransaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/topups"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidTransaction(topupTransaction);
        assertThat(topupTransaction.getPinDetail(), is(notNullValue()));
        assertThat(topupTransaction.getRecipientEmail(), is(nullValue()));

    }

    @Test
    public void testSendEmailTopup() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        EmailTopupRequest emailTopupRequest = EmailTopupRequest.builder()
                .recipientEmail("testing@nauta.com.cu")
                .customIdentifier(UUID.randomUUID().toString())
                .amount(30.00)
                .operatorId(683L).build();

        Request<TopupTransaction> request = airtimeAPI.topups().send(emailTopupRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(EMAIL_TOPUP, 200);
        TopupTransaction topupTransaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/topups"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidTransaction(topupTransaction);
        assertThat(topupTransaction.getRecipientEmail(), is(notNullValue()));
        assertThat(topupTransaction.getRecipientPhone(), is(nullValue()));
    }

    @Test
    public void testSendPhoneTopupAsync() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+50936377111", CountryCode.HT))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(15.00)
                .operatorId(173L).build();

        Request<AsyncAirtimeResponse> request = airtimeAPI.topups().sendAsync(phoneTopupRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PHONE_TOPUP_ASYNC, 200);
        AsyncAirtimeResponse airtimeResponse = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/topups-async"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertThat(airtimeResponse, is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(greaterThan(0L)));
    }

    @Test
    public void testSendEmailTopupAsync() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        EmailTopupRequest emailTopupRequest = EmailTopupRequest.builder()
                .recipientEmail("testing@nauta.com.cu")
                .customIdentifier(UUID.randomUUID().toString())
                .amount(30.00)
                .operatorId(683L).build();

        Request<AsyncAirtimeResponse> request = airtimeAPI.topups().sendAsync(emailTopupRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(EMAIL_TOPUP_ASYNC, 200);
        AsyncAirtimeResponse airtimeResponse = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/topups-async"));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertThat(airtimeResponse, is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(greaterThan(0L)));
    }

    @Test
    public void testRetrievePhoneTransactionStatus() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Long transactionId = 1L;
        Request<AirtimeTransactionStatusResponse> request = airtimeAPI.topups().getStatus(transactionId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PHONE_TOPUP_STATUS, 200);
        AirtimeTransactionStatusResponse transactionStatus = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/topups/" + transactionId + "/status";
        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", endPoint));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidTransactionStatusResponse(transactionStatus);
        assertThat(transactionStatus.getTransaction().getRecipientEmail(), is(emptyOrNullString()));
        assertThat(transactionStatus.getTransaction().getRecipientPhone(), is(not(emptyOrNullString())));
    }

    @Test
    public void testRetrieveEmailTransactionStatus() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = airtimeAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(airtimeAPI, HttpUrl.parse(server.getBaseUrl()));

        Long transactionId = 2L;
        Request<AirtimeTransactionStatusResponse> request = airtimeAPI.topups().getStatus(transactionId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(EMAIL_TOPUP_STATUS, 200);
        AirtimeTransactionStatusResponse transactionStatus = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/topups/" + transactionId + "/status";
        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("GET", endPoint));
        MatcherAssert.assertThat(recordedRequest, hasHeader(ACCEPT, Version.AIRTIME_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidTransactionStatusResponse(transactionStatus);
        assertThat(transactionStatus.getTransaction().getRecipientPhone(), is(emptyOrNullString()));
        assertThat(transactionStatus.getTransaction().getRecipientEmail(), is(not(emptyOrNullString())));
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenRecipientPhoneIsMissing() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .customIdentifier(UUID.randomUUID().toString())
                .amount(18.24)
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Recipient phone' cannot be null!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenRecipientPhoneNumberIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone(null, CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(18.24)
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Recipient phone number' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenRecipientPhoneNumberIsEmpty() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(18.24)
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Recipient phone number' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenRecipientPhoneNumberContainsInvalidCharacters() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+3ABCD06907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(18.24)
                .operatorId(672L).build();

        String expected = "'Recipient phone number' must contain only numbers and an optional leading '+' sign!";
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());

        Assertions.assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenRecipientPhoneCountryCodeIsNull() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", null))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(18.24)
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Recipient phone country code' cannot be null!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenAmountIsMissing() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Amount' cannot be null!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenAmountEqualsToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(0.00)
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Amount' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenAmountLessThanToZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(-50.00)
                .operatorId(672L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Amount' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenOperatorIdIsMissing() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(10.00)
                .build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Operator id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenOperatorIdEqualsZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(10.00)
                .operatorId(0L)
                .build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testSendPhoneTopupShouldThrowExceptionWhenOperatorIdIsLessThanZero() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(10.00)
                .operatorId(-672L)
                .build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(phoneTopupRequest).execute());
        Assertions.assertEquals("'Operator id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testSendEmailTopupShouldThrowExceptionWhenRecipientEmailIsMissing() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        EmailTopupRequest emailTopupRequest = EmailTopupRequest.builder()
                .customIdentifier(UUID.randomUUID().toString())
                .amount(30.00)
                .operatorId(683L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(emailTopupRequest).execute());
        Assertions.assertEquals("'Recipient email' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testSendEmailTopupShouldThrowExceptionWhenRecipientEmailIsInvalid() {
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(AirtimeAPIMockServer.ACCESS_TOKEN).build();
        EmailTopupRequest emailTopupRequest = EmailTopupRequest.builder()
                .recipientEmail("not-a-valid-email-address")
                .customIdentifier(UUID.randomUUID().toString())
                .amount(30.00)
                .operatorId(683L).build();

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> airtimeAPI.topups().send(emailTopupRequest).execute());
        Assertions.assertEquals("'Recipient email' is not a valid email address!", exception.getMessage());
    }

    private void assertIsValidTransaction(TopupTransaction transaction) {

        int expectedPinDetailFieldsCount = 8;
        int expectedBalanceInfoFieldsCount = 5;
        int expectedTransactionFieldsCount = 19;
        List<String> topupTransactionFields = Arrays.asList("id", "operatorTransactionId", "customIdentifier",
                "recipientPhone", "recipientEmail", "senderPhone", "countryCode", "operatorId", "operatorName",
                "discount", "discountCurrencyCode", "requestedAmount", "requestedAmountCurrencyCode",
                "deliveredAmount", "deliveredAmountCurrencyCode", "date", "balanceInfo", "balanceInfo", "status"
        );

        List<String> pinDetailFields = Arrays.asList(
                "serial", "info", "infoPart2", "infoPart3", "value", "code", "ivr", "validity");

        List<String> balanceInfoFields = Arrays.asList(
                "previousBalance", "currentBalance", "currencyCode", "currencyName", "updatedAt");

        assertThat(transaction, is(notNullValue()));
        List<String> transFields = Arrays.stream(transaction.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());

        int actualTransactionFieldsCount = transFields.size();
        String errorMsg = "Failed asserting that TopupTransaction::class contains " + expectedTransactionFieldsCount;
        errorMsg += " fields. It actually contains " + actualTransactionFieldsCount + " fields";
        assertThat(errorMsg, expectedTransactionFieldsCount == actualTransactionFieldsCount);

        topupTransactionFields.forEach(field -> assertThat(transaction, hasProperty(field)));

        if (transaction.getPinDetail() != null) {
            PinDetail pinDetail = transaction.getPinDetail();
            List<String> pinDFields = Arrays.stream(pinDetail.getClass().getDeclaredFields())
                    .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                            !f.getName().equalsIgnoreCase("$jacocoData")))
                    .map(Field::getName).collect(Collectors.toList());
            int actualPinDetailFieldsCount = pinDFields.size();
            errorMsg = "Failed asserting that PinDetail::class contains " + expectedPinDetailFieldsCount;
            errorMsg += " fields. It actually contains " + actualPinDetailFieldsCount + " fields";
            assertThat(errorMsg, expectedPinDetailFieldsCount == actualPinDetailFieldsCount);
            pinDetailFields.forEach(field -> assertThat(pinDetail, hasProperty(field)));
        }

        assertThat(transaction.getBalanceInfo(), is(notNullValue()));
        TransactionBalanceInfo balanceInfo = transaction.getBalanceInfo();
        List<String> balInfoDFields = Arrays.stream(balanceInfo.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());
        int actualBalanceInfoFieldsCount = balInfoDFields.size();
        errorMsg = "Failed asserting that PinDetail::class contains " + expectedBalanceInfoFieldsCount;
        errorMsg += " fields. It actually contains " + actualBalanceInfoFieldsCount + " fields";
        assertThat(errorMsg, expectedBalanceInfoFieldsCount == actualBalanceInfoFieldsCount);
        balanceInfoFields.forEach(field -> assertThat(balanceInfo, hasProperty(field)));

        assertThat(transaction.getId(), is(notNullValue()));
        if (transaction.getOperatorName().contains("Nauta Cuba")) {
            assertThat(transaction.getRecipientEmail(), is(notNullValue()));
        } else {
            assertThat(transaction.getRecipientPhone(), is(notNullValue()));
        }
        assertThat(transaction.getCountryCode(), is(notNullValue()));
        assertThat(transaction.getOperatorId(), is(notNullValue()));
        assertThat(transaction.getOperatorName(), is(notNullValue()));
        assertThat(transaction.getDiscount(), is(notNullValue()));
        assertThat(transaction.getDiscountCurrencyCode(), is(notNullValue()));
        assertThat(transaction.getRequestedAmount(), is(notNullValue()));
        assertThat(transaction.getRequestedAmountCurrencyCode(), is(notNullValue()));
        assertThat(transaction.getDeliveredAmount(), is(notNullValue()));
        assertThat(transaction.getDeliveredAmountCurrencyCode(), is(notNullValue()));
        assertThat(transaction.getDate(), is(notNullValue()));
    }

    private void assertIsValidTransactionStatusResponse(AirtimeTransactionStatusResponse response) {

        int expectedResponseFieldsCount = 4;
        List<String> responseFields = Arrays.asList("errorCode", "errorMessage", "transaction", "status");

        assertThat(response, is(notNullValue()));
        List<String> transFields = Arrays.stream(response.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());

        int actualResponseFieldsCount = transFields.size();
        String errorMsg = "Failed asserting that TopupTransaction::class contains " + expectedResponseFieldsCount;
        errorMsg += " fields. It actually contains " + actualResponseFieldsCount + " fields";
        assertThat(errorMsg, expectedResponseFieldsCount == actualResponseFieldsCount);

        responseFields.forEach(field -> assertThat(response, hasProperty(field)));

        assertThat(response.getErrorCode(), is(emptyOrNullString()));
        assertThat(response.getErrorMessage(), is(emptyOrNullString()));
        assertThat(response.getStatus(), is(notNullValue()));
        assertEquals(AirtimeTransactionStatus.SUCCESSFUL, response.getStatus());
        assertThat(response.getTransaction(), is(notNullValue()));
        assertIsValidTransaction(response.getTransaction());
    }
}
