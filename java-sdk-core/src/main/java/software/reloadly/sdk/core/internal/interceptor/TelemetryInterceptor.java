package software.reloadly.sdk.core.internal.interceptor;

import lombok.Getter;
import lombok.Setter;
import software.reloadly.sdk.core.internal.net.Telemetry;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@Setter
public class TelemetryInterceptor implements Interceptor {

    private boolean enabled;
    private Telemetry telemetry;

    public TelemetryInterceptor(Telemetry telemetry) {
        this.telemetry = telemetry;
        this.enabled = true;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (!enabled) {
            return chain.proceed(chain.request());
        }

        okhttp3.Request request = chain.request()
                .newBuilder()
                .addHeader(Telemetry.HEADER_NAME, telemetry.getValue())
                .build();
        return chain.proceed(request);
    }
}

