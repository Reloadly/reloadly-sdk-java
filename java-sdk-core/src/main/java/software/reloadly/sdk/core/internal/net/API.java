package software.reloadly.sdk.core.internal.net;

import lombok.AccessLevel;
import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import software.reloadly.sdk.core.internal.interceptor.TelemetryInterceptor;
import software.reloadly.sdk.core.internal.util.TelemetryUtil;
import software.reloadly.sdk.core.net.HttpOptions;
import software.reloadly.sdk.core.net.ProxyOptions;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.Duration.ofSeconds;
import static okhttp3.logging.HttpLoggingInterceptor.Level;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.PROXY_AUTHORIZATION_HEADER;

@Getter
public abstract class API {

    protected static final String KEY_CLIENT_ID = "client_id";
    protected static final String KEY_CLIENT_SECRET = "client_secret";

    protected final String clientId;
    protected final String clientSecret;
    @Getter(AccessLevel.NONE)
    protected final boolean enableLogging;
    @Getter(AccessLevel.NONE)
    protected final Boolean enableTelemetry;
    @Getter(AccessLevel.PROTECTED)
    protected final OkHttpClient client;
    @Getter(AccessLevel.NONE)
    private TelemetryInterceptor telemetryInterceptor;
    @Getter(AccessLevel.NONE)
    private HttpLoggingInterceptor httpLoggingInterceptor;
    @Getter(AccessLevel.NONE)
    private Set<String> headersToRedact;
    @Getter(AccessLevel.NONE)
    private String apiVersion;
    @Getter(AccessLevel.NONE)
    private final String libraryVersion;


    @SuppressWarnings("unused")
    public API(String clientId, String clientSecret, boolean enableLogging,
               List<String> redactHeaders, HttpOptions options, Boolean enableTelemetry, String libraryVersion) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.enableLogging = enableLogging;
        this.libraryVersion = libraryVersion;
        if (redactHeaders != null && !redactHeaders.isEmpty()) {
            this.headersToRedact = new HashSet<>(redactHeaders);
        }
        this.enableTelemetry = (enableTelemetry == null || enableTelemetry);
        client = buildClient(options == null ? new HttpOptions() : options);
    }

    public API(String clientId, String clientSecret,
               boolean enableLogging, List<String> redactHeaders,
               HttpOptions options, Boolean enableTelemetry, String libraryVersion, String apiVersion) {

        this.clientId = clientId;
        this.apiVersion = apiVersion;
        this.clientSecret = clientSecret;
        this.enableLogging = enableLogging;
        this.libraryVersion = libraryVersion;
        if (redactHeaders != null && !redactHeaders.isEmpty()) {
            this.headersToRedact = new HashSet<>(redactHeaders);
        }
        this.enableTelemetry = (enableTelemetry == null || enableTelemetry);
        client = buildClient(options == null ? new HttpOptions() : options);
    }

    public boolean isLoggingEnabled() {
        return enableLogging;
    }

    @Nullable
    protected HttpUrl createUrlFromString(String url) {
        try {
            return HttpUrl.parse(url);
        } catch (Exception e) {
            return null;
        }
    }

    private OkHttpClient buildClient(HttpOptions options) {

        Duration readTimeout = options.getConnectTimeout() == null ? ofSeconds(60) : options.getConnectTimeout();
        Duration writeTimeout = options.getConnectTimeout() == null ? ofSeconds(60) : options.getConnectTimeout();
        Duration connectTimeout = options.getConnectTimeout() == null ? ofSeconds(60) : options.getConnectTimeout();

        final ProxyOptions proxyOptions = options.getProxyOptions();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (proxyOptions != null) {
            //Set proxy
            clientBuilder.proxy(proxyOptions.getProxy());
            //Set authentication, if present
            final String proxyAuth = proxyOptions.getBasicAuthentication();
            if (proxyAuth != null) {
                clientBuilder.proxyAuthenticator((route, response) -> {
                    if (response.request().header(PROXY_AUTHORIZATION_HEADER) != null) {
                        return null;
                    }
                    return response.request().newBuilder().header(PROXY_AUTHORIZATION_HEADER, proxyAuth).build();
                });
            }
        }

        OkHttpClient.Builder builder = clientBuilder.connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .writeTimeout(writeTimeout);

        if (this.enableLogging) {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(Level.BODY);
            if (headersToRedact != null && !headersToRedact.isEmpty()) {
                headersToRedact.forEach(header -> {
                    if (StringUtils.isNotBlank(header.trim())) {
                        httpLoggingInterceptor.redactHeader(header.trim());
                    }
                });
            }
            builder.addInterceptor(httpLoggingInterceptor);
        }

        if (this.enableTelemetry) {
            telemetryInterceptor = TelemetryUtil.getTelemetryInterceptor(libraryVersion, apiVersion);
            telemetryInterceptor.setEnabled(true);
            builder.addInterceptor(telemetryInterceptor);
        }

        return builder.build();
    }
}
