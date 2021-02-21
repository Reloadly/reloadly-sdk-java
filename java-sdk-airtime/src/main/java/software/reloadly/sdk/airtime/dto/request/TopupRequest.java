package software.reloadly.sdk.airtime.dto.request;

import software.reloadly.sdk.airtime.dto.Phone;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public abstract class TopupRequest implements Topupable, Serializable {

    private static final long serialVersionUID = 4862758313864789228L;

    /**
     * Amount (in sender's currency) to credit recipient phone for
     */
    private final Double amount;

    /**
     * Unique identifier of the destination mobile operator id
     */
    private final Long operatorId;

    /**
     * Phone number of user requesting to credit the recipient phone, this field is optional.
     */
    private final Phone senderPhone;

    /**
     * Indicates whether topup amount is a local amount (as in the same currency as the destination country)
     */
    private final boolean useLocalAmount;

    /**
     * This field can be used to record any kind of info when performing the transaction.
     * Maximum length allowed for field customIdentifier is 150 characters, this field is optional.
     */
    private final String customIdentifier;
}
