package software.reloadly.sdk.giftcard.operation.integration;

import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardDiscount;
import software.reloadly.sdk.giftcard.interfaces.IntegrationTest;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GiftcardDiscountsOperationsTests extends BaseIntegrationTest {

    @IntegrationTest
    public void testListGiftcardDiscounts() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .environment(Environment.LIVE)
                .accessToken(accessToken).build();

        Request<Page<GiftcardDiscount>> request = giftcardAPI.discounts().list();
        assertThat(request, is(notNullValue()));
        Page<GiftcardDiscount> redeemInstructions = request.execute();
        redeemInstructions.forEach(this::assertIsValidGiftcardDiscount);
    }

    @IntegrationTest
    public void testListGiftcardDiscountsByProductId() throws Exception {

        long productId = 100L;
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .environment(Environment.LIVE)
                .accessToken(accessToken).build();

        Request<GiftcardDiscount> request = giftcardAPI.discounts().getByProductId(productId);
        assertThat(request, is(notNullValue()));
        GiftcardDiscount discount = request.execute();
        assertIsValidGiftcardDiscount(discount);
    }

    private void assertIsValidGiftcardDiscount(GiftcardDiscount discount) {

        int expectedDiscountFieldsCount = 2;
        int expectedProductSimplifiedFieldsCount = 4;
        List<String> discountFields = Arrays.asList("percentage", "product");
        List<String> productSimplifiedFields = Arrays.asList("id", "name", "global", "countryCode");

        int actualDiscountFieldsCount = (int) Arrays.stream(discount.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).count();

        int actualProductSimplifiedFieldsCount = (int) Arrays.stream(discount.getProduct().getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData") &&
                        !f.getName().equalsIgnoreCase("__$lineHits$__"))
                ).map(Field::getName).count();

        String errorMsg = "Failed asserting that GiftcardDiscount::class contains " + expectedDiscountFieldsCount;
        errorMsg += " fields. It actually contains " + actualDiscountFieldsCount;
        String errorMsg2 = "Failed asserting that GiftcardProductSimplified::class contains ";
        errorMsg2 += expectedProductSimplifiedFieldsCount + " fields. It actually contains ";
        errorMsg2 += actualProductSimplifiedFieldsCount + " fields.";
        assertThat(errorMsg, expectedDiscountFieldsCount == actualDiscountFieldsCount);
        assertThat(errorMsg2, expectedProductSimplifiedFieldsCount == actualProductSimplifiedFieldsCount);
        assertThat(discount, is(notNullValue()));
        assertThat(discount.getProduct(), is(notNullValue()));

        GiftcardDiscount.GiftcardDiscountProduct product = discount.getProduct();
        discountFields.forEach(field -> assertThat(discount, hasProperty(field)));
        productSimplifiedFields.forEach(field -> assertThat(product, hasProperty(field)));

        assertThat(discount.getPercentage(), is(notNullValue()));
        assertThat(discount.getPercentage(), is(greaterThanOrEqualTo(0F)));

        assertThat(product.getId(), is(notNullValue()));
        assertThat(product.getId(), is(greaterThan(0L)));
        assertThat(product.getName(), is(not(emptyOrNullString())));
        assertThat(product.getCountryCode(), is(not(emptyOrNullString())));
    }
}
