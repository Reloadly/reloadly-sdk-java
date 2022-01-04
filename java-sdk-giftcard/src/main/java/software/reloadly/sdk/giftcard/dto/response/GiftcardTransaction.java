package software.reloadly.sdk.giftcard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import software.reloadly.sdk.core.internal.adapter.JackSonDateDeserializer;
import software.reloadly.sdk.giftcard.enums.GiftCardTransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftcardTransaction implements Serializable {

    private static final long serialVersionUID = 5205777808405666653L;
    @JsonProperty("transactionId")
    private Long id;
    private Float fee;
    private Float smsFee;
    private Float discount;
    private BigDecimal amount;
    private String currencyCode;
    private String recipientEmail;
    private String recipientPhone;
    private String customIdentifier;
    private GiftCardTransactionStatus status;
    private GiftcardTransactionProduct product;

    @JsonProperty("transactionCreatedTime")
    @JsonDeserialize(using = JackSonDateDeserializer.class)
    private Date date;

    @Getter
    @EqualsAndHashCode
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GiftcardTransactionProduct implements Serializable {

        private static final long serialVersionUID = 8796617953160384773L;
        @JsonProperty("productId")
        private Long id;
        @JsonProperty("productName")
        private String name;
        private Integer quantity;
        private String countryCode;
        private String currencyCode;
        private GiftcardBrand brand;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
