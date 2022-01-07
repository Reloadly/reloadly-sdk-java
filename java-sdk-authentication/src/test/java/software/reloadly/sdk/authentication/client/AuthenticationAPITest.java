package software.reloadly.sdk.authentication.client;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.authentication.AuthenticationAPIMockServer;
import software.reloadly.sdk.authentication.dto.request.OAuth2ClientCredentialsRequest;
import software.reloadly.sdk.authentication.dto.response.TokenHolder;
import software.reloadly.sdk.core.constant.ServiceURLs;
import software.reloadly.sdk.core.enums.Service;
import software.reloadly.sdk.core.exception.oauth.OAuthException;
import software.reloadly.sdk.core.internal.constant.GrantType;
import software.reloadly.sdk.core.internal.constant.HttpHeader;
import software.reloadly.sdk.core.internal.interceptor.TelemetryInterceptor;

import java.lang.reflect.Field;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.authentication.util.RecordedRequestMatcher.hasHeader;
import static software.reloadly.sdk.authentication.util.RecordedRequestMatcher.hasMethodAndPath;
import static software.reloadly.sdk.core.internal.constant.MediaType.APPLICATION_JSON;

public class AuthenticationAPITest {

    private static final String CLIENT_ID = "some-client-id";
    private static final String CLIENT_SECRET = "some-client-secret";
    private static final String ACCESS_DENIED_ERROR_RESPONSE = "src/test/resources/client/auth/error_access_denied.json";
    private static final String SUCCESS_RESPONSE = "src/test/resources/client/auth/success_token_response.json";

    private AuthenticationAPIMockServer server;

