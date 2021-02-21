package software.reloadly.sdk.core.internal.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import software.reloadly.sdk.core.internal.util.TelemetryUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("WeakerAccess")
public class Telemetry {

    private static final String ENV_KEY = "env";
    private static final String JAVA_KEY = "java";
    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "api-version";
    public static final String HEADER_NAME = "Reloadly-Client";
    private static final String LIBRARY_VERSION_KEY = "reloadly-sdk-java";

    private final String name;
    private final String value;
    private final String apiVersion;
    private final String libraryVersion;
    private final Map<String, String> env;

    public Telemetry(String name, String libraryVersion) {
        this(name, libraryVersion, null);
    }

    public Telemetry(String name, String libraryVersion, @Nullable String apiVersion) {

        this.name = name;
        this.apiVersion = apiVersion;
        this.libraryVersion = libraryVersion;

        if (name == null) {
            env = Collections.emptyMap();
            value = null;
            return;
        }

        Map<String, Object> values = new HashMap<>();
        values.put(NAME_KEY, name);
        if (StringUtils.isNotBlank(apiVersion)) {
            values.put(VERSION_KEY, apiVersion);
        }

        HashMap<String, String> tmpEnv = new HashMap<>();
        tmpEnv.put(JAVA_KEY, TelemetryUtil.getJDKVersion());
        if (StringUtils.isNotBlank(libraryVersion)) {
            tmpEnv.put(LIBRARY_VERSION_KEY, libraryVersion);
        }
        this.env = Collections.unmodifiableMap(tmpEnv);
        values.put(ENV_KEY, env);

        String tmpValue;
        try {
            String json = new ObjectMapper().writeValueAsString(values);
            tmpValue = Base64.encodeBase64URLSafeString(json.getBytes());
        } catch (JsonProcessingException e) {
            tmpValue = null;
            e.printStackTrace();
        }
        value = tmpValue;
    }
}
