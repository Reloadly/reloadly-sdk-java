package software.reloadly.sdk.giftcard.operation.integration;

import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardTransaction;
import software.reloadly.sdk.giftcard.filter.GiftcardTransactionFilter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static software.reloadly.sdk.core.enums.Environment.LIVE;
import static software.reloadly.sdk.core.enums.Environment.SANDBOX;

public class GiftcardTransactionsOperationsTests extends BaseIntegrationTest {

    @Test
    public void testListGiftcardTransactionsWithNoFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(LIVE).accessToken(accessToken).build();
        Request<Page<GiftcardTransaction>> request = giftcardAPI.transactionsHistory().list();
        assertThat(request, is(notNullValue()));
        Page<GiftcardTransaction> transactionsPage = request.execute();
        transactionsPage.forEach(this::assertIsValidGiftcardTransaction);
    }

    @Test
    public void testListGiftcardTransactionsWithFilters() throws Exception {
        
        int page = 1;
        int pageSize = 200;
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(LIVE).accessToken(accessToken).build();
        GiftcardTransactionFilter filter = new GiftcardTransactionFilter().withPage(page, pageSize)
                .productName("itunes");

        Request<Page<GiftcardTransaction>> request = giftcardAPI.transactionsHistory().list(filter);
        assertThat(request, is(notNullValue()));
        Page<GiftcardTransaction> transactionsPage = request.execute();
        transactionsPage.getContent().forEach(this::assertIsValidGiftcardTransaction);
        transactionsPage.forEach(this::assertIsValidGiftcardTransaction);
    }

    @Test
    public void testGetGiftcardTransactionById() throws Exception {

        Long transactionId = 813L;
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(SANDBOX).accessToken(sandboxAccessToken).build();
        Request<GiftcardTransaction> request = giftcardAPI.transactionsHistory().getById(transactionId);
        assertThat(request, is(notNullValue()));
        GiftcardTransaction transaction = request.execute();
        assertIsValidGiftcardTransaction(transaction);
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