    @BeforeEach
    public void setUp() throws Exception {
        server = new AuthenticationAPIMockServer();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void shouldEnableTelemetryByDefault() throws NoSuchFieldException, IllegalAccessException {

        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().build();
        Field field = authenticationAPI.getClass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(authenticationAPI);

        assertThat(okHttpClient.interceptors(), hasItem(isA(TelemetryInterceptor.class)));

        for (Interceptor i : okHttpClient.interceptors()) {
            if (i instanceof TelemetryInterceptor) {
                TelemetryInterceptor telemetry = (TelemetryInterceptor) i;
                assertThat(telemetry.isEnabled(), is(true));
            }
        }
    }

    @Test
    public void shouldEnableTelemetryExplicitly() throws NoSuchFieldException, IllegalAccessException {

        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().enableTelemetry(true).build();
        Field field = authenticationAPI.getClass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(authenticationAPI);
        assertThat(okHttpClient.interceptors(), hasItem(isA(TelemetryInterceptor.class)));

        for (Interceptor i : okHttpClient.interceptors()) {
            if (i instanceof TelemetryInterceptor) {
                TelemetryInterceptor telemetry = (TelemetryInterceptor) i;
                assertThat(telemetry.isEnabled(), is(true));
            }
        }
    }

    @Test
    public void shouldDisableTelemetry() throws NoSuchFieldException, IllegalAccessException {
        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().enableTelemetry(false).build();

        Field field = authenticationAPI.getClass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(authenticationAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(TelemetryInterceptor.class))));
    }

    @Test
    public void shouldDisableLoggingByDefault() throws NoSuchFieldException, IllegalAccessException {
        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().build();
        Field field = authenticationAPI.getClass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(authenticationAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(HttpLoggingInterceptor.class))));
    }

    @Test
    public void shouldEnableLogging() throws NoSuchFieldException, IllegalAccessException {
        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().enableLogging(true).build();
        Field field = authenticationAPI.getClass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(authenticationAPI);

        assertThat(okHttpClient.interceptors(), hasItem(isA(HttpLoggingInterceptor.class)));
        for (Interceptor i : okHttpClient.interceptors()) {
            if (i instanceof HttpLoggingInterceptor) {
                HttpLoggingInterceptor logging = (HttpLoggingInterceptor) i;
                assertThat(logging.getLevel(), is(HttpLoggingInterceptor.Level.BODY));
            }
        }
    }

    @Test
    public void shouldDisableLoggingInterceptorExplicitly() throws IllegalAccessException, NoSuchFieldException {
        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().enableLogging(false).build();
        Field field = authenticationAPI.getClass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(authenticationAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(HttpLoggingInterceptor.class))));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdIsNull() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                AuthenticationAPI.builder().clientSecret(CLIENT_SECRET).build().clientCredentials().getAccessToken().execute());

        Assertions.assertEquals("'Client id' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenClientIdIsEmpty() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                AuthenticationAPI.builder().clientId(" ")
                        .clientSecret(CLIENT_SECRET).build().clientCredentials().getAccessToken().execute());

        Assertions.assertEquals("'Client id' cannot be null or empty!", exception.getMessage());
    }


    @Test
    public void shouldThrowExceptionWhenClientSecretIsNull() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                AuthenticationAPI.builder().clientId(CLIENT_ID).build()
                        .clientCredentials().getAccessToken().execute());

        Assertions.assertEquals("'Client secret' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenClientSecretIsEmpty() {

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                AuthenticationAPI.builder().clientId(CLIENT_ID).clientSecret(" ").build()
                        .clientCredentials().getAccessToken().execute());

        Assertions.assertEquals("'Client secret' cannot be null or empty!", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenTargetServiceIsMissing() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                AuthenticationAPI.builder().clientId(CLIENT_ID).clientSecret(CLIENT_SECRET).build()
                        .clientCredentials().getAccessToken().execute());

        Assertions.assertEquals("'Target service' cannot be null!", exception.getMessage());
    }

    @Test
    public void shouldCreateOAuth2ClientCredentialsTokenRequest() throws Exception {

        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().build();

        OAuth2ClientCredentialsOperation oAuthOperation = authenticationAPI.clientCredentials();
        Field baseUrlField = oAuthOperation.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(oAuthOperation, HttpUrl.parse(server.getBaseUrl()));

        OAuth2ClientCredentialsRequest tokenRequest = oAuthOperation.getAccessToken();
        assertThat(tokenRequest, is(notNullValue()));
        server.jsonResponse(SUCCESS_RESPONSE, 200);
        TokenHolder response = tokenRequest.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/oauth/token"));
        assertThat(recordedRequest, hasHeader(HttpHeader.CONTENT_TYPE, APPLICATION_JSON));
        assertThat(recordedRequest, hasHeader(HttpHeader.ACCEPT, APPLICATION_JSON));

        Map<String, Object> body = AuthenticationAPIMockServer.bodyFromRequest(recordedRequest);
        assertThat(body, hasEntry("grant_type", GrantType.CLIENT_CREDENTIALS));
        assertThat(body, hasEntry("client_id", CLIENT_ID));
        assertThat(body, hasEntry("client_secret", CLIENT_SECRET));

        assertThat(response, is(notNullValue()));
        assertThat(response.getToken(), not(emptyOrNullString()));
        assertThat(response.getTokenType(), not(emptyOrNullString()));
        assertThat(response.getExpiresIn(), is(notNullValue()));
    }

    @Test
    public void shouldCreateOAuth2ClientCredentialsTokenRequestWithAudience() throws Exception {

        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().build();

        OAuth2ClientCredentialsOperation oAuthOperation = authenticationAPI.clientCredentials();
        Field baseUrlField = oAuthOperation.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(oAuthOperation, HttpUrl.parse(server.getBaseUrl()));

        OAuth2ClientCredentialsRequest tokenRequest = oAuthOperation.getAccessToken();
        tokenRequest.setAudience(ServiceURLs.AIRTIME);
        assertThat(tokenRequest, is(notNullValue()));
        server.jsonResponse(SUCCESS_RESPONSE, 200);
        TokenHolder response = tokenRequest.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/oauth/token"));
        assertThat(recordedRequest, hasHeader(HttpHeader.CONTENT_TYPE, APPLICATION_JSON));
        assertThat(recordedRequest, hasHeader(HttpHeader.ACCEPT, APPLICATION_JSON));

        Map<String, Object> body = AuthenticationAPIMockServer.bodyFromRequest(recordedRequest);
        assertThat(body, hasEntry("grant_type", GrantType.CLIENT_CREDENTIALS));
        assertThat(body, hasEntry("client_id", CLIENT_ID));
        assertThat(body, hasEntry("client_secret", CLIENT_SECRET));

        assertThat(response, is(notNullValue()));
        assertThat(response.getToken(), not(emptyOrNullString()));
        assertThat(response.getTokenType(), not(emptyOrNullString()));
        assertThat(response.getExpiresIn(), is(notNullValue()));
    }

    @Test
    public void shouldCreateOauth2ClientCredentialsTokenRequestErrorResponse() throws Exception {

        AuthenticationAPI authenticationAPI = getAuthenticationAPIBuilder().build();

        OAuth2ClientCredentialsOperation oAuthOperation = authenticationAPI.clientCredentials();
        Field baseUrlField = oAuthOperation.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(oAuthOperation, HttpUrl.parse(server.getBaseUrl()));

        OAuth2ClientCredentialsRequest tokenRequest = oAuthOperation.getAccessToken();
        assertThat(tokenRequest, is(notNullValue()));
        server.jsonResponse(ACCESS_DENIED_ERROR_RESPONSE, 401);
        OAuthException exception = assertThrows(OAuthException.class, tokenRequest::execute);
        RecordedRequest recordedRequest = server.takeRequest();

        Assertions.assertEquals(exception.getHttpStatusCode(), 401);
        Assertions.assertEquals(exception.getMessage(), "Access Denied");

        assertThat(recordedRequest, hasMethodAndPath("POST", "/oauth/token"));
        assertThat(recordedRequest, hasHeader(HttpHeader.CONTENT_TYPE, APPLICATION_JSON));
        assertThat(recordedRequest, hasHeader(HttpHeader.ACCEPT, APPLICATION_JSON));

        Map<String, Object> body = AuthenticationAPIMockServer.bodyFromRequest(recordedRequest);
        assertThat(body, hasEntry("grant_type", GrantType.CLIENT_CREDENTIALS));
        assertThat(body, hasEntry("client_id", CLIENT_ID));
        assertThat(body, hasEntry("client_secret", CLIENT_SECRET));
    }

    @Test
    public void shouldCreateOAuth2ClientCredentialsTokenRequestWithAudienceGiftCard() throws Exception {

        AuthenticationAPI authenticationAPI = AuthenticationAPI.builder().clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET).service(Service.GIFTCARD).build();

        OAuth2ClientCredentialsOperation oAuthOperation = authenticationAPI.clientCredentials();
        Field baseUrlField = oAuthOperation.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(oAuthOperation, HttpUrl.parse(server.getBaseUrl()));

        OAuth2ClientCredentialsRequest tokenRequest = oAuthOperation.getAccessToken();
        tokenRequest.setAudience(ServiceURLs.AIRTIME);
        assertThat(tokenRequest, is(notNullValue()));
        server.jsonResponse(SUCCESS_RESPONSE, 200);
        TokenHolder response = tokenRequest.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/oauth/token"));
        assertThat(recordedRequest, hasHeader(HttpHeader.CONTENT_TYPE, APPLICATION_JSON));
        assertThat(recordedRequest, hasHeader(HttpHeader.ACCEPT, APPLICATION_JSON));

        Map<String, Object> body = AuthenticationAPIMockServer.bodyFromRequest(recordedRequest);
        assertThat(body, hasEntry("grant_type", GrantType.CLIENT_CREDENTIALS));
        assertThat(body, hasEntry("client_id", CLIENT_ID));
        assertThat(body, hasEntry("client_secret", CLIENT_SECRET));

        assertThat(response, is(notNullValue()));
        assertThat(response.getToken(), not(emptyOrNullString()));
        assertThat(response.getTokenType(), not(emptyOrNullString()));
        assertThat(response.getExpiresIn(), is(notNullValue()));
    }

    @Test
    public void shouldCreateOAuth2ClientCredentialsTokenRequestWithAudienceGiftCardSandbox() throws Exception {

        AuthenticationAPI authenticationAPI = AuthenticationAPI.builder().clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET).service(Service.GIFTCARD_SANDBOX).build();

        OAuth2ClientCredentialsOperation oAuthOperation = authenticationAPI.clientCredentials();
        Field baseUrlField = oAuthOperation.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(oAuthOperation, HttpUrl.parse(server.getBaseUrl()));

        OAuth2ClientCredentialsRequest tokenRequest = oAuthOperation.getAccessToken();
        tokenRequest.setAudience(ServiceURLs.AIRTIME);
        assertThat(tokenRequest, is(notNullValue()));
        server.jsonResponse(SUCCESS_RESPONSE, 200);
        TokenHolder response = tokenRequest.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        MatcherAssert.assertThat(recordedRequest, hasMethodAndPath("POST", "/oauth/token"));
        assertThat(recordedRequest, hasHeader(HttpHeader.CONTENT_TYPE, APPLICATION_JSON));
        assertThat(recordedRequest, hasHeader(HttpHeader.ACCEPT, APPLICATION_JSON));

        Map<String, Object> body = AuthenticationAPIMockServer.bodyFromRequest(recordedRequest);
        assertThat(body, hasEntry("grant_type", GrantType.CLIENT_CREDENTIALS));
        assertThat(body, hasEntry("client_id", CLIENT_ID));
        assertThat(body, hasEntry("client_secret", CLIENT_SECRET));

        assertThat(response, is(notNullValue()));
        assertThat(response.getToken(), not(emptyOrNullString()));
        assertThat(response.getTokenType(), not(emptyOrNullString()));
        assertThat(response.getExpiresIn(), is(notNullValue()));
    }

    private AuthenticationAPI.AuthenticationAPIBuilder getAuthenticationAPIBuilder() {
        return AuthenticationAPI.builder().clientId(CLIENT_ID).clientSecret(CLIENT_SECRET).service(Service.AIRTIME);
    }
}
