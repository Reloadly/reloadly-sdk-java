package software.reloadly.sdk.core.internal.client;

import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@RequiredArgsConstructor
public abstract class BaseOperation {
    protected final HttpUrl baseUrl;
    protected final String apiToken;
    protected final OkHttpClient client;
}
