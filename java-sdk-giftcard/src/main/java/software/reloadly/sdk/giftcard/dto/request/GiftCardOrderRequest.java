package software.reloadly.sdk.giftcard.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.neovisionaries.i18n.CountryCode;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@ToString
public class GiftCardOrderRequest implements Serializable {

    private static final long serialVersionUID = -5602589800681629739L;

    private final int quantity;
    private final Long productId;
    private final String senderName;
    private final BigDecimal unitPrice;
    private final String recipientEmail;
    @JsonProperty("recipientPhoneDetails")
    @SerializedName("recipientPhoneDetails")
    private final Phone recipientPhone;
    private final String customIdentifier;

    @Builder
    @SuppressWarnings("unused")
    public GiftCardOrderRequest(int quantity,
                                Long productId,
                                String senderName,
                                BigDecimal unitPrice,
                                String recipientEmail, Phone recipientPhone, String customIdentifier) {

        this.quantity = quantity;
        this.productId = productId;
        this.senderName = senderName;
        this.unitPrice = unitPrice;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.customIdentifier = customIdentifier;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class Phone {
        private final String phoneNumber;
        private final CountryCode countryCode;
    }
}