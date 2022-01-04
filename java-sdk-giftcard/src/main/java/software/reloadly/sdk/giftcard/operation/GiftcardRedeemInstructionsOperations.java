package software.reloadly.sdk.giftcard.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.dto.response.GiftcardRedeemInstruction;

import java.util.List;

public class GiftcardRedeemInstructionsOperations extends BaseGiftcardOperation {

    private static final String PATH_SEGMENT_BRANDS = "brands";
    private static final String END_POINT = "redeem-instructions";

    public GiftcardRedeemInstructionsOperations(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        super(baseUrl, apiToken, client);
    }

    public Request<List<GiftcardRedeemInstruction>> list() {
        return createGetRequest(getBuilder(END_POINT).build().toString(),
                new TypeReference<List<GiftcardRedeemInstruction>>() {
                }
        );
    }

    public Request<GiftcardRedeemInstruction> getByBrandId(Long brandId) {
        Asserter.assertNotNull(brandId, "Brand id");
        Asserter.assertGreaterThanZero(brandId, "Brand id");
        String endPoint = PATH_SEGMENT_BRANDS + "/" + brandId + "/" + END_POINT;
        return createGetRequest(getBuilder(endPoint).build().toString(),
                new TypeReference<GiftcardRedeemInstruction>() {
                }
        );
    }
}
