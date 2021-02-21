package software.reloadly.sdk.core.internal.dto.request;

import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

public interface CustomizableRequest<T> extends Request<T> {

    CustomizableRequest<T> addHeader(String name, String value);

    CustomizableRequest<T> addParameter(String name, Object value);

    CustomizableRequest<T> setBody(Object body);
}
