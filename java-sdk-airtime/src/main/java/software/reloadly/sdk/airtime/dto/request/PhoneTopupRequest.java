package software.reloadly.sdk.airtime.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import software.reloadly.sdk.airtime.dto.Phone;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneTopupRequest extends TopupRequest implements Serializable {

    private static final long serialVersionUID = 588674246514588914L;

    /**
     * Destination recipient phone number (with country prefix) to be credited, example +50936377111
     */
    private final Phone recipientPhone;

    @Builder
    public PhoneTopupRequest(Double amount, Long operatorId,
                             Phone recipientPhone, boolean useLocalAmount, Phone senderPhone, String customIdentifier) {

        super(amount, operatorId, senderPhone, useLocalAmount, customIdentifier);
        this.recipientPhone = recipientPhone;
    }
}
