package software.reloadly.sdk.airtime.client;

import software.reloadly.sdk.airtime.AirtimeAPIMockServer;
import software.reloadly.sdk.core.internal.interceptor.TelemetryInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AirtimeAPITest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJyZWxvYWRseSIsImV4cCI6MTY3NTY0N" +
            "DE5OTgsImlhdCI6MTYwOTEzOTU5OH0.fKuCwsOC7d6oDEG2hZdQSwrtwQtxQUx9ueRXlt_9mtA";

    private AirtimeAPIMockServer server;

    @BeforeEach
    public void setUp() throws Exception {
        server = new AirtimeAPIMockServer();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void shouldEnableTelemetryByDefault() throws NoSuchFieldException, IllegalAccessException {

        AirtimeAPI airtimeAPI = getAirtimeAPIBuilder().build();
        Field field = airtimeAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(airtimeAPI);

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

        AirtimeAPI airtimeAPI = getAirtimeAPIBuilder().enableTelemetry(true).build();
        Field field = airtimeAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(airtimeAPI);
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
        AirtimeAPI airtimeAPI = getAirtimeAPIBuilder().enableTelemetry(false).build();

        Field field = airtimeAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(airtimeAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(TelemetryInterceptor.class))));
    }

    @Test
    public void shouldDisableLoggingByDefault() throws NoSuchFieldException, IllegalAccessException {
        AirtimeAPI airtimeAPI = getAirtimeAPIBuilder().build();
        Field field = airtimeAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(airtimeAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(HttpLoggingInterceptor.class))));
    }

    @Test
    public void shouldEnableLogging() throws NoSuchFieldException, IllegalAccessException {
        AirtimeAPI airtimeAPI = getAirtimeAPIBuilder().enableLogging(true).build();
        Field field = airtimeAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(airtimeAPI);

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
        AirtimeAPI airtimeAPI = getAirtimeAPIBuilder().enableLogging(false).build();
        Field field = airtimeAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(airtimeAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(HttpLoggingInterceptor.class))));
    }

    @Test
    public void shouldThrowExceptionWhenCredentialsAndAccessTokenAreMissing() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> AirtimeAPI.builder().build());
        String expected = "Either a valid access token or both client id & client secret must be provided";
        Assertions.assertEquals(expected, exception.getMessage());
    }

    private AirtimeAPI.AirtimeAPIBuilder getAirtimeAPIBuilder() {
        return AirtimeAPI.builder().accessToken(AirtimeAPITest.ACCESS_TOKEN);
    }

    private AirtimeAPI.AirtimeAPIBuilder getAirtimeAPIBuilder(String clientId, String clientSecret) {
        return AirtimeAPI.builder().clientId(clientId).clientSecret(clientSecret);
    }
}
