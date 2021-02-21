package software.reloadly.sdk.core.internal.dto.request;

import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.exception.ReloadlyException;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;

public abstract class BaseRequest<T> implements Request<T> {

    private final OkHttpClient client;

    BaseRequest(OkHttpClient client) {
        this.client = client;
    }

    protected abstract okhttp3.Request createRequest() throws ReloadlyException;

    protected abstract T parseResponse(Response response) throws ReloadlyException;

    /**
     * Executes this request.
     *
     * @return the response body JSON decoded as T
     * @throws ReloadlyException if the request execution fails.
     */
    @Override
    public T execute() throws ReloadlyException {
        okhttp3.Request request = createRequest();
        try (Response response = client.newCall(request).execute()) {
            return parseResponse(response);
        } catch (ReloadlyException e) {
            throw e;
        } catch (IOException e) {
            throw new ReloadlyException("Failed to execute request", e);
        }
    }
}
