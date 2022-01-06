package software.reloadly.sdk.giftcard.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.authentication.client.AuthenticationAPI;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.request.GiftCardOrderRequest;
import software.reloadly.sdk.giftcard.dto.response.GiftcardInfo;
import software.reloadly.sdk.giftcard.dto.response.GiftcardTransaction;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GiftcardOrdersOperationsTests {

    private static String accessToken;

    @BeforeAll
    static void beforeAll() throws ReloadlyException {
        String clientId = System.getenv("SANDBOX_CLIENT_ID");
        String clientSecret = System.getenv("SANDBOX_CLIENT_SECRET");
        accessToken = AuthenticationAPI.builder().clientId(clientId)
                .clientSecret(clientSecret)
                .service(Service.GIFTCARD_SANDBOX)
                .build().clientCredentials().getAccessToken().execute().getToken();
    }

    @Test
    public void testPlaceOrderWithRecipientEmailHappyPath() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(accessToken).build();
        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientEmail("test@example.com")
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        Request<GiftcardTransaction> request = giftcardAPI.orders().placeOrder(orderRequest);
        assertThat(request, is(notNullValue()));
        GiftcardTransaction transaction = request.execute();
        assertIsValidGiftcardTransaction(transaction);
    }

    @Test
    public void testPlaceOrderWithRecipientPhoneHappyPath() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(accessToken).build();
        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder().productId(10L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(15))
                .senderName("Emmanuel")
                .recipientPhone(new GiftCardOrderRequest.Phone("+13058989796", CountryCode.US))
                .customIdentifier(UUID.randomUUID().toString())
                .build();

        Request<GiftcardTransaction> request = giftcardAPI.orders().placeOrder(orderRequest);
        assertThat(request, is(notNullValue()));
        GiftcardTransaction transaction = request.execute();
        assertIsValidGiftcardTransaction(transaction);
    }

    @Test
    public void testRedeemOrderByTransactionId() throws Exception {

        Long transactionId = 818L;
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(accessToken).build();

        Request<List<GiftcardInfo>> request = giftcardAPI.orders().redeem(transactionId);
        assertThat(request, is(notNullValue()));
        List<GiftcardInfo> cards = request.execute();
        cards.forEach(this::assertValidGiftCardRedeemInfo);
    }

    private void assertValidGiftCardRedeemInfo(GiftcardInfo giftcardInfo) {

        int expectedFieldsCount = 2;
        List<String> transactionFields = Arrays.asList("pinCode", "cardNumber");
        List<String> fields = Arrays.stream(giftcardInfo.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());

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
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());

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
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());

        actualFieldsCount = fields.size();
        errorMsg = "Failed asserting that GiftcardTransactionProduct::class contains " + expectedFieldsCount;
        errorMsg += " fields. It actually contains " + actualFieldsCount + " fields";
        assertThat(errorMsg, expectedFieldsCount == actualFieldsCount);

        List<String> transactionProductFields = Arrays.asList("id", "name", "quantity", "countryCode", "currencyCode",
                "brand", "unitPrice", "totalPrice");

        transactionProductFields.forEach(field -> assertThat(transaction.getProduct(), hasProperty(field)));
    }
}
