package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.TreeSet;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeographicalRechargePlan implements Serializable {

    private static final long serialVersionUID = -5652377401301285195L;
    private String locationCode;
    private String locationName;
    private TreeSet<BigDecimal> fixedAmounts;
    private TreeSet<BigDecimal> localAmounts;
    private TreeMap<BigDecimal, String> fixedAmountsPlanNames;
    private TreeMap<BigDecimal, String> fixedAmountsDescriptions;
    private TreeMap<BigDecimal, String> localFixedAmountsPlanNames;
    private TreeMap<BigDecimal, String> localFixedAmountsDescriptions;
}
