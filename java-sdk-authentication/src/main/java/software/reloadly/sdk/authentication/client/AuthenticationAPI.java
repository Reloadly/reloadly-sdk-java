package software.reloadly.sdk.authentication.client;

import lombok.Builder;
import okhttp3.HttpUrl;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.internal.enums.Version;
import software.reloadly.sdk.core.internal.net.API;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.core.net.HttpOptions;

import java.io.FileReader;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Class that provides an implementation of some of the Authentication and Authorization API methods
 */
public class AuthenticationAPI extends API {

    private static final String BASE_URL = "https://auth.reloadly.com";

    private final HttpUrl baseUrl;
    private final Service service;

    @Builder
    public AuthenticationAPI(String clientId, String clientSecret,
                             Service service, boolean enableLogging,
                             List<String> redactHeaders, HttpOptions options, Boolean enableTelemetry) {

        super(clientId, clientSecret, enableLogging, redactHeaders, options, enableTelemetry, getSDKVersion(),
                Version.AUTHENTICATION_V1.getValue()
        );

        Asserter.assertNotBlank(clientId, "Client id");
        Asserter.assertNotBlank(clientSecret, "Client secret");
        Asserter.assertNotNull(service, "Target service");
        this.service = service;

        baseUrl = createUrlFromString(BASE_URL);
        if (baseUrl == null) {
            throw new IllegalArgumentException(
                    "The oauth base url had an invalid format and couldn't be parsed as an URL."
            );
        }
    }

    public OAuth2ClientCredentialsOperation clientCredentials() {
        return new OAuth2ClientCredentialsOperation(baseUrl, clientId, service, clientSecret, client);
    }

    private static String getSDKVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;
        try {
            model = reader.read(new FileReader("./java-sdk-authentication/pom.xml"));
        } catch (Exception e) {
            try {
                model = reader.read(new FileReader("../java-sdk-authentication/pom.xml"));
            } catch (Exception ex) {
                return "MISSING";
            }

            if (model == null) {
                return "MISSING";
            }
        }

        return isBlank(model.getVersion()) ? "MISSING" : model.getVersion();
    }
}
