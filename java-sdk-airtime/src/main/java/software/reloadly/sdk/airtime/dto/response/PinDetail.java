package software.reloadly.sdk.airtime.dto.response;

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
public class PinDetail implements Serializable {

    private static final long serialVersionUID = 4763811416861692554L;

    /**
     * Serial number
     */
    private String serial;

    /**
     * Info part 1
     */
    @JsonProperty("info1")
    private String info;

    /**
     * Info part 2
     */
    @JsonProperty("info2")
    private String infoPart2;

    /**
     * Info part 3
     */
    @JsonProperty("info3")
    private String infoPart3;

    /**
     * PIN value
     */
    private Double value;

    /**
     * PIN code
     */
    private String code;

    /**
     * PIN IVR info
     */
    private String ivr;

    /**
     * PIN validity info
     */
    private String validity;
}
