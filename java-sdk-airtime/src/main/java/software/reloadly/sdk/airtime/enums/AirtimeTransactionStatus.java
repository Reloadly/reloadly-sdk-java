package software.reloadly.sdk.airtime.enums;

import java.io.Serializable;

@SuppressWarnings("unused")
public enum AirtimeTransactionStatus implements Serializable {
    PROCESSING, SUCCESSFUL, REFUNDED, FAILED
}