package software.reloadly.sdk.giftcard.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.dto.response.GiftcardDiscount;

public class GiftcardDiscountsOperations extends BaseGiftcardOperation {

    private static final String END_POINT = "discounts";
    private static final String PATH_SEGMENT_PRODUCTS = "products";

    public GiftcardDiscountsOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<Page<GiftcardDiscount>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(),
                new TypeReference<Page<GiftcardDiscount>>() {
                }
        );
    }

    public Request<GiftcardDiscount> getByProductId(Long productId) {
        Asserter.assertNotNull(productId, "Product id");
        Asserter.assertGreaterThanZero(productId, "Product id");
        String endPoint = PATH_SEGMENT_PRODUCTS + "/" + productId + "/" + END_POINT;
        return createGetRequest(getBuilder(endPoint).build().toString(),
                new TypeReference<GiftcardDiscount>() {
                }
        );
    }
}
