package software.reloadly.sdk.airtime.internal.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FxRateRequest implements Serializable {

    private static final long serialVersionUID = 6823503731663525315L;
    private final Double amount;
}
