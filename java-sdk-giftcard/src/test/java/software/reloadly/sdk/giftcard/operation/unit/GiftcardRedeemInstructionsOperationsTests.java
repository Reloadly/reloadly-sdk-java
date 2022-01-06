package software.reloadly.sdk.giftcard.operation.unit;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.RecordedRequestMatcher;
import software.reloadly.sdk.giftcard.util.GiftcardAPIMockServer;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardRedeemInstruction;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static software.reloadly.sdk.core.internal.enums.Version.GIFTCARD_V1;

public class GiftcardRedeemInstructionsOperationsTests {

    private static final String PATH = "src/test/resources/redeem_instructions";
    private static final String ALL_INSTRUCTIONS = PATH + "/redeem_instructions_list_response.json";
    private static final String INSTRUCTIONS_BY_BRAND = PATH + "/redeem_instructions_by_brand_response.json";

    private GiftcardAPIMockServer server;

    @BeforeEach
    public void setUp() throws Exception {
        server = new GiftcardAPIMockServer();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void testListGiftcardRedeemInstructions() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<List<GiftcardRedeemInstruction>> request = giftcardAPI.redeemInstructions().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(ALL_INSTRUCTIONS, 200);
        List<GiftcardRedeemInstruction> redeemInstructions = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/redeem-instructions"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        redeemInstructions.forEach(this::assertIsValidGiftcardRedeemInstruction);
    }

    @Test
    public void testListGiftcardRedeemInstructionByBrandId() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        long brandId = 25L;
        Request<GiftcardRedeemInstruction> request = giftcardAPI.redeemInstructions().getByBrandId(brandId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(INSTRUCTIONS_BY_BRAND, 200);
        GiftcardRedeemInstruction redeemInstruction = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/brands/" + brandId + "/redeem-instructions";
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidGiftcardRedeemInstruction(redeemInstruction);
    }

    @Test
    public void testListGiftcardRedeemInstructionByBrandIdShouldThrowExceptionWhenProductIdIsNull() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.redeemInstructions().getByBrandId(null));
        Assertions.assertEquals("'Brand id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testListGiftcardRedeemInstructionByBrandIdShouldThrowExceptionWhenProductIdIsLessThanZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.redeemInstructions().getByBrandId(-25L));
        Assertions.assertEquals("'Brand id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testListGiftcardRedeemInstructionByBrandIdShouldThrowExceptionWhenProductIdIsEqualToZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.redeemInstructions().getByBrandId(0L));
        Assertions.assertEquals("'Brand id' must be greater than zero!", exception.getMessage());
    }

    private void assertIsValidGiftcardRedeemInstruction(GiftcardRedeemInstruction redeemInstruction) {

        int expectedFieldsCount = 4;
        List<String> redeemInstructionFields = Arrays.asList("brandId", "brandName", "concise", "verbose");

        List<String> fields = Arrays.stream(redeemInstruction.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).collect(Collectors.toList());

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
