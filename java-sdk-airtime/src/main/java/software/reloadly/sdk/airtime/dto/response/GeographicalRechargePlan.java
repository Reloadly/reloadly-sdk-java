package software.reloadly.sdk.airtime.dto.response;

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
public class GeographicalRechargePlan implements Serializable {

    private static final long serialVersionUID = -5652377401301285195L;
    private String locationCode;
    private String locationName;
    private TreeSet<BigDecimal> fixedAmounts;
    private TreeSet<BigDecimal> localAmounts;
    private TreeMap<BigDecimal, String> fixedAmountsDescriptions;
    private TreeMap<BigDecimal, String> localFixedAmountsDescriptions;
}
