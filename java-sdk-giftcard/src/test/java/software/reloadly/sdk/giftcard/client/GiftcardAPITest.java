package software.reloadly.sdk.giftcard.client;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.internal.interceptor.TelemetryInterceptor;
import software.reloadly.sdk.giftcard.util.GiftcardAPIMockServer;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftcardAPITest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJyZWxvYWRseSIsImV4cCI6MTY3NTY0N" +
            "DE5OTgsImlhdCI6MTYwOTEzOTU5OH0.fKuCwsOC7d6oDEG2hZdQSwrtwQtxQUx9ueRXlt_9mtA";

    private GiftcardAPIMockServer server;

    @BeforeEach
    public void setUp() throws Exception {
        server = new GiftcardAPIMockServer();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void shouldEnableTelemetryByDefault() throws NoSuchFieldException, IllegalAccessException {

        GiftcardAPI giftcardAPI =  getGiftcardAPIBuilder().build();
        Field field = giftcardAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(giftcardAPI);

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

        GiftcardAPI giftcardAPI = getGiftcardAPIBuilder().enableTelemetry(true).build();
        Field field = giftcardAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(giftcardAPI);
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

        GiftcardAPI giftcardAPI = getGiftcardAPIBuilder().enableTelemetry(false).build();
        Field field = giftcardAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(giftcardAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(TelemetryInterceptor.class))));
    }

    @Test
    public void shouldDisableLoggingByDefault() throws NoSuchFieldException, IllegalAccessException {
        GiftcardAPI giftcardAPI = getGiftcardAPIBuilder().build();
        Field field = giftcardAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(giftcardAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(HttpLoggingInterceptor.class))));
    }

    @Test
    public void shouldEnableLogging() throws NoSuchFieldException, IllegalAccessException {

        GiftcardAPI giftcardAPI = getGiftcardAPIBuilder().enableLogging(true).build();
        Field field = giftcardAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(giftcardAPI);

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
        GiftcardAPI giftcardAPI = getGiftcardAPIBuilder().enableLogging(false).build();
        Field field = giftcardAPI.getClass().getSuperclass().getSuperclass().getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient okHttpClient = (OkHttpClient) field.get(giftcardAPI);
        assertThat(okHttpClient.interceptors(), not(hasItem(isA(HttpLoggingInterceptor.class))));
    }

    @Test
    public void shouldThrowExceptionWhenCredentialsAndAccessTokenAreMissing() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> GiftcardAPI.builder().build());
        String expected = "Either a valid access token or both client id & client secret must be provided";
        Assertions.assertEquals(expected, exception.getMessage());
    }

    private GiftcardAPI.GiftcardAPIBuilder getGiftcardAPIBuilder() {
        return GiftcardAPI.builder().accessToken(ACCESS_TOKEN);
    }
}
