package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Serializable {

    private static final long serialVersionUID = -3646879608669281411L;

    /**
     * ISO 3166-1 alpha-2 Country code. See https://www.iso.org/obp/ui/#search
     */
    private String isoName;

    /**
     * Full country name
     */
    private String name;

    /**
     * Account ISO-4217 3 letter currency code for the given country.
     * See https://www.iso.org/iso-4217-currency-codes.html
     */
    private String currencyCode;

    /**
     * Full currency name
     */
    private String currencyName;

    /**
     * Symbol of currency
     */
    private String currencySymbol;

    /**
     * Url of country flag image
     */
    private String flag;

    /**
     * Calling codes of the country
     */
    private Set<String> callingCodes;
}
