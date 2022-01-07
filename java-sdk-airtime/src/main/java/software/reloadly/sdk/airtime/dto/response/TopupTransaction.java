package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import software.reloadly.sdk.airtime.enums.AirtimeTransactionStatus;
import software.reloadly.sdk.core.internal.adapter.JackSonDateDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopupTransaction implements Serializable {

    private static final long serialVersionUID = 4524613307993713291L;

    /**
     * Unique ID of the transaction
     */
    @JsonProperty("transactionId")
    private Long id;

    /**
     * Unique ID of the transaction from the mobile operator if available
     */
    private String operatorTransactionId;

    /**
     * Unique ID of the transaction provided by the user during at transaction request if any
     */
    private String customIdentifier;

    /**
     * Unique id of the operator the transaction was sent to
     */
    private Long operatorId;

    /**
     * Topup recipient phone number (with country prefix)
     */
    private String recipientPhone;

    /**
     * Topup recipient email
     */
    private String recipientEmail;

    /**
     * Topup sender phone number that was provided at transaction request if any
     */
    private String senderPhone;

    /**
     * ISO 3166-1 alpha-2 country code of topup destination country. See https://www.iso.org/obp/ui/#search
     */
    private String countryCode;

    /**
     * Name of the mobile operator.
     */
    private String operatorName;

    /**
     * Topup amount that was requested by sender
     */
    private BigDecimal requestedAmount;

    /**
     * Discount amount that was applied to the request sender's account balance for this transaction
     */
    private BigDecimal discount;

    /**
     * ISO-4217 3 letter currency code of discount field. See https://www.iso.org/iso-4217-currency-codes.html
     */
    private String discountCurrencyCode;

    /**
     * ISO-4217 3 letter currency code of requestedAmount field. See https://www.iso.org/iso-4217-currency-codes.html
     */
    private String requestedAmountCurrencyCode;

    /**
     * Amount that was delivered in local currency
     */
    private BigDecimal deliveredAmount;

    /**
     * ISO-4217 3 letter currency code of deliveredAmount field. See https://www.iso.org/iso-4217-currency-codes.html
     */
    private String deliveredAmountCurrencyCode;

    /**
     * Time stamp recorded for this transaction
     */
    @JsonProperty("transactionDate")
    @JsonDeserialize(using = JackSonDateDeserializer.class)
    private Date date;

    /**
     * User (you) account balance info after this transaction
     */
    private TransactionBalanceInfo balanceInfo;

    /**
     * PIN detail info for PIN-based transactions
     */
    private PinDetail pinDetail;

    /**
     * The transaction status (PROCESSING, SUCCESSFUL, REFUNDED, FAILED)
     */
    private AirtimeTransactionStatus status;
}
