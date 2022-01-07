package software.reloadly.sdk.airtime.filter;


import com.neovisionaries.i18n.CountryCode;
import lombok.NoArgsConstructor;
import software.reloadly.sdk.airtime.operation.TopupOperations;
import software.reloadly.sdk.core.internal.filter.QueryFilter;
import software.reloadly.sdk.core.internal.util.Asserter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used to filter the results received when calling topup transaction history endpoint.
 * Related to the {@link TopupOperations}.
 */
@NoArgsConstructor
@SuppressWarnings("unused")
public class TransactionHistoryFilter extends QueryFilter {

    private static final String END_DATE = "endDate";
    private static final String START_DATE = "startDate";
    private static final String OPERATOR_ID = "operatorId";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String OPERATOR_NAME = "operatorName";
    private static final String CUSTOM_IDENTIFIER = "customIdentifier";

    @Override
    public TransactionHistoryFilter withPage(int pageNumber, int pageSize) {
        super.withPage(pageNumber, pageSize);
        return this;
    }

    /**
     * @param operatorId - Operator id to filter by
     * @return - TransactionHistoryFilter
     */
    public TransactionHistoryFilter operatorId(Long operatorId) {
        Asserter.assertNotNull(operatorId, "Operator id");
        Asserter.assertGreaterThanZero(operatorId, "Operator id");
        parameters.put(OPERATOR_ID, operatorId);
        return this;
    }

    /**
     * @param countryCode - Country code to filter by
     * @return - TransactionHistoryFilter
     */
    public TransactionHistoryFilter countryCode(CountryCode countryCode) {
        Asserter.assertNotNull(countryCode, "Country code");
        parameters.put(COUNTRY_CODE, countryCode.getAlpha2());
        return this;
    }

    /**
     * @param operatorName - Operator name to filter by
     * @return - TransactionHistoryFilter
     */
    public TransactionHistoryFilter operatorName(String operatorName) {
        Asserter.assertNotBlank(operatorName, "Operator name");
        parameters.put(OPERATOR_NAME, operatorName);
        return this;
    }

    /**
     * @param customIdentifier - Custom identifier to filter by
     * @return - TransactionHistoryFilter
     */
    public TransactionHistoryFilter customIdentifier(String customIdentifier) {
        Asserter.assertNotBlank(customIdentifier, "Custom identifier");
        parameters.put(CUSTOM_IDENTIFIER, customIdentifier);
        return this;
    }

    /**
     * @param startDate - Date range start date to filter by
     * @return - TransactionHistoryFilter
     */
    public TransactionHistoryFilter startDate(LocalDateTime startDate) {
        Asserter.assertNotNull(startDate, "Start date");
        parameters.put(START_DATE, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this;
    }

    /**
     * @param endDate - Date range end date to filter by
     * @return - TransactionHistoryFilter
     */
    public TransactionHistoryFilter endDate(LocalDateTime endDate) {
        Asserter.assertNotNull(endDate, "End date");
        parameters.put(END_DATE, endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this;
    }
}
