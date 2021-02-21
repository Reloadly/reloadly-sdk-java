package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperatorFxRate implements Serializable {

    private static final long serialVersionUID = 4787380284997559055L;
    @JsonProperty("id")
    private Long operatorId;
    @JsonProperty("name")
    private String operatorName;
    private float fxRate;
    private String currencyCode;
}
