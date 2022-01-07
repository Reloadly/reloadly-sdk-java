package software.reloadly.sdk.airtime.util;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import software.reloadly.sdk.core.internal.constant.HttpHeader;
import software.reloadly.sdk.core.internal.enums.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AirtimeAPIMockServer {

    public static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJyZWxvYWRseSIsImV4cCI6MTY3NTY0N" +
            "DE5OTgsImlhdCI6MTYwOTEzOTU5OH0.fKuCwsOC7d6oDEG2hZdQSwrtwQtxQUx9ueRXlt_9mtA";

    private final MockWebServer server;

    public AirtimeAPIMockServer() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    public void stop() throws IOException {
        server.shutdown();
    }

    public String getBaseUrl() {
        return server.url("").toString().replaceAll("/\\z", "");
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    private String readTextFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public void jsonResponse(String path, int statusCode) throws IOException {
        MockResponse response = new MockResponse()
                .setResponseCode(statusCode)
                .addHeader(HttpHeader.CONTENT_TYPE, Version.AIRTIME_V1.getValue())
                .setBody(readTextFile(path));
        server.enqueue(response);
    }

}
