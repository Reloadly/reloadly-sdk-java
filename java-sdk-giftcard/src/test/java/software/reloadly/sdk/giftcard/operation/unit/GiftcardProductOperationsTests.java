package software.reloadly.sdk.giftcard.operation.unit;

import com.neovisionaries.i18n.CountryCode;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.util.RecordedRequestMatcher;
import software.reloadly.sdk.giftcard.util.GiftcardAPIMockServer;
import software.reloadly.sdk.giftcard.client.GiftcardAPI;
import software.reloadly.sdk.giftcard.dto.response.GiftcardProduct;
import software.reloadly.sdk.giftcard.filter.GiftcardProductFilter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.reloadly.sdk.core.internal.constant.HttpHeader.ACCEPT;
import static software.reloadly.sdk.core.internal.enums.Version.GIFTCARD_V1;
import static software.reloadly.sdk.giftcard.enums.GiftcardDenominationType.*;

public class GiftcardProductOperationsTests {

    private static final String PAGE = "page";
    private static final String PAGE_SIZE = "size";
    private static final String PRODUCT_NAME = "productName";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String INCLUDE_RANGE = "includeRange";
    private static final String INCLUDE_FIXED = "includeFixed";
    private static final String INCLUDE_SIMPLIFY = "simplified";

    private static final List<String> validFilters = Arrays.asList(PAGE.toLowerCase(), PAGE_SIZE.toLowerCase(),
            PRODUCT_NAME.toLowerCase(), COUNTRY_CODE.toLowerCase(), INCLUDE_RANGE.toLowerCase(),
            INCLUDE_FIXED.toLowerCase(), INCLUDE_SIMPLIFY.toLowerCase()
    );

    private static final String PATH = "src/test/resources/product";
    private static final String PRODUCT_BY_ID = PATH + "/product_by_id_response.json";
    private static final String PRODUCTS_PAGED_FILTERED = PATH + "/products_paged_filtered_response.json";
    private static final String PRODUCTS_PAGED_UNFILTERED = PATH + "/products_paged_unfiltered_response.json";
    private static final String PRODUCTS_LIST_BY_COUNTRY_FILTERED = PATH + "/products_list_by_country_filtered_response.json";
    private static final String PRODUCTS_LIST_BY_COUNTRY_UNFILTERED = PATH + "/products_list_by_country_unfiltered_response.json";


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
    public void testListGiftcardProductsWithNoFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<GiftcardProduct>> request = giftcardAPI.products().list();
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PRODUCTS_PAGED_UNFILTERED, 200);
        Page<GiftcardProduct> productPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/products"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
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

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();

        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Request<Page<GiftcardProduct>> request = giftcardAPI.products().list(filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PRODUCTS_PAGED_FILTERED, 200);
        Page<GiftcardProduct> giftcardProductsPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        Set<String> queryParameterNames = Objects.requireNonNull(requestUrl).queryParameterNames();
        queryParameterNames.forEach(param -> Assertions.assertTrue(validFilters.contains(param.toLowerCase())));
        assertThat(requestUrl.querySize(), equalTo(7));
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", "/products"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PAGE, String.valueOf(page)));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PAGE_SIZE, String.valueOf(pageSize)));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PRODUCT_NAME, "xbox"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(COUNTRY_CODE, "US"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_RANGE, "true"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_FIXED, "true"));
        giftcardProductsPage.getContent().forEach(this::assertIsValidGiftcardProduct);
    }

    @Test
    public void testListGiftcardProductsByCountryCodeWithNoFilters() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        String countryCode = CountryCode.US.getAlpha2();
        Request<List<GiftcardProduct>> request = giftcardAPI.products().listByCountryCode(CountryCode.US);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PRODUCTS_LIST_BY_COUNTRY_UNFILTERED, 200);
        List<GiftcardProduct> products = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/countries/" + countryCode + "/products";
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
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

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();

        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        String countryCode = CountryCode.US.getAlpha2();
        Request<List<GiftcardProduct>> request = giftcardAPI.products().listByCountryCode(CountryCode.US, filter);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PRODUCTS_LIST_BY_COUNTRY_FILTERED, 200);
        List<GiftcardProduct> giftcardProductsPage = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        String endPoint = "/countries/" + countryCode + "/products";
        Set<String> queryParameterNames = Objects.requireNonNull(requestUrl).queryParameterNames();
        queryParameterNames.forEach(param -> Assertions.assertTrue(validFilters.contains(param.toLowerCase())));
        assertThat(requestUrl.querySize(), equalTo(7));
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PAGE, String.valueOf(page)));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PAGE_SIZE, String.valueOf(pageSize)));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(PRODUCT_NAME, "xbox"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(COUNTRY_CODE, "US"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_RANGE, "true"));
        assertThat(recordedRequest, RecordedRequestMatcher.hasQueryParameter(INCLUDE_FIXED, "true"));
        giftcardProductsPage.forEach(this::assertIsValidGiftcardProduct);
    }

    @Test
    public void testListGiftcardProductById() throws Exception {

        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Field baseUrlField = giftcardAPI.getClass().getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(giftcardAPI, HttpUrl.parse(server.getBaseUrl()));

        Long productId = 10L;
        Request<GiftcardProduct> request = giftcardAPI.products().getById(productId);
        assertThat(request, is(notNullValue()));
        server.jsonResponse(PRODUCT_BY_ID, 200);
        GiftcardProduct product = request.execute();
        RecordedRequest recordedRequest = server.takeRequest();

        String endPoint = "/products/" + productId;
        assertThat(recordedRequest, RecordedRequestMatcher.hasMethodAndPath("GET", endPoint));
        assertThat(recordedRequest, RecordedRequestMatcher.hasHeader(ACCEPT, GIFTCARD_V1.getValue()));

        assertThat(Objects.requireNonNull(recordedRequest.getRequestUrl()).querySize(), equalTo(0));
        assertIsValidGiftcardProduct(product);
    }

    @Test
    public void testListGiftcardProductByIdShouldThrowExceptionWhenProductIdIsNull() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.products().getById(null));
        Assertions.assertEquals("'Product id' cannot be null!", exception.getMessage());
    }

    @Test
    public void testListGiftcardProductByIdShouldThrowExceptionWhenProductIdIsLessThanZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.products().getById(-5L));
        Assertions.assertEquals("'Product id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testListGiftcardProductByIdShouldThrowExceptionWhenProductIdIsEqualToZero() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.products().getById(0L));
        Assertions.assertEquals("'Product id' must be greater than zero!", exception.getMessage());
    }

    @Test
    public void testListGiftcardProductsByCountryCodeShouldThrowExceptionWhenCountryCodeIsNull() {
        GiftcardAPI giftcardAPI = GiftcardAPI.builder().accessToken(GiftcardAPIMockServer.ACCESS_TOKEN).build();
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> giftcardAPI.products().listByCountryCode(null));
        Assertions.assertEquals("'Country code' cannot be null!", exception.getMessage());
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
