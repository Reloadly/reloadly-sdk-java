package software.reloadly.sdk.airtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.neovisionaries.i18n.CountryCode;
import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Phone implements Serializable {

    private static final long serialVersionUID = -4426743606174460330L;

    /**
     * Phone number
     */
    private final String number;

    /**
     * ISO 3166-1 alpha-2 Country code. See https://www.iso.org/obp/ui/#search
     */
    private final CountryCode countryCode;
}
