package software.reloadly.sdk.core.net;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

import static java.time.Duration.ofSeconds;

/**
 * Used to configure additional configuration options when customizing the API client instance.
 */
@Getter
public class HttpOptions {

    private final Duration readTimeout;
    private final Duration writeTimeout;
    private final Duration connectTimeout;
    private final ProxyOptions proxyOptions;

    @Builder
    public HttpOptions(Duration readTimeout, Duration writeTimeout, Duration connectTimeout, ProxyOptions proxyOptions) {
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.connectTimeout = connectTimeout;
        this.proxyOptions = proxyOptions;
    }

    public HttpOptions() {
        proxyOptions = null;
        this.readTimeout = ofSeconds(180);
        this.writeTimeout = ofSeconds(180);
        this.connectTimeout = ofSeconds(180);
    }
}
