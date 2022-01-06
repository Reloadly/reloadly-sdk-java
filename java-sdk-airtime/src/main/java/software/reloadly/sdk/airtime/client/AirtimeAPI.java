package software.reloadly.sdk.airtime.client;

import lombok.Builder;
import okhttp3.HttpUrl;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import software.reloadly.sdk.airtime.operation.*;
import software.reloadly.sdk.authentication.client.AuthenticationAPI;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.constant.HttpHeader;
import software.reloadly.sdk.core.internal.dto.request.CustomizableRequest;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.enums.Version;
import software.reloadly.sdk.core.internal.net.ServiceAPI;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.core.net.HttpOptions;

import java.io.FileReader;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static software.reloadly.sdk.core.enums.Environment.LIVE;
import static software.reloadly.sdk.core.enums.Service.AIRTIME;
import static software.reloadly.sdk.core.enums.Service.AIRTIME_SANDBOX;

public class AirtimeAPI extends ServiceAPI {

    private final HttpUrl baseUrl;
    private final Environment environment;

    @Builder
    @SuppressWarnings("unused")
    public AirtimeAPI(String clientId, String clientSecret, String accessToken,
                      Environment environment, boolean enableLogging,
                      List<String> redactHeaders, HttpOptions options, Boolean enableTelemetry) {

        super(clientId, clientSecret, accessToken, enableLogging,
                redactHeaders, options, enableTelemetry, getSDKVersion(), Version.AIRTIME_V1.getValue());

        validateCredentials();
        this.environment = environment;
        baseUrl = createBaseUrl(environment);
    }

    public OperatorOperations operators() throws ReloadlyException {
        return new OperatorOperations(client, baseUrl, retrieveAccessToken());
    }

    public CountryOperations countries() throws ReloadlyException {
        return new CountryOperations(client, baseUrl, retrieveAccessToken());
    }

    public AccountOperations accounts() throws ReloadlyException {
        return new AccountOperations(client, baseUrl, retrieveAccessToken());
    }

    public DiscountOperations discounts() throws ReloadlyException {
        return new DiscountOperations(client, baseUrl, retrieveAccessToken());
    }

    public PromotionOperations promotions() throws ReloadlyException {
        return new PromotionOperations(client, baseUrl, retrieveAccessToken());
    }

    public TopupOperations topups() throws ReloadlyException {
        return new TopupOperations(client, baseUrl, retrieveAccessToken());
    }

    public ReportOperations reports() throws ReloadlyException {
        return new ReportOperations(client, baseUrl, retrieveAccessToken());
    }

    /**
     * Retrieve a new API access token to use on new calls.
     * This is useful when the token is about to expire or already has.
     *
     * @param request - The request to refresh the token for
     */
    @Override
    public void refreshAccessToken(Request<?> request) throws ReloadlyException {
        this.accessToken = null;
        CustomizableRequest<?> customizableRequest = (CustomizableRequest<?>) request;
        String newAccessToken = retrieveAccessToken();
        customizableRequest.addHeader(HttpHeader.AUTHORIZATION, "Bearer " + newAccessToken);
    }

    private HttpUrl createBaseUrl(Environment environment) {
        Service service = getServiceByEnvironment(environment);
        Asserter.assertNotNull(service, "Service");
        HttpUrl url = HttpUrl.parse(service.getAudience());
        if (url == null) {
            throw new IllegalArgumentException(
                    "The airtime base url had an invalid format and couldn't be parsed as a URL."
            );
        }
        return url;
    }

    private Service getServiceByEnvironment(Environment environment) {
        return (environment != null && environment.equals(LIVE)) ? AIRTIME : AIRTIME_SANDBOX;
    }

    private String retrieveAccessToken() throws ReloadlyException {
        String accessToken = doGetAccessToken(getServiceByEnvironment(environment));
        if (cacheAccessToken) {
            this.accessToken = accessToken;
        }
        return accessToken;
    }

    private String doGetAccessToken(Service service) throws ReloadlyException {
        return isNotBlank(accessToken) ? accessToken : AuthenticationAPI.builder().service(service)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .enableLogging(enableLogging)
                .enableTelemetry(enableTelemetry)
                .build().clientCredentials().getAccessToken().execute().getToken();
    }

    private static String getSDKVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;
        try {
            FileReader fileReader = new FileReader("./java-sdk-airtime/pom.xml");
            model = reader.read(fileReader);
        } catch (Exception e) {
            try {
                model = reader.read(new FileReader("../java-sdk-airtime/pom.xml"));
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
