package software.reloadly.sdk.giftcard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftcardDiscount implements Serializable {

    private static final long serialVersionUID = 5205777808405666653L;
    @JsonProperty("discountPercentage")
    private Float percentage;
    private GiftcardDiscountProduct product;

    @Getter
    @EqualsAndHashCode
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GiftcardDiscountProduct implements Serializable {

        private static final long serialVersionUID = -5004222688965304622L;
        @JsonProperty("productId")
        private Long id;
        @JsonProperty("productName")
        private String name;
        private boolean global;
        private String countryCode;
    }
}
