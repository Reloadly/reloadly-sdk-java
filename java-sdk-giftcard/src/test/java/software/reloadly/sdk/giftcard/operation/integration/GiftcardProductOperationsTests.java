package software.reloadly.sdk.giftcard.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardProduct;
import software.reloadly.sdk.giftcard.filter.GiftcardProductFilter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static software.reloadly.sdk.giftcard.enums.GiftcardDenominationType.*;

public class GiftcardProductOperationsTests extends BaseIntegrationTest {

    @Test
    public void testListGiftcardProductsWithNoFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<Page<GiftcardProduct>> request = giftcardAPI.products().list();
        assertThat(request, is(notNullValue()));
        Page<GiftcardProduct> productPage = request.execute();
        productPage.getContent().forEach(this::assertIsValidGiftcardProduct);
    }

    @Test
    public void testListGiftcardProductsWithFilters() throws Exception {

        int page = 1;
        int pageSize = 200;
        GiftcardProductFilter filter = new GiftcardProductFilter().withPage(page, pageSize)
                .countryCode(CountryCode.US)
                .productName("xbox")
                .includeRange(true)
                .includeFixed(true);

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<Page<GiftcardProduct>> request = giftcardAPI.products().list(filter);
        assertThat(request, is(notNullValue()));
        Page<GiftcardProduct> giftcardProductsPage = request.execute();
        giftcardProductsPage.getContent().forEach(this::assertIsValidGiftcardProduct);
    }

    @Test
    public void testListGiftcardProductsByCountryCodeWithNoFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<List<GiftcardProduct>> request = giftcardAPI.products().listByCountryCode(CountryCode.US);
        assertThat(request, is(notNullValue()));
        List<GiftcardProduct> products = request.execute();
        products.forEach(this::assertIsValidGiftcardProduct);
    }

    @Test
    public void testListGiftcardProductsByCountryCodeWithFilters() throws Exception {

        int page = 1;
        int pageSize = 200;
        GiftcardProductFilter filter = new GiftcardProductFilter().withPage(page, pageSize)
                .countryCode(CountryCode.US)
                .productName("xbox")
                .includeRange(true)
                .includeFixed(true);

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<List<GiftcardProduct>> request = giftcardAPI.products().listByCountryCode(CountryCode.US, filter);
        assertThat(request, is(notNullValue()));
        List<GiftcardProduct> giftcardProductsPage = request.execute();
        giftcardProductsPage.forEach(this::assertIsValidGiftcardProduct);
    }

    @Test
    public void testListGiftcardProductById() throws Exception {

        Long productId = 10L;
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<GiftcardProduct> request = giftcardAPI.products().getById(productId);
        assertThat(request, is(notNullValue()));
        GiftcardProduct product = request.execute();
        assertIsValidGiftcardProduct(product);
    }

    private void assertIsValidGiftcardProduct(GiftcardProduct product) {

        int expectedFieldsCount = 19;
        List<String> productFields = Arrays.asList("id", "name", "global", "senderFee", "discountPercentage",
                "denominationType", "recipientCurrencyCode", "minRecipientDenomination", "maxRecipientDenomination",
                "senderCurrencyCode", "minSenderDenomination", "maxSenderDenomination", "brand", "logoUrls", "country",
                "fixedRecipientDenominations", "fixedSenderDenominations", "fixedRecipientToSenderDenominationsMap",
                "redeemInstruction");

        int actualFieldsCount = (int) Arrays.stream(product.getClass().getDeclaredFields())
                .filter(f -> (!f.getName().equalsIgnoreCase("serialVersionUID") &&
                        !f.getName().equalsIgnoreCase("$jacocoData")))
                .map(Field::getName).count();

        String errorMsg = "Failed asserting that GiftcardRedeemInstruction::class contains " + expectedFieldsCount;
        errorMsg += " fields. It actually contains " + actualFieldsCount + " fields";
        assertThat(errorMsg, expectedFieldsCount == actualFieldsCount);
        productFields.forEach(field -> assertThat(product, hasProperty(field)));

        assertThat(product.getId(), is(notNullValue()));
        assertThat(product.getName(), is(not(emptyOrNullString())));
        assertThat(product.isGlobal(), is(notNullValue()));
        assertThat(product.getSenderFee(), is(notNullValue()));
        assertThat(product.getSenderFee(), is(greaterThanOrEqualTo(0F)));
        assertThat(product.getDiscountPercentage(), is(notNullValue()));
        assertThat(product.getDiscountPercentage(), is(greaterThanOrEqualTo(0F)));
        assertThat(product.getDenominationType(), is(notNullValue()));
        assertThat(product.getDenominationType(), anyOf(equalTo(RANGE), equalTo(FIXED), equalTo(FIXED_AND_RANGE)));
        assertThat(product.getRecipientCurrencyCode(), is(notNullValue()));
        assertThat("Failed asserting truthiness for recipient currency code value length",
                product.getRecipientCurrencyCode().length() == 3);

        assertThat(product.getSenderCurrencyCode(), is(notNullValue()));
        assertThat("Failed asserting truthiness for sender currency code value length",
                product.getSenderCurrencyCode().length() == 3);


        assertThat(product.getBrand(), is(notNullValue()));
        assertThat(product.getBrand().getId(), is(notNullValue()));
        assertThat(product.getBrand().getId(), is(greaterThan(0L)));
        assertThat(product.getBrand().getName(), is(not(emptyOrNullString())));

        assertThat(product.getLogoUrls(), is(notNullValue()));
        assertThat("Failed asserting truthiness for logo url collection size",
                product.getLogoUrls().size() > 0);
        assertThat(product.getLogoUrls().iterator().next(), is(not(emptyOrNullString())));

        assertThat(product.getCountry(), is(notNullValue()));
        assertThat(product.getCountry().getIsoName(), is(not(emptyOrNullString())));
        assertThat("Failed asserting truthiness for country iso name length",
                product.getCountry().getIsoName().length() == 2);
        assertThat(product.getCountry().getName(), is(not(emptyOrNullString())));
        assertThat(product.getCountry().getFlagUrl(), is(not(emptyOrNullString())));

        assertThat(product.getRedeemInstruction(), is(notNullValue()));
        assertThat(product.getRedeemInstruction().getConcise(), is(not(emptyOrNullString())));
        assertThat(product.getRedeemInstruction().getVerbose(), is(not(emptyOrNullString())));
    }
}
