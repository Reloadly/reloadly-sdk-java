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
public class EmailTopupRequest extends TopupRequest implements Serializable {

    private static final long serialVersionUID = 5538891031402350675L;

    /**
     * Destination recipient email address to be credited
     */
    private final String recipientEmail;

    @Builder
    @SuppressWarnings("unused")
    public EmailTopupRequest(Double amount, Long operatorId, String recipientEmail,
                             Phone senderPhone, boolean useLocalAmount, String customIdentifier) {

        super(amount, operatorId, senderPhone, useLocalAmount, customIdentifier);
        this.recipientEmail = recipientEmail;
    }
}
