package software.reloadly.sdk.core.exception;

import java.io.IOException;

/**
 * Class that represents an error captured when executing an http request to the Reloadly Server.
 */
public class ReloadlyException extends IOException {

    public ReloadlyException(String message){
        super(message);
    }

    public ReloadlyException(String message, Throwable cause){
        super(message, cause);
    }
}
