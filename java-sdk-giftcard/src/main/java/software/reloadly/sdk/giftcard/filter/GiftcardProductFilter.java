package software.reloadly.sdk.giftcard.filter;

import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;
import software.reloadly.sdk.core.internal.filter.QueryFilter;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.operation.GiftcardProductOperations;

/**
 * Class used to filter the results received when calling the giftcard products endpoint.
 * Related to the {@link GiftcardProductOperations}.
 */
@Getter
public class GiftcardProductFilter extends QueryFilter {

    private static final String PRODUCT_NAME = "productName";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String INCLUDE_RANGE = "includeRange";
    private static final String INCLUDE_FIXED = "includeFixed";
    private static final String INCLUDE_SIMPLIFY = "simplified";

    public GiftcardProductFilter() {
        parameters.put(INCLUDE_RANGE, true);
        parameters.put(INCLUDE_FIXED, true);
        parameters.put(INCLUDE_SIMPLIFY, false);
    }

    @Override
    public GiftcardProductFilter withPage(int pageNumber, int pageSize) {
        super.withPage(pageNumber, pageSize);
        return this;
    }

    public GiftcardProductFilter productName(String productName) {
        Asserter.assertNotBlank(productName, "Product name");
        parameters.put(PRODUCT_NAME, productName);
        return this;
    }

    public GiftcardProductFilter countryCode(CountryCode countryCode) {
        Asserter.assertNotNull(countryCode, "Country code");
        parameters.put(COUNTRY_CODE, countryCode.getAlpha2());
        return this;
    }

    public GiftcardProductFilter simplified(boolean simplified) {
        parameters.put(INCLUDE_SIMPLIFY, simplified);
        return this;
    }

    public GiftcardProductFilter includeRange(boolean includeRange) {
        parameters.put(INCLUDE_RANGE, includeRange);
        return this;
    }

    public GiftcardProductFilter includeFixed(boolean includeFixed) {
        parameters.put(INCLUDE_FIXED, includeFixed);
        return this;
    }
}
