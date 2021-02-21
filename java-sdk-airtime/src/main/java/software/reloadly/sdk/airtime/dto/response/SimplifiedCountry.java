package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifiedCountry implements Serializable {

    private static final long serialVersionUID = -8942563311871329851L;

    /**
     * ISO 3166-1 alpha-2 Country code. See https://www.iso.org/obp/ui/#search
     */
    private String isoName;

    /**
     * Full country name
     */
    private String name;
}
