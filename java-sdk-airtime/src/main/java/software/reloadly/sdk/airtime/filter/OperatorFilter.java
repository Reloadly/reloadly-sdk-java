package software.reloadly.sdk.airtime.filter;


import software.reloadly.sdk.airtime.operation.OperatorOperations;
import software.reloadly.sdk.core.internal.filter.QueryFilter;

/**
 * Class used to filter the results received when calling the Operators endpoint.
 * Related to the {@link OperatorOperations}.
 */
public class OperatorFilter extends QueryFilter {

    private static final String INCLUDE_PIN = "includePin";
    private static final String INCLUDE_DATA = "includeData";
    private static final String INCLUDE_BUNDLES = "includeBundles";
    private static final String INCLUDE_SUGGESTED_AMOUNTS = "suggestedAmounts";
    private static final String INCLUDE_RANGE_DENOMINATION_TYPE = "includeRange";
    private static final String INCLUDE_FIXED_DENOMINATION_TYPE = "includeFixed";
    private static final String INCLUDE_SUGGESTED_AMOUNTS_MAP = "suggestedAmountsMap";

    public OperatorFilter() {
        parameters.put(INCLUDE_PIN, true);
        parameters.put(INCLUDE_DATA, true);
        parameters.put(INCLUDE_BUNDLES, true);
        parameters.put(INCLUDE_SUGGESTED_AMOUNTS, false);
        parameters.put(INCLUDE_SUGGESTED_AMOUNTS_MAP, false);
        parameters.put(INCLUDE_RANGE_DENOMINATION_TYPE, true);
        parameters.put(INCLUDE_FIXED_DENOMINATION_TYPE, true);
    }

    @Override
    public OperatorFilter withPage(int pageNumber, int pageSize) {
        super.withPage(pageNumber, pageSize);
        return this;
    }

    /**
     * Whether to include pin-based operators in response
     *
     * @param includePin - Whether to include pin-based operators in response
     * @return - OperatorFilter
     */
    public OperatorFilter includePin(boolean includePin) {
        parameters.put(INCLUDE_PIN, includePin);
        return this;
    }

    /**
     * Whether to include data operators in response
     *
     * @param includeData - Whether to include data operators in response
     * @return - OperatorFilter
     */
    public OperatorFilter includeData(boolean includeData) {
        parameters.put(INCLUDE_DATA, includeData);
        return this;
    }

    /**
     * Whether to include bundles operators in response
     *
     * @param includeBundles - Whether to include bundles in response
     * @return - OperatorFilter
     */
    public OperatorFilter includeBundles(boolean includeBundles) {
        parameters.put(INCLUDE_BUNDLES, includeBundles);
        return this;
    }

    /**
     * Whether to include suggestedAmount field in response
     *
     * @param includeSuggestedAmounts - Whether to include suggested amounts in response
     * @return - OperatorFilter
     */
    public OperatorFilter includeSuggestedAmounts(boolean includeSuggestedAmounts) {
        parameters.put(INCLUDE_SUGGESTED_AMOUNTS, includeSuggestedAmounts);
        return this;
    }

    /**
     * Whether to include suggestedAmountsMap field in response
     *
     * @param includeSuggestedAmountsMap - Whether to include suggested amounts map in response
     * @return - OperatorFilter
     */
    public OperatorFilter includeSuggestedAmountsMap(boolean includeSuggestedAmountsMap) {
        parameters.put(INCLUDE_SUGGESTED_AMOUNTS_MAP, includeSuggestedAmountsMap);
        return this;
    }

    /**
     * Whether to include operators where denomination type is RANGE in response
     *
     * @param includeRangeDenominationType - Whether to include range denomination type
     * @return - OperatorFilter
     */
    public OperatorFilter includeRangeDenominationType(boolean includeRangeDenominationType) {
        parameters.put(INCLUDE_RANGE_DENOMINATION_TYPE, includeRangeDenominationType);
        return this;
    }

    /**
     * Whether to include operators where denomination type is FIXED in response
     *
     * @param includeFixedDenominationType - Whether to include fixed denomination type
     * @return - OperatorFilter
     */
    public OperatorFilter includeFixedDenominationType(boolean includeFixedDenominationType) {
        parameters.put(INCLUDE_FIXED_DENOMINATION_TYPE, includeFixedDenominationType);
        return this;
    }
}
