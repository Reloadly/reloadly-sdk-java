package software.reloadly.sdk.giftcard.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.dto.request.GiftCardOrderRequest;
import software.reloadly.sdk.giftcard.dto.response.GiftcardInfo;
import software.reloadly.sdk.giftcard.dto.response.GiftcardTransaction;

import java.util.List;

public class GiftcardOrdersOperations extends BaseGiftcardOperation {

    private static final String END_POINT = "orders";
    private static final String ORDERS_REDEEM_PATH_SEGMENT = "orders/transactions";

    public GiftcardOrdersOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<GiftcardTransaction> placeOrder(GiftCardOrderRequest request) {
        validateRequest(request);
        return createPostRequest(getBuilder(END_POINT).build().toString(), request,
                new TypeReference<GiftcardTransaction>() {
                }
        );
    }

    public Request<List<GiftcardInfo>> redeem(Long transactionId) {
        Asserter.assertNotNull(transactionId, "Transaction id");
        Asserter.assertGreaterThanZero(transactionId, "Transaction id");
        String endPoint = ORDERS_REDEEM_PATH_SEGMENT + "/" + transactionId + "/cards";
        return createGetRequest(getBuilder(endPoint).build().toString(), new TypeReference<List<GiftcardInfo>>() {
        });
    }

    private void validateRequest(GiftCardOrderRequest request) {

        Asserter.assertNotNull(request.getQuantity(), "Quantity");
        Asserter.assertGreaterThanZero(request.getQuantity(), "Quantity");
        Asserter.assertNotNull(request.getProductId(), "Product id");
        Asserter.assertGreaterThanZero(request.getProductId(), "Product id");
        Asserter.assertNotBlank(request.getSenderName(), "Sender name");
        Asserter.assertNotNull(request.getUnitPrice(), "Unit price");
        Asserter.assertGreaterThanZero(request.getUnitPrice(), "Unit price");

        if (request.getRecipientPhone() == null && StringUtils.isBlank(request.getRecipientEmail())) {
            throw new IllegalArgumentException("Either recipient email or recipient phone is required");
        }

        if (request.getRecipientPhone() != null) {
            Asserter.assertNotBlank(request.getRecipientPhone().getPhoneNumber(), "Recipient phone number");
            Asserter.assertNotNull(request.getRecipientPhone().getCountryCode(), "Recipient phone country code");

            String phoneNumber = request.getRecipientPhone().getPhoneNumber().replace("+", "");
            Asserter.assertValidPhoneNumber(phoneNumber, "Recipient phone number");
        }

        if (StringUtils.isNotBlank(request.getRecipientEmail())) {
            Asserter.assertNotBlank(request.getRecipientEmail(), "Recipient email");
            Asserter.assertValidEmail(request.getRecipientEmail(), "Recipient email");
        }
    }
}
