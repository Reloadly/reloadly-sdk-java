package software.reloadly.sdk.core.internal.filter;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BaseFilter {
    protected final Map<String, Object> parameters = new HashMap<>();
}
