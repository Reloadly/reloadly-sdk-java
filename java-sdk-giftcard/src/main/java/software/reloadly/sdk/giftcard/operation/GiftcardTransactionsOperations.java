package software.reloadly.sdk.giftcard.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.dto.response.GiftcardTransaction;
import software.reloadly.sdk.giftcard.filter.GiftcardTransactionFilter;

public class GiftcardTransactionsOperations extends BaseGiftcardOperation {

    private static final String END_POINT = "reports/transactions";

    public GiftcardTransactionsOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<GiftcardTransaction>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(),
                new TypeReference<Page<GiftcardTransaction>>() {
                }
        );
    }

    public Request<Page<GiftcardTransaction>> list(GiftcardTransactionFilter filter) {
        return createGetRequest(buildFilters(filter, END_POINT).build().toString(),
                new TypeReference<Page<GiftcardTransaction>>() {
                }
        );
    }

    public Request<GiftcardTransaction> getById(Long transactionId) {
        Asserter.assertNotNull(transactionId, "Transaction id");
        Asserter.assertGreaterThanZero(transactionId, "Transaction id");
        String endPoint = END_POINT + "/" + transactionId;
        return createGetRequest(getBuilder(endPoint).build().toString(),
                new TypeReference<GiftcardTransaction>() {
                }
        );
    }
}
