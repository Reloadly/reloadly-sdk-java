package software.reloadly.sdk.core.net;

import lombok.Getter;
import okhttp3.Credentials;
import org.apache.commons.lang3.StringUtils;
import software.reloadly.sdk.core.internal.util.Asserter;

import java.net.Proxy;

/**
 * Used to configure Java Proxy-related configurations.
 */
@Getter
public class ProxyOptions {

    private final Proxy proxy;
    private String proxyUsername;
    private char[] proxyPassword;
    private String basicAuthentication;

    /**
     * Builds a new instance using the given Proxy.
     * The Proxy will not have authentication unless {@link #basicAuthentication} is set.
     */
    public ProxyOptions(Proxy proxy) {
        Asserter.assertNotNull(proxy, "proxy");
        this.proxy = proxy;
    }

    public ProxyOptions(Proxy proxy, String proxyUsername, char[] proxyPassword) {
        Asserter.assertNotNull(proxy, "proxy");
        this.proxy = proxy;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        validateAndBuildAuthenticationCredentials();
    }

    /**
     * Validate and build the authentication value to use for this Proxy.
     */
    private void validateAndBuildAuthenticationCredentials() {
        if (StringUtils.isNotBlank(proxyUsername) || proxyPassword != null) {
            Asserter.assertNotBlank(proxyUsername, "Proxy username");
            Asserter.assertNotNull(proxyPassword, "Proxy password");
            this.basicAuthentication = Credentials.basic(proxyUsername, new String(proxyPassword));
        }
    }
}
