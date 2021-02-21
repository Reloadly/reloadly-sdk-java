package software.reloadly.sdk.core.internal.util;

import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Collection;

public abstract class Asserter {

    public static void assertNotNull(Object value, String name) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(String.format("'%s' cannot be null!", name));
        }
    }

    public static void assertGreaterThanZero(Number value, String name) throws IllegalArgumentException {
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(String.format("'%s' must be greater than zero!", name));
        }
    }

    public static void assertNotBlank(String value, String name) throws IllegalArgumentException {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(String.format("'%s' cannot be null or empty!", name));
        }
    }

    public static void assertValidUrl(String value, String name) throws IllegalArgumentException {
        if (StringUtils.isBlank(value) || HttpUrl.parse(value) == null) {
            throw new IllegalArgumentException(String.format("'%s' must be a valid URL!", name));
        }
    }

    public static void assertNotEmpty(Collection<Object> value, String name) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(String.format("'%s' cannot be null!", name));
        }
        if (value.size() == 0) {
            throw new IllegalArgumentException(String.format("'%s' cannot be empty!", name));
        }
    }

    public static void assertValidEmail(String email, String name) {
        assertNotBlank(email, name);
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(String.format("'%s' is not a valid email address!", name));
        }
    }
}
