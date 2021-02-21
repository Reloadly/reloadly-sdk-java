package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import software.reloadly.sdk.airtime.dto.response.Discount;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.filter.QueryFilter;
import software.reloadly.sdk.core.internal.util.Asserter;

public class DiscountOperations extends BaseAirtimeOperation {

    private static final String END_POINT = "operators";
    private static final String PATH_SEGMENT_DISCOUNT = "commissions";


    public DiscountOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<Discount>> list() {
        return createGetRequest(getBuilder(END_POINT).addPathSegments(PATH_SEGMENT_DISCOUNT).build().toString(),
                new TypeReference<Page<Discount>>() {
                }
        );
    }

    public Request<Page<Discount>> list(QueryFilter filter) {
        return createGetRequest(buildFilters(filter, END_POINT)
                        .addPathSegments(PATH_SEGMENT_DISCOUNT).build().toString(),
                new TypeReference<Page<Discount>>() {
                }
        );
    }

    public Request<Discount> getByOperatorId(Long operatorId) {
        Asserter.assertNotNull(operatorId, "Operator id");
        Asserter.assertGreaterThanZero(operatorId, "Operator id");
        return createGetRequest(getBuilder(END_POINT).addPathSegments(String.valueOf(operatorId))
                        .addPathSegments(PATH_SEGMENT_DISCOUNT).build().toString(),
                new TypeReference<Discount>() {
                }
        );
    }
}
