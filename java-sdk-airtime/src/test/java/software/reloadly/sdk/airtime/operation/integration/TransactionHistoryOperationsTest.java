package software.reloadly.sdk.airtime.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.TopupTransaction;
import software.reloadly.sdk.airtime.filter.TransactionHistoryFilter;
import software.reloadly.sdk.airtime.interfaces.IntegrationTest;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransactionHistoryOperationsTest extends BaseIntegrationTest {

    @IntegrationTest
    public void testListTransactionHistoryWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();

        Request<Page<TopupTransaction>> request = airtimeAPI.reports().transactionsHistory().list();
        assertThat(request, is(notNullValue()));
        Page<TopupTransaction> transactionHistoryPage = request.execute();
        transactionHistoryPage.getContent().forEach(this::assertIsValidTransactionHistory);
    }

    @IntegrationTest
    public void testListTransactionHistoryWithFilters() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();

        int page = 1;
        int pageSize = 100;
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 5, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 1, 5, 23, 59, 59);
        TransactionHistoryFilter filter = new TransactionHistoryFilter().withPage(page, pageSize)
                .startDate(startDate).endDate(endDate).operatorId(173L).countryCode(CountryCode.HT)
                .operatorName("Digicel Haiti");

        Request<Page<TopupTransaction>> request = airtimeAPI.reports().transactionsHistory().list(filter);
        assertThat(request, is(notNullValue()));
        Page<TopupTransaction> transactionHistoryPage = request.execute();
        assertThat(transactionHistoryPage.getContent().size(), is(greaterThan(0)));
        transactionHistoryPage.getContent().forEach(this::assertIsValidTransactionHistory);
    }

    @IntegrationTest
    public void testListTransactionHistoryFilteredByCustomIdentifier() throws Exception {

        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();

        int page = 1;
        int pageSize = 100;
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 5, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 1, 5, 23, 59, 59);
        TransactionHistoryFilter filter = new TransactionHistoryFilter().withPage(page, pageSize)
                .startDate(startDate).endDate(endDate).customIdentifier("c5bddf94-f279-44a3-bcf1-2b5a9b5fe531");

        Request<Page<TopupTransaction>> request = airtimeAPI.reports().transactionsHistory().list(filter);
        assertThat(request, is(notNullValue()));
        Page<TopupTransaction> transactionHistoryPage = request.execute();
        assertThat(transactionHistoryPage.getContent().size(), is(greaterThan(0)));
        transactionHistoryPage.getContent().forEach(this::assertIsValidTransactionHistory);
    }

    @IntegrationTest
    public void testGetById() throws Exception {

        Long transactionId = 24995L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();

        Request<TopupTransaction> request = airtimeAPI.reports().transactionsHistory().getById(transactionId);
        assertThat(request, is(notNullValue()));
        TopupTransaction topupTransaction = request.execute();
        assertIsValidTransactionHistory(topupTransaction);
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
