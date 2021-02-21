package software.reloadly.sdk.airtime.dto.request;

import software.reloadly.sdk.airtime.dto.Phone;

public interface Topupable {
    Double getAmount();
    Long getOperatorId();
    Phone getSenderPhone();
    boolean isUseLocalAmount();
}
