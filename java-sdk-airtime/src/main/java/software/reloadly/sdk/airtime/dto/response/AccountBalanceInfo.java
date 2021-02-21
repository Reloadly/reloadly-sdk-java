package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import software.reloadly.sdk.core.internal.adapter.JackSonDateDeserializer;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBalanceInfo implements Serializable {

    private static final long serialVersionUID = 3027699901605787441L;

    /**
     * Current account balance amount
     */
    @JsonProperty("balance")
    private BigDecimal amount;

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
