package software.reloadly.sdk.airtime.operation;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class ReportOperations extends BaseAirtimeOperation {

    public ReportOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public TransactionHistoryOperations transactionsHistory() {
        return new TransactionHistoryOperations(client, baseUrl, apiToken);
    }
}
