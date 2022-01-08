package software.reloadly.sdk.giftcard.operation.integration;

import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardRedeemInstruction;
import software.reloadly.sdk.giftcard.interfaces.IntegrationTest;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GiftcardRedeemInstructionsOperationsTests extends BaseIntegrationTest {

    @IntegrationTest
    public void testListGiftcardRedeemInstructions() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<List<GiftcardRedeemInstruction>> request = giftcardAPI.redeemInstructions().list();
        assertThat(request, is(notNullValue()));
        List<GiftcardRedeemInstruction> redeemInstructions = request.execute();
        redeemInstructions.forEach(this::assertIsValidGiftcardRedeemInstruction);
    }

    @IntegrationTest
    public void testListGiftcardRedeemInstructionByBrandId() throws Exception {

        long brandId = 25L;
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<GiftcardRedeemInstruction> request = giftcardAPI.redeemInstructions().getByBrandId(brandId);
        assertThat(request, is(notNullValue()));
        GiftcardRedeemInstruction redeemInstruction = request.execute();
        assertIsValidGiftcardRedeemInstruction(redeemInstruction);
    }

    private void assertIsValidGiftcardRedeemInstruction(GiftcardRedeemInstruction redeemInstruction) {

        int expectedFieldsCount = 4;
        List<String> redeemInstructionFields = Arrays.asList("brandId", "brandName", "concise", "verbose");

        List<String> fields = Arrays.stream(redeemInstruction.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).collect(Collectors.toList());

        int actualFieldsCount = fields.size();
        String errorMsg = "Failed asserting that GiftcardRedeemInstruction::class contains " + expectedFieldsCount;
        errorMsg += " fields. It actually contains " + actualFieldsCount + " fields";
        assertThat(errorMsg, expectedFieldsCount == actualFieldsCount);
        assertThat(redeemInstruction, is(notNullValue()));
        redeemInstructionFields.forEach(field -> assertThat(redeemInstruction, hasProperty(field)));
        assertThat(redeemInstruction.getBrandId(), is(notNullValue()));
        assertThat(redeemInstruction.getConcise(), is(not(emptyOrNullString())));
        assertThat(redeemInstruction.getVerbose(), is(not(emptyOrNullString())));
        assertThat(redeemInstruction.getBrandName(), is(not(emptyOrNullString())));
        assertThat(redeemInstruction.getBrandId(), is(greaterThanOrEqualTo(0L)));
    }
}
