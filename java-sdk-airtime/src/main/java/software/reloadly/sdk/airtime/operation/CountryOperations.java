package software.reloadly.sdk.airtime.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.dto.response.Country;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.List;

public class CountryOperations extends BaseAirtimeOperation {

    private static final String END_POINT = "countries";

    public CountryOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<List<Country>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(), new TypeReference<List<Country>>() {
        });
    }

    public Request<Country> getByCode(CountryCode countryCode) {
        Asserter.assertNotNull(countryCode, "Country code");
        return createGetRequest(getBuilder(END_POINT).addPathSegments(countryCode.getAlpha2()).build().toString(),
                new TypeReference<Country>() {
                }
        );
    }
}
