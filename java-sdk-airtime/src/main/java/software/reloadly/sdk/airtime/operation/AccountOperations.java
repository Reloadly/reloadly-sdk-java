package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import software.reloadly.sdk.airtime.dto.response.AccountBalanceInfo;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class AccountOperations extends BaseAirtimeOperation {

    private static final String END_POINT = "accounts";
    private static final String PATH_BALANCE = "balance";

    public AccountOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<AccountBalanceInfo> getBalance() {
        return createGetRequest(getBuilder(END_POINT).addPathSegments(PATH_BALANCE).build().toString(),
                new TypeReference<AccountBalanceInfo>() {
                }
        );
    }
}
