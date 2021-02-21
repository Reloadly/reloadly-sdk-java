package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.dto.response.Operator;
import software.reloadly.sdk.airtime.dto.response.OperatorFxRate;
import software.reloadly.sdk.airtime.filter.OperatorFilter;
import software.reloadly.sdk.airtime.internal.dto.request.FxRateRequest;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.lang.Nullable;

import java.util.List;

public class OperatorOperations extends BaseAirtimeOperation {

    private static final String END_POINT = "operators";
    private static final String PATH_SEGMENT_FX_RATE = "fx-rate";
    private static final String PATH_SEGMENT_COUNTRIES = "countries";
    private static final String PATH_SEGMENT_AUTO_DETECT = "auto-detect";
    private static final String PATH_SEGMENT_AUTO_DETECT_PHONE = "phone";

    public OperatorOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<Operator>> list(OperatorFilter filter) {
        return createGetRequest(buildFilters(filter, END_POINT).build().toString(),
                new TypeReference<Page<Operator>>() {
                }
        );
    }

    public Request<Page<Operator>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(), new TypeReference<Page<Operator>>() {
        });
    }

    public Request<Operator> getById(Long operatorId, OperatorFilter filter) {
        validateOperatorId(operatorId);
        HttpUrl.Builder builder = buildFilters(filter, END_POINT);
        builder.addPathSegment(operatorId.toString());
        return createGetRequest(builder.build().toString(), new TypeReference<Operator>() {
        });
    }

    public Request<Operator> getById(Long operatorId) {
        validateOperatorId(operatorId);
        HttpUrl.Builder builder = getBuilder(END_POINT);
        builder.addPathSegment(operatorId.toString());
        return createGetRequest(builder.build().toString(), new TypeReference<Operator>() {
        });
    }

    public Request<Operator> autoDetect(String phone, CountryCode countryCode, OperatorFilter filter) {
        validatePhoneAndCountryCode(phone, countryCode);
        return createGetRequest(buildAutoDetectRequest(phone, countryCode, buildFilters(filter, END_POINT)),
                new TypeReference<Operator>() {
                }
        );
    }

    public Request<Operator> autoDetect(String phone, CountryCode countryCode) {
        validatePhoneAndCountryCode(phone, countryCode);
        return createGetRequest(buildAutoDetectRequest(phone, countryCode, null), new TypeReference<Operator>() {
        });
    }

    public Request<List<Operator>> listByCountryCode(CountryCode countryCode, OperatorFilter filter) {
        Asserter.assertNotNull(countryCode, "Country code");
        return createGetRequest(buildListByCountryCodeRequestUrl(countryCode, buildFilters(filter, END_POINT)),
                new TypeReference<List<Operator>>() {
                }
        );
    }

    public Request<List<Operator>> listByCountryCode(CountryCode countryCode) {
        Asserter.assertNotNull(countryCode, "Country code");
        return createGetRequest(
                buildListByCountryCodeRequestUrl(countryCode, null), new TypeReference<List<Operator>>() {
                }
        );
    }

    public Request<OperatorFxRate> calculateFxRate(Long operatorId, Double amount) {
        validateOperatorId(operatorId);
        Asserter.assertNotNull(amount, "Amount");
        Asserter.assertGreaterThanZero(amount, "Amount");
        return createPostRequest(buildCalculateFxRateRequestUrl(operatorId), new FxRateRequest(amount),
                new TypeReference<OperatorFxRate>() {
                }
        );
    }

    private String buildListByCountryCodeRequestUrl(CountryCode countryCode, @Nullable HttpUrl.Builder builder) {

        if (builder == null) {
            builder = getBuilder(END_POINT);
        }

        return builder.addPathSegment(PATH_SEGMENT_COUNTRIES).addPathSegment(countryCode.getAlpha2()).build().toString();
    }

    private String buildCalculateFxRateRequestUrl(Long operatorId) {
        return getBuilder(END_POINT).addPathSegment(String.valueOf(operatorId))
                .addPathSegment(PATH_SEGMENT_FX_RATE).build().toString();
    }

    private String buildAutoDetectRequest(String phone, CountryCode countryCode, @Nullable HttpUrl.Builder builder) {

        phone = phone.trim();
        if (!phone.contains("+")) {
            phone = "+" + phone;
        }

        if (builder == null) {
            builder = getBuilder(END_POINT);
        }

        return builder.addPathSegment(PATH_SEGMENT_AUTO_DETECT)
                .addPathSegment(PATH_SEGMENT_AUTO_DETECT_PHONE)
                .addPathSegment(phone)
                .addPathSegment(PATH_SEGMENT_COUNTRIES)
                .addPathSegment(countryCode.getAlpha2()).build().toString();
    }

    private void validateOperatorId(Long operatorId) {
        Asserter.assertNotNull(operatorId, "Operator id");
        Asserter.assertGreaterThanZero(operatorId, "Operator id");
    }

    private void validatePhoneAndCountryCode(String phone, CountryCode countryCode) {
        Asserter.assertNotBlank(phone, "Phone");
        Asserter.assertNotNull(countryCode, "Country code");
    }
}
