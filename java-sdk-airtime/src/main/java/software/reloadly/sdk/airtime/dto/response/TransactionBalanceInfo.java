package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class TransactionBalanceInfo implements Serializable {

    private static final long serialVersionUID = 2189899214743926170L;

    /**
     * Account balance prior to the transaction
     */
    @JsonProperty("oldBalance")
    private BigDecimal previousBalance;

    /**
     * Current account balance amount
     */
    @JsonProperty("newBalance")
    private BigDecimal currentBalance;

    /**
     * Account ISO-4217 3 letter currency code. See https://www.iso.org/iso-4217-currency-codes.html.
     * Example : USD
     */
    private String currencyCode;

    /**
     * Account currency name for the given currency code, example "United States Dollar"
     */
    private String currencyName;

    /**
     * Account balance last updated date
     */
    @JsonDeserialize(using = JackSonDateDeserializer.class)
    private Date updatedAt;
}
