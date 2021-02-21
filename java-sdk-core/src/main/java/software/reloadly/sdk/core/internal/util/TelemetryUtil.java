package software.reloadly.sdk.core.internal.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import software.reloadly.sdk.core.internal.interceptor.TelemetryInterceptor;
import software.reloadly.sdk.core.internal.net.Telemetry;

public class TelemetryUtil {

    private static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";

    public static TelemetryInterceptor getTelemetryInterceptor(String libraryVersion, @Nullable String apiVersion) {
        String name = "reloadly-sdk-java";
        Asserter.assertNotBlank(libraryVersion, "Library version");
        if (StringUtils.isNotBlank(apiVersion)) {
            return new TelemetryInterceptor(new Telemetry(name, libraryVersion, apiVersion));
        }
        return new TelemetryInterceptor(new Telemetry(name, libraryVersion));
    }

    public static String getJDKVersion() {
        try {
            return System.getProperty(JAVA_SPECIFICATION_VERSION);
        } catch (Exception ignored) {
            return Runtime.class.getPackage().getSpecificationVersion();
        }
    }
}
