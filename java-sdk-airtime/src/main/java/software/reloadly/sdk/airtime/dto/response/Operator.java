package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import software.reloadly.sdk.airtime.operation.OperatorOperations;
import software.reloadly.sdk.airtime.enums.DenominationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Class that represents a Reloadly airtime operator object. Related to the {@link OperatorOperations}
 */
@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Operator implements Serializable {

    private static final long serialVersionUID = -1933108105383250905L;

    /**
     * Unique id assign to each operator.
     */
    private Long id;

    /**
     * Name of the mobile operator.
     */
    private String name;

    /**
     * Whether the mobile operator is a prepaid data bundle. Prepaid bundles are a mixture of calls, data,
     * SMS and social media access which the users can purchase other than airtime credits
     */
    protected boolean bundle;

    /**
     * Whether the operator is a prepaid data only
     */
    protected boolean data;

    /**
     * Whether the operator is pin based
     */
    @JsonProperty("pin")
    protected boolean pinBased;

    /**
     * Whether the operator supports local amounts
     */
    protected boolean supportsLocalAmounts;

    /**
     * Whether the operator supports geographical recharge plans
     */
    private boolean supportsGeographicalRechargePlans;

    /**
     * Operator amount denomination type
     */
    private DenominationType denominationType;

    /**
     * ISO-3 currency code of user account
     */
    private String senderCurrencyCode;

    /**
     * User account currency symbol
     */
    private String senderCurrencySymbol;

    /**
     * ISO-3 currency code of operator destination country
     */
    private String destinationCurrencyCode;

    /**
     * Destination currency symbol
     */
    private String destinationCurrencySymbol;

    /**
     * International discount assigned for this operator
     */
    private Float internationalDiscount;

    /**
     * Local discount assigned for this operator
     */
    private Float localDiscount;

    /**
     * Most popular international amount for this operator
     */
    @JsonProperty("mostPopularAmount")
    private BigDecimal mostPopularInternationalAmount;

    /**
     * Most popular local amount for this operator
     */
    private BigDecimal mostPopularLocalAmount;

    /**
     * Operator's country
     */
    private SimplifiedCountry country;

    /**
     * Current fx rate for this operator
     */
    @JsonProperty("fx")
    private FxRate fxRate;

    /**
     * Suggested whole amounts when denomination type is 'FIXED'
     */
    private TreeSet<Integer> suggestedAmounts;

    /**
     * Suggested amounts map containing (amount in sender currency, amount in recipient currency)
     * when denomination type is 'FIXED'
     */
    private TreeMap<BigDecimal, BigDecimal> suggestedAmountsMap;

    /**
     * Minimum amount when denomination type is 'RANGE' will be empty/null for 'FIXED' denomination type
     */
    private BigDecimal minAmount;

    /**
     * Maximum amount when denomination type is 'RANGE', will be empty/null for 'FIXED' denomination type
     */
    private BigDecimal maxAmount;

    /**
     * Minimum local amount when denomination type is 'RANGE', will be empty/null for 'FIXED' denomination type
     */
    private BigDecimal localMinAmount;

    /**
     * Maximum local amount when denomination type is 'RANGE', will be empty/null for 'FIXED' denomination
     */
    private BigDecimal localMaxAmount;

    /**
     * Available operator amounts when denomination type is 'FIXED', will be empty/null for 'RANGE' denomination type
     */
    private TreeSet<BigDecimal> fixedAmounts;

    /**
     * Available operator local amounts when denomination type is 'FIXED', will be empty/null for 'RANGE' denomination type
     */
    private TreeSet<BigDecimal> localFixedAmounts;

    /**
     * International fixed amounts descriptions
     */
    private TreeMap<BigDecimal, String> fixedAmountsDescriptions;

    /**
     * Local fixed amounts descriptions
     */
    private TreeMap<BigDecimal, String> localFixedAmountsDescriptions;

    /**
     * Geographical recharge plans where supported
     */
    private Set<GeographicalRechargePlan> geographicalRechargePlans;

    /**
     * Logo url of the mobile operator
     */
    private Set<String> logoUrls;

    /**
     * Available promotions for this operator if any
     */
    private Set<Promotion> promotions;
}
