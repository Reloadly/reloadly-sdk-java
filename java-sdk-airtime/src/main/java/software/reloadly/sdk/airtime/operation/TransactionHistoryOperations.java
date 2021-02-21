package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import software.reloadly.sdk.airtime.dto.response.TopupTransaction;
import software.reloadly.sdk.airtime.filter.TransactionHistoryFilter;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionHistoryOperations extends BaseAirtimeOperation {

    private static final String TOPUP_TRANSACTION_HISTORY_END_POINT = "topups/reports/transactions";

    public TransactionHistoryOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<TopupTransaction>> list() {
        return createGetRequest(getBuilder(TOPUP_TRANSACTION_HISTORY_END_POINT).build().toString(),
                new TypeReference<Page<TopupTransaction>>() {
                }
        );
    }

    public Request<Page<TopupTransaction>> list(TransactionHistoryFilter filter) {
        validateStartAndEndDate(filter);
        return createGetRequest(buildFilters(filter, TOPUP_TRANSACTION_HISTORY_END_POINT).build().toString(),
                new TypeReference<Page<TopupTransaction>>() {
                }
        );
    }

    public Request<TopupTransaction> getById(Long transactionId) {
        Asserter.assertNotNull(transactionId, "Transaction id");
        Asserter.assertGreaterThanZero(transactionId, "Transaction id");
        return createGetRequest(getBuilder(TOPUP_TRANSACTION_HISTORY_END_POINT)
                        .addPathSegments(String.valueOf(transactionId)).build().toString(),
                new TypeReference<TopupTransaction>() {
                }
        );
    }

    private void validateStartAndEndDate(TransactionHistoryFilter filter) {

        LocalDateTime endDate = null;
        LocalDateTime startDate = null;
        final String END_DATE = "endDate";
        final String START_DATE = "startDate";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endDateStr = (String) filter.getParameters().getOrDefault(END_DATE, null);
        String startDateStr = (String) filter.getParameters().getOrDefault(START_DATE, null);

        if (StringUtils.isNotBlank(endDateStr)) {
            endDate = LocalDateTime.parse(endDateStr, dateFormatter);
        }

        if (StringUtils.isNotBlank(startDateStr)) {
            startDate = LocalDateTime.parse(startDateStr, dateFormatter);
        }

        if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
            String msg = "If start date is set, end date must be set as well and vice-versa";
            throw new IllegalArgumentException(msg);
        } else if (startDate != null && startDate.isAfter(endDate)) {
            String msg = "The start date must NOT be greater than the end date";
            throw new IllegalArgumentException(msg);
        } else if (endDate != null && endDate.isBefore(startDate)) {
            String msg = "The end date must be greater than the start date";
            throw new IllegalArgumentException(msg);
        }
    }
}
