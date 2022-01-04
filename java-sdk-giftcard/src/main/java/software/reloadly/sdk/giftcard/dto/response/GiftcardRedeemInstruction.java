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
public class GiftcardRedeemInstruction implements Serializable {

    private static final long serialVersionUID = 5205777808405666653L;
    private Long brandId;
    private String concise;
    private String verbose;
    private String brandName;
}
