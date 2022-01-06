package software.reloadly.sdk.giftcard.operation.unit;

import com.neovisionaries.i18n.CountryCode;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.internal.constant.MediaType;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.RecordedRequestMatcher;
import software.reloadly.sdk.giftcard.util.GiftcardAPIMockServer;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.request.GiftCardOrderRequest;
import software.reloadly.sdk.giftcard.dto.response.GiftcardInfo;
import software.reloadly.sdk.giftcard.dto.response.GiftcardTransaction;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.CONTENT_TYPE;
import static software.reloadly.sdk.core.internal.enums.Version.GIFTCARD_V1;

public class GiftcardOrdersOperationsTests {

    private static final String END_POINT = "/orders";
    private static final String PATH = "src/test/resources/order";
    private static final String TRANSACTION_RECIPIENT_EMAIL = PATH + "/transaction_with_recipient_email_response.json";
    private static final String TRANSACTION_RECIPIENT_PHONE = PATH + "/transaction_with_recipient_phone_response.json";
    private static final String TRANSACTION_REDEEM = PATH + "/transaction_redeem_response.json";

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
    public void testPlaceOrderWithRecipientEmailHappyPath() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientEmail("test@example.com")
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        Request<GiftcardTransaction> request = giftcardAPI.orders().placeOrder(orderRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_RECIPIENT_EMAIL, 200);
        GiftcardTransaction transaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("POST", END_POINT));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidGiftcardTransaction(transaction);
    }

    @Test
    public void testPlaceOrderWithRecipientPhoneHappyPath() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        Request<GiftcardTransaction> request = giftcardAPI.orders().placeOrder(orderRequest);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_RECIPIENT_PHONE, 200);
        GiftcardTransaction transaction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("POST", END_POINT));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidGiftcardTransaction(transaction);
    }

    @Test
    public void testRedeemOrderByTransactionId() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Long transactionId = 818L;
        Request<List<GiftcardInfo>> request = giftcardAPI.orders().redeem(transactionId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(TRANSACTION_REDEEM, 200);
        List<GiftcardInfo> cards = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/orders/transactions/" + transactionId + "/cards";
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        cards.forEach(this::assertValidGiftCardRedeemInfo);
    }

    @Test
    public void testRedeemOrderByTransactionIdShouldThrowExceptionWhenProductIdIsNull() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().redeem(null));
        Assertions.assertEquals("'Transaction id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testRedeemOrderByTransactionIdShouldThrowExceptionWhenProductIdIsLessThanZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().redeem(-25L));
        Assertions.assertEquals("'Transaction id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testRedeemOrderByTransactionIdShouldThrowExceptionWhenProductIdIsEqualToZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().redeem(0L));
        Assertions.assertEquals("'Transaction id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestQuantityIsMissing() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Quantity' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestQuantityIsLessThanZero() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(-2)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Quantity' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestUnitPriceIsMissing() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Unit price' cannot be null!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestUnitPriceIsLessThanZero() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(-5))
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Unit price' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestSenderNameIsMissing() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Sender name' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestSenderNameIsEmpty() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Sender name' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestRecipientEmailAndRecipientPhoneAreBothMissing() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("Either recipient email or recipient phone is required", exception.getMessage());
    }

    @Test
    public void testPlaceOrderShouldThrowExceptionWhenRequestRecipientEmailIsInvalid() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientEmail("someInvalidEmail")
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));
        Assertions.assertEquals("'Recipient email' is not a valid email address!", exception.getMessage());
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    public void testPlaceOrderShouldThrowExceptionWhenRequestRecipientPhoneNumberIsNotValid() {

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058abcd", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.orders().placeOrder(orderRequest));

        String msg = "'Recipient phone number' must contain '+' signs and numbers only. No other characters allowed!";
        Assertions.assertEquals(msg, exception.getMessage());
    }

    private void assertValidGiftCardRedeemInfo(GiftcardInfo giftcardInfo) {

        int expectedFieldsCount = 2;
        List<String> transactionFields = Arrays.asList("pinCode", "cardNumber");
        List<String> fields = Arrays.stream(giftcardInfo.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());

        int actualFieldsCount = fields.size();
        String errorMsg = "Failed asserting that GiftcardInfo::class contains " + expectedFieldsCount;
        errorMsg += " fields. It actually contains " + actualFieldsCount + " fields";
        assertThat(errorMsg, expectedFieldsCount == actualFieldsCount);
        assertThat(giftcardInfo, is(notNullValue()));
        transactionFields.forEach(field -> assertThat(giftcardInfo, hasProperty(field)));

        assertThat(giftcardInfo.getPinCode(), is(not(emptyOrNullString())));
        assertThat(giftcardInfo.getCardNumber(), is(not(emptyOrNullString())));
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
