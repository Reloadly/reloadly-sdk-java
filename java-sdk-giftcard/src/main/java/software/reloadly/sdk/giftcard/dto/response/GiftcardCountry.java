package software.reloadly.sdk.giftcard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftcardCountry implements Serializable {

    private static final long serialVersionUID = -5061206329979177191L;

    /**
     * ISO 3166-1 alpha-2 Country code. See https://www.iso.org/obp/ui/#search
     */
    private String isoName;

    /**
     * Full country name
     */
    private String name;

    /**
     * Url of country flag image
     */
    private String flagUrl;
}
