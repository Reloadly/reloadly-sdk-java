package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import software.reloadly.sdk.airtime.dto.Phone;
import software.reloadly.sdk.airtime.dto.request.EmailTopupRequest;
import software.reloadly.sdk.airtime.dto.request.PhoneTopupRequest;
import software.reloadly.sdk.airtime.dto.request.TopupRequest;
import software.reloadly.sdk.airtime.dto.response.TopupTransaction;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class TopupOperations extends BaseAirtimeOperation {

    private static final String END_POINT = "topups";

    public TopupOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<TopupTransaction> send(TopupRequest request) {
        validateTopupRequest(request);
        return createPostRequest(getBuilder(END_POINT).build().toString(), request,
                new TypeReference<TopupTransaction>() {
                }
        );
    }

    private void validateTopupRequest(TopupRequest request) {

        Asserter.assertNotNull(request.getAmount(), "Amount");
        Asserter.assertGreaterThanZero(request.getAmount(), "Amount");
        Asserter.assertNotNull(request.getOperatorId(), "Operator id");
        Asserter.assertGreaterThanZero(request.getOperatorId(), "Operator id");

        if (request instanceof PhoneTopupRequest) {
            if (request.getSenderPhone() != null) {
                assertValidPhone(request.getSenderPhone(), "senderPhone");
            }
            assertValidPhone(((PhoneTopupRequest) request).getRecipientPhone(), "recipientPhone");
        } else if (request instanceof EmailTopupRequest) {
            Asserter.assertValidEmail(((EmailTopupRequest) request).getRecipientEmail(), "Recipient email");
        }
    }

    private void assertValidPhone(Phone phone, @Nullable String fieldName) {

        String messagePrefix1 = "Phone";
        String messagePrefix2 = "Phone number";
        String messagePrefix3 = "Phone country code";
        if (StringUtils.isNotBlank(fieldName) && fieldName.equalsIgnoreCase("recipientPhone")) {
            messagePrefix1 = "Recipient phone";
            messagePrefix2 = "Recipient phone number";
            messagePrefix3 = "Recipient phone country code";
        } else if (StringUtils.isNotBlank(fieldName) && fieldName.equalsIgnoreCase("senderPhone")) {
            messagePrefix1 = "Sender phone";
            messagePrefix2 = "Sender phone number";
            messagePrefix3 = "Sender phone country code";
        }

        Asserter.assertNotNull(phone, messagePrefix1);
        Asserter.assertNotBlank(phone.getNumber(), messagePrefix2);
        String number = phone.getNumber().replace("+", "").replace(" ", "").trim();
        if (!(number.length() > 3 && number.matches("[0-9]+"))) {
            throw new IllegalArgumentException(
                    String.format("'%s' must contain only numbers and an optional leading '+' sign!", messagePrefix2));
        }

        Asserter.assertNotNull(phone.getCountryCode(), messagePrefix3);
    }
}
