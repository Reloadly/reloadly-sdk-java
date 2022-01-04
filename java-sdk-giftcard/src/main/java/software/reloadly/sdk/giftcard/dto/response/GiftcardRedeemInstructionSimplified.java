package software.reloadly.sdk.giftcard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftcardRedeemInstructionSimplified implements Serializable {


    private static final long serialVersionUID = 962678749036587376L;
    private String concise;
    private String verbose;
}
