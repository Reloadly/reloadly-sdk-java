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
import software.reloadly.sdk.airtime.dto.response.TopupTransaction;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.airtime.util.RecordedRequestMatcher.hasHeader;
import static software.reloadly.sdk.airtime.util.RecordedRequestMatcher.hasMethodAndPath;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;

public class TopupOperationsTest {

    private static final String PATH = "src/test/resources/topup";
    private static final String PHONE_TOPUP = PATH + "/phone_topup_transaction.json";
    private static final String EMAIL_TOPUP = PATH + "/email_topup_transaction.json";
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
        assertIsValidTransactionHistory(topupTransaction);
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
        assertIsValidTransactionHistory(topupTransaction);
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
        assertIsValidTransactionHistory(topupTransaction);
        assertThat(topupTransaction.getRecipientEmail(), is(notNullValue()));
        assertThat(topupTransaction.getRecipientPhone(), is(nullValue()));
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

    private void assertIsValidTransactionHistory(TopupTransaction topupTransaction) {

        List<String> topupTransactionFields = Arrays.asList("id", "operatorTransactionId", "customIdentifier",
                "recipientPhone", "recipientEmail", "senderPhone", "countryCode", "operatorId", "operatorName",
                "discount", "discountCurrencyCode", "requestedAmount", "requestedAmountCurrencyCode",
                "deliveredAmount", "deliveredAmountCurrencyCode", "date", "pinDetail"
        );

        List<String> pinDetailFields = Arrays.asList(
                "serial", "info", "infoPart2", "infoPart3", "value", "code", "ivr", "validity");

        topupTransactionFields.forEach(field -> assertThat(topupTransaction, hasProperty(field)));

        if (topupTransaction.getPinDetail() != null) {
            pinDetailFields.forEach(field -> assertThat(topupTransaction.getPinDetail(), hasProperty(field)));
        }

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
