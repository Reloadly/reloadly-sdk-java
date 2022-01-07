package software.reloadly.sdk.airtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.internal.util.Asserter;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AsserterTest {

    @Test
    public void testAssertValidUrlThrowsAnExceptionWhenUrlIsNotValid() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                Asserter.assertValidUrl("some-invalid-url", "Url"));
        Assertions.assertEquals("'Url' must be a valid URL!", exception.getMessage());
    }

    @Test
    public void testAssertNotEmptyThrowsAnExceptionWhenUrlCollectionIsNull() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                Asserter.assertNotEmpty(null, "Urls"));
        Assertions.assertEquals("'Urls' cannot be null!", exception.getMessage());
    }

    @Test
    public void testAssertNotEmptyThrowsAnExceptionWhenUrlCollectionIsEmpty() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                Asserter.assertNotEmpty(new ArrayList<>(), "Urls"));
        Assertions.assertEquals("'Urls' cannot be empty!", exception.getMessage());
    }
}
