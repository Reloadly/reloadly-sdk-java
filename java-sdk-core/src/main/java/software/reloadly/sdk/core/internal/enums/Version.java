package software.reloadly.sdk.core.internal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Version {

    AIRTIME_V1("application/com.reloadly.topups-v1+json");

    private final String value;
}
