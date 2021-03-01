package software.reloadly.sdk.core.internal.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import software.reloadly.sdk.core.dto.APIError;
import software.reloadly.sdk.core.exception.APIException;
import software.reloadly.sdk.core.exception.RateLimitException;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.util.ExceptionUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static software.reloadly.sdk.core.internal.constant.MediaType.APPLICATION_JSON;

@SuppressWarnings("WeakerAccess")
public class CustomRequest<T> extends BaseRequest<T> implements CustomizableRequest<T> {

    private final String url;
    private final String method;
    private final ObjectMapper mapper;
    private final TypeReference<T> tType;
    private final Map<String, String> headers;
    private final Map<String, Object> parameters;
    private Object body;

    private static final int STATUS_CODE_TOO_MANY_REQUEST = 429;

    CustomRequest(OkHttpClient client, String url, String method, ObjectMapper mapper, TypeReference<T> tType) {
        super(client);
        this.url = url;
        this.method = method;
        this.mapper = mapper;
        this.tType = tType;
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
    }

    public CustomRequest(OkHttpClient client, String url, String method, TypeReference<T> tType) {
        this(client, url, method, new ObjectMapper(), tType);
    }

    @Override
    protected Request createRequest() throws ReloadlyException {
        Request.Builder builder = new Request.Builder().url(url).method(method, createBody());
        headers.forEach(builder::addHeader);
        return builder.build();
    }

    @Override
    protected T parseResponse(Response response) throws ReloadlyException {
        if (!response.isSuccessful()) {
            throw createResponseException(response);
        }

        try (ResponseBody body = response.body()) {
            if (body == null) {
                throw new Exception();
            }
            String payload = body.string();
            return mapper.readValue(payload, tType);
        } catch (Exception e) {
            String path = getPath(response);
            throw new APIException("Failed to parse json body", response.code(), path, e);
        }
    }

    @Override
    public CustomRequest<T> addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public CustomRequest<T> addParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    @Override
    public CustomRequest<T> setBody(Object value) {
        body = value;
        return this;
    }

    protected RequestBody createBody() throws ReloadlyException {
        if (body == null && parameters.isEmpty()) {
            return null;
        }
        try {
            byte[] jsonBody = mapper.writeValueAsBytes(body != null ? body : parameters);
            return RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonBody);
        } catch (JsonProcessingException e) {
            throw new ReloadlyException("Couldn't create the request body.", e);
        }
    }

    protected ReloadlyException createResponseException(Response response) {
        if (response.code() == STATUS_CODE_TOO_MANY_REQUEST) {
            return createRateLimitException(response);
        }

        Reader streamReader;
        try (ResponseBody body = response.body()) {

            if (body == null) {
                throw new Exception("Operation failed");
            }
            streamReader = body.charStream();
            return ExceptionUtil.convert(new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create().fromJson(streamReader, APIError.class), response.code());
        } catch (Exception e) {
            body = (body == null) ? "No response from server, please try again or contact support" : body.toString();
            return new APIException(body.toString(), response.code(), getPath(response), e);
        }
    }

    private RateLimitException createRateLimitException(Response response) {

        RateLimitException rateLimitException = (RateLimitException) createResponseException(response);

        // -1 as default value if the header could not be found.
        String resetValue = response.header("X-RateLimit-Reset", "-1");
        String limitValue = response.header("X-RateLimit-Limit", "-1");
        String remainingValue = response.header("X-RateLimit-Remaining", "-1");

        resetValue = resetValue == null ? "-1" : resetValue;
        limitValue = limitValue == null ? "-1" : limitValue;
        remainingValue = remainingValue == null ? "-1" : remainingValue;

        long limit = Long.parseLong(limitValue);
        long remaining = Long.parseLong(remainingValue);
        long reset = Long.parseLong(resetValue);

        rateLimitException.setLimit(limit);
        rateLimitException.setRemaining(remaining);
        rateLimitException.setExpectedResetTimestamp(reset);

        return rateLimitException;
    }

    private String getPath(Response response) {
        return StringUtils.join(response.request().url().pathSegments(), "/");
    }
}
