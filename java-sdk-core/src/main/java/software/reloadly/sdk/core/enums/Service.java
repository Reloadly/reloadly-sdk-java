package software.reloadly.sdk.core.enums;

import software.reloadly.sdk.core.constant.ServiceURLs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Service {
    AIRTIME(ServiceURLs.AIRTIME),
    AIRTIME_SANDBOX(ServiceURLs.AIRTIME_SANDBOX);

    private final String url;
}
