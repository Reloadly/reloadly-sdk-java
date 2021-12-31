package software.reloadly.sdk.core.enums;

import software.reloadly.sdk.core.constant.ServiceURLs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Service {
    AIRTIME(ServiceURLs.AIRTIME),
    GIFTCARD(ServiceURLs.GIFTCARD),
    AIRTIME_SANDBOX(ServiceURLs.AIRTIME_SANDBOX),
    GIFTCARD_SANDBOX(ServiceURLs.GIFTCARD_SANDBOX);

    private final String url;
}
