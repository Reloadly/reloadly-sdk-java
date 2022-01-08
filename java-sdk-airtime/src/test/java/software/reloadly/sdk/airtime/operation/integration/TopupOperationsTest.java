package software.reloadly.sdk.airtime.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.Phone;
import software.reloadly.sdk.airtime.dto.request.EmailTopupRequest;
import software.reloadly.sdk.airtime.dto.request.PhoneTopupRequest;
import software.reloadly.sdk.airtime.dto.response.*;
import software.reloadly.sdk.airtime.enums.AirtimeTransactionStatus;
import software.reloadly.sdk.airtime.enums.DenominationType;
import software.reloadly.sdk.airtime.interfaces.IntegrationTest;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopupOperationsTest extends BaseIntegrationTest {

    @IntegrationTest
    public void testSendPhoneTopup() throws Exception {

        Long operatorId = 173L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();
        double amount = getValidAmountByOperatorId(airtimeAPI, operatorId);

        assertThat(amount, is(greaterThan(0.00)));
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+50936377111", CountryCode.HT))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(amount)
                .operatorId(operatorId).build();

        Request<TopupTransaction> request = airtimeAPI.topups().send(phoneTopupRequest);
        assertThat(request, is(notNullValue()));
        TopupTransaction topupTransaction = request.execute();

        assertIsValidTransaction(topupTransaction);
        assertThat(topupTransaction.getRecipientEmail(), is(nullValue()));
    }

    @IntegrationTest
    public void testSendPinBasedPhoneTopup() throws Exception {

        Long operatorId = 672L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();
        double amount = getValidAmountByOperatorId(airtimeAPI, operatorId);

        assertThat(amount, is(greaterThan(0.00)));
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone("+306907039456", CountryCode.GR))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(amount)
                .operatorId(operatorId).build();

        Request<TopupTransaction> request = airtimeAPI.topups().send(phoneTopupRequest);
        assertThat(request, is(notNullValue()));
        TopupTransaction topupTransaction = request.execute();

        assertIsValidTransaction(topupTransaction);
        assertThat(topupTransaction.getPinDetail(), is(notNullValue()));
        assertThat(topupTransaction.getRecipientEmail(), is(nullValue()));
    }

    @IntegrationTest
    public void testSendEmailTopup() throws Exception {

        Long operatorId = 683L;
        String recipientEmail = "testing@nauta.com.cu";
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();

        double amount = getValidAmountByOperatorId(airtimeAPI, operatorId);
        assertThat(amount, is(greaterThan(0.00)));
        EmailTopupRequest emailTopupRequest = EmailTopupRequest.builder()
                .recipientEmail(recipientEmail)
                .customIdentifier(UUID.randomUUID().toString())
                .amount(amount)
                .operatorId(operatorId).build();

        Request<TopupTransaction> request = airtimeAPI.topups().send(emailTopupRequest);
        assertThat(request, is(notNullValue()));
        TopupTransaction topupTransaction = request.execute();

        assertIsValidTransaction(topupTransaction);
        assertThat(topupTransaction.getRecipientPhone(), is(nullValue()));
        assertThat(topupTransaction.getRecipientEmail(), is(not(emptyOrNullString())));
        assertEquals(recipientEmail, topupTransaction.getRecipientEmail());
    }

    @IntegrationTest
    public void testSendPhoneTopupAsync() throws Exception {

        Long operatorId = 173L;
        String recipientPhone = "+50936377111";
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();

        double amount = getValidAmountByOperatorId(airtimeAPI, operatorId);
        PhoneTopupRequest phoneTopupRequest = PhoneTopupRequest.builder()
                .recipientPhone(new Phone(recipientPhone, CountryCode.HT))
                .customIdentifier(UUID.randomUUID().toString())
                .amount(amount)
                .operatorId(operatorId).build();

        Request<AsyncAirtimeResponse> request = airtimeAPI.topups().sendAsync(phoneTopupRequest);
        assertThat(request, is(notNullValue()));
        AsyncAirtimeResponse airtimeResponse = request.execute();

        assertThat(airtimeResponse, is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(greaterThan(0L)));
    }

    @IntegrationTest
    public void testSendEmailTopupAsync() throws Exception {

        Long operatorId = 683L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();
        double amount = getValidAmountByOperatorId(airtimeAPI, operatorId);

        EmailTopupRequest emailTopupRequest = EmailTopupRequest.builder()
                .recipientEmail("testing@nauta.com.cu")
                .customIdentifier(UUID.randomUUID().toString())
                .amount(amount)
                .operatorId(operatorId).build();

        Request<AsyncAirtimeResponse> request = airtimeAPI.topups().sendAsync(emailTopupRequest);
        assertThat(request, is(notNullValue()));
        AsyncAirtimeResponse airtimeResponse = request.execute();

        assertThat(airtimeResponse, is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(notNullValue()));
        assertThat(airtimeResponse.getTransactionId(), is(greaterThan(0L)));
    }

    @IntegrationTest
    public void testRetrievePhoneTransactionStatus() throws Exception {

        Long transactionId = 24995L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();
        Request<AirtimeTransactionStatusResponse> request = airtimeAPI.topups().getStatus(transactionId);
        assertThat(request, is(notNullValue()));
        AirtimeTransactionStatusResponse transactionStatus = request.execute();

        assertIsValidTransactionStatusResponse(transactionStatus);
        assertThat(transactionStatus.getTransaction().getRecipientEmail(), is(emptyOrNullString()));
        assertThat(transactionStatus.getTransaction().getRecipientPhone(), is(not(emptyOrNullString())));
        assertEquals(transactionId, transactionStatus.getTransaction().getId());
    }

    @IntegrationTest
    public void testRetrieveEmailTransactionStatus() throws Exception {

        Long transactionId = 24996L;
        AirtimeAPI airtimeAPI = AirtimeAPI.builder().accessToken(sandboxAccessToken).build();
        Request<AirtimeTransactionStatusResponse> request = airtimeAPI.topups().getStatus(transactionId);
        assertThat(request, is(notNullValue()));
        AirtimeTransactionStatusResponse transactionStatus = request.execute();

        assertIsValidTransactionStatusResponse(transactionStatus);
        assertThat(transactionStatus.getTransaction().getRecipientPhone(), is(emptyOrNullString()));
        assertThat(transactionStatus.getTransaction().getRecipientEmail(), is(not(emptyOrNullString())));
        assertEquals(transactionId, transactionStatus.getTransaction().getId());
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
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());

        int actualTransactionFieldsCount = transFields.size();
        String errorMsg = "Failed asserting that TopupTransaction::class contains " + expectedTransactionFieldsCount;
        errorMsg += " fields. It actually contains " + actualTransactionFieldsCount + " fields";
        assertThat(errorMsg, expectedTransactionFieldsCount == actualTransactionFieldsCount);

        topupTransactionFields.forEach(field -> assertThat(transaction, hasProperty(field)));

        if (transaction.getPinDetail() != null) {
            PinDetail pinDetail = transaction.getPinDetail();
            List<String> pinDFields = Arrays.stream(pinDetail.getClass().getDeclaredFields())
                    .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                            !f.getName().equalsIgnoreCase("$jacocoData") &&
                            !f.getName().equalsIgnoreCase("__$lineHits$__"))
                    ).map(Field::getName).collect(Collectors.toList());
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
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());
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
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());

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

    private double getValidAmountByOperatorId(AirtimeAPI airtimeAPI, Long operatorId) throws ReloadlyException {

        Operator operator = airtimeAPI.operators().getById(operatorId).execute();
        if (operator.getDenominationType().equals(DenominationType.FIXED)) {
            List<Double> amounts = operator.getFixedAmounts().stream()
                    .map(BigDecimal::doubleValue).collect(Collectors.toList());
            return amounts.get(new Random().nextInt(amounts.size())); //get an amount at random
        } else {
            return operator.getMinAmount().doubleValue();
        }
    }
}
