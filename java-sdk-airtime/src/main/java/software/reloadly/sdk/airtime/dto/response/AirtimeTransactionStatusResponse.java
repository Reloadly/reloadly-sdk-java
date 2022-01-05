package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import software.reloadly.sdk.airtime.enums.AirtimeTransactionStatus;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirtimeTransactionStatusResponse implements Serializable {

    private static final long serialVersionUID = -107316033333805883L;
    @JsonProperty("code")
    private String errorCode;
    @JsonProperty("message")
    private String errorMessage;
    private TopupTransaction transaction;
    private AirtimeTransactionStatus status;
}
