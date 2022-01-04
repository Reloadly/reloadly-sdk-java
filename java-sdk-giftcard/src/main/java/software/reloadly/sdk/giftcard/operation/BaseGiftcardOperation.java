package software.reloadly.sdk.giftcard.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import software.reloadly.sdk.core.internal.client.BaseOperation;
import software.reloadly.sdk.core.internal.constant.HttpHeader;
import software.reloadly.sdk.core.internal.constant.MediaType;
import software.reloadly.sdk.core.internal.dto.request.CustomRequest;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;
import software.reloadly.sdk.core.internal.filter.QueryFilter;

public class BaseGiftcardOperation extends BaseOperation {

    BaseGiftcardOperation(HttpUrl baseUrl, String apiToken, OkHttpClient client) {
        super(baseUrl, apiToken, client);
    }

    HttpUrl.Builder buildFilters(QueryFilter filter, String endPoint) {
        HttpUrl.Builder builder = getBuilder(endPoint);

        if (filter != null) {
            filter.getParameters().forEach((key, value) -> builder.addQueryParameter(key, String.valueOf(value)));
        }

        return builder;
    }

    protected <T> Request<T> createGetRequest(String url, TypeReference<T> type) {
        CustomRequest<T> request = new CustomRequest<>(client, url, "GET", type);
        request.addHeader(HttpHeader.ACCEPT, Version.GIFTCARD_V1.getValue());
        request.addHeader(HttpHeader.AUTHORIZATION, "Bearer " + apiToken);
        return request;
    }

    protected <T> Request<T> createPostRequest(String url, Object body, TypeReference<T> type) {
        return new CustomRequest<>(client, url, "POST", type)
                .addHeader(HttpHeader.ACCEPT, Version.GIFTCARD_V1.getValue())
                .addHeader(HttpHeader.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .addHeader(HttpHeader.AUTHORIZATION, "Bearer " + apiToken)
                .setBody(body);
    }

    protected HttpUrl.Builder getBuilder(String endPoint) {
        return baseUrl.newBuilder().addPathSegments(endPoint);
    }
}
