package software.reloadly.sdk.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationAPIMockServer {

    private final MockWebServer server;

    public AuthenticationAPIMockServer() throws Exception {
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
                .addHeader("Content-Type", "application/json")
                .setBody(readTextFile(path));
        server.enqueue(response);
    }

    public static Map<String, Object> bodyFromRequest(RecordedRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        try (Buffer body = request.getBody()) {
            return mapper.readValue(body.inputStream(), mapType);
        }
    }
}
