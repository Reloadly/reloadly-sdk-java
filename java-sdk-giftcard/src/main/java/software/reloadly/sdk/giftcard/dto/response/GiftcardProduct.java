package software.reloadly.sdk.giftcard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import software.reloadly.sdk.giftcard.enums.GiftcardDenominationType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftcardProduct implements Serializable {

    private static final long serialVersionUID = 4061591738267790767L;

    @JsonProperty("productId")
    private Long id;
    @JsonProperty("productName")
    private String name;
    private boolean global;
    private Float senderFee;
    private Float discountPercentage;
    private GiftcardDenominationType denominationType;
    private String recipientCurrencyCode;
    private BigDecimal minRecipientDenomination;
    private BigDecimal maxRecipientDenomination;
    private String senderCurrencyCode;
    private BigDecimal minSenderDenomination;
    private BigDecimal maxSenderDenomination;
    private GiftcardBrand brand;
    private Set<String> logoUrls;
    private GiftcardCountry country;
    private TreeSet<BigDecimal> fixedRecipientDenominations;
    private TreeSet<BigDecimal> fixedSenderDenominations;
    private TreeMap<BigDecimal, BigDecimal> fixedRecipientToSenderDenominationsMap;
    private GiftcardRedeemInstructionSimplified redeemInstruction;
    private Map<BigDecimal, String> denominationsInfo;
}
