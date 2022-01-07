package software.reloadly.sdk.giftcard.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neovisionaries.i18n.CountryCode;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.dto.response.GiftcardProduct;
import software.reloadly.sdk.giftcard.filter.GiftcardProductFilter;

import java.util.List;

public class GiftcardProductOperations extends BaseGiftcardOperation {

    private static final String END_POINT = "products";
    private static final String PATH_SEGMENT_COUNTRIES = "countries";

    public GiftcardProductOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<GiftcardProduct>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(),
                new TypeReference<Page<GiftcardProduct>>() {
                }
        );
    }

    public Request<Page<GiftcardProduct>> list(GiftcardProductFilter filter) {
        return createGetRequest(buildFilters(filter, END_POINT).build().toString(),
                new TypeReference<Page<GiftcardProduct>>() {
                }
        );
    }

    public Request<List<GiftcardProduct>> listByCountryCode(CountryCode countryCode) {
        Asserter.assertNotNull(countryCode, "Country code");
        HttpUrl.Builder builder = getBuilder(PATH_SEGMENT_COUNTRIES)
                .addPathSegment(countryCode.getAlpha2()).addPathSegment(END_POINT);
        return createGetRequest(builder.toString(), new TypeReference<List<GiftcardProduct>>() {
        });
    }

    public Request<List<GiftcardProduct>> listByCountryCode(CountryCode countryCode, GiftcardProductFilter filter) {
        Asserter.assertNotNull(countryCode, "Country code");
        Asserter.assertNotBlank(countryCode.getAlpha2(), "Country code");
        HttpUrl.Builder builder = buildFilters(filter, PATH_SEGMENT_COUNTRIES)
                .addPathSegment(countryCode.getAlpha2()).addPathSegment(END_POINT);
        return createGetRequest(builder.toString(), new TypeReference<List<GiftcardProduct>>() {
        });
    }

    public Request<GiftcardProduct> getById(Long productId) {
        validateProductId(productId);
        HttpUrl.Builder builder = getBuilder(END_POINT).addPathSegment(productId.toString());
        return createGetRequest(builder.toString(), new TypeReference<GiftcardProduct>() {
        });
    }

    private void validateProductId(Long productId) {
        Asserter.assertNotNull(productId, "Product id");
        Asserter.assertGreaterThanZero(productId, "Product id");
    }
}
