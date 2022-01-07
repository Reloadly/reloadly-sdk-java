package software.reloadly.sdk.core;

import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.constant.ServiceURLs;
import software.reloadly.sdk.core.internal.constant.GrantType;
import software.reloadly.sdk.core.internal.constant.HttpHeader;
import software.reloadly.sdk.core.internal.constant.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantTest {

    private static final String AIRTIME = "https://topups.reloadly.com";
    private static final String GIFTCARD = "https://giftcards.reloadly.com";
    private static final String AIRTIME_SANDBOX = "https://topups-sandbox.reloadly.com";
    private static final String GIFTCARD_SANDBOX = "https://giftcards-sandbox.reloadly.com";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";
    private static final String PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization";
    private static final String APPLICATION_JSON = "application/json";

    @Test
    public void testServiceUrls() {
        assertEquals(AIRTIME, ServiceURLs.AIRTIME);
        assertEquals(GIFTCARD, ServiceURLs.GIFTCARD);
        assertEquals(AIRTIME_SANDBOX, ServiceURLs.AIRTIME_SANDBOX);
        assertEquals(GIFTCARD_SANDBOX, ServiceURLs.GIFTCARD_SANDBOX);
    }

    @Test
    public void testGrantType() {
        assertEquals(CLIENT_CREDENTIALS, GrantType.CLIENT_CREDENTIALS);
    }

    @Test
    public void testHttpHeaders() {
        assertEquals(ACCEPT, HttpHeader.ACCEPT);
        assertEquals(CONTENT_TYPE, HttpHeader.CONTENT_TYPE);
        assertEquals(AUTHORIZATION, HttpHeader.AUTHORIZATION);
        assertEquals(PROXY_AUTHORIZATION_HEADER, HttpHeader.PROXY_AUTHORIZATION_HEADER);
    }

    @Test
    public void testMediaTypes() {
        assertEquals(APPLICATION_JSON, MediaType.APPLICATION_JSON);
    }
}
