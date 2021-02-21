package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.dto.response.Promotion;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.filter.QueryFilter;
import software.reloadly.sdk.core.internal.util.Asserter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.List;

public class PromotionOperations extends BaseAirtimeOperation {

    private static final String END_POINT = "promotions";
    private static final String PATH_SEGMENT_COUNTRIES = "countries";
    private static final String PATH_SEGMENT_OPERATORS = "operators";

    public PromotionOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<Promotion>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(), new TypeReference<Page<Promotion>>() {
        });
    }

    public Request<Page<Promotion>> list(QueryFilter filter) {
        return createGetRequest(buildFilters(filter, END_POINT).build().toString(),
                new TypeReference<Page<Promotion>>() {
                }
        );
    }

    public Request<Promotion> getById(Long promotionId) {
        Asserter.assertNotNull(promotionId, "Promotion id");
        Asserter.assertGreaterThanZero(promotionId, "Promotion id");
        return createGetRequest(getBuilder(END_POINT).addPathSegments(String.valueOf(promotionId)).build().toString(),
                new TypeReference<Promotion>() {
                }
        );
    }

    public Request<List<Promotion>> getByCountryCode(CountryCode countryCode) {
        Asserter.assertNotNull(countryCode, "Country code");
        return createGetRequest(getBuilder(END_POINT).addPathSegments(PATH_SEGMENT_COUNTRIES)
                        .addPathSegments(countryCode.getAlpha2()).build().toString(),
                new TypeReference<List<Promotion>>() {
                }
        );
    }

    public Request<List<Promotion>> getByOperatorId(Long operatorId) {
        Asserter.assertNotNull(operatorId, "Operator id");
        Asserter.assertGreaterThanZero(operatorId, "Operator id");
        return createGetRequest(getBuilder(END_POINT).addPathSegments(PATH_SEGMENT_OPERATORS)
                        .addPathSegments(String.valueOf(operatorId)).build().toString(),
                new TypeReference<List<Promotion>>() {
                }
        );
    }
}
