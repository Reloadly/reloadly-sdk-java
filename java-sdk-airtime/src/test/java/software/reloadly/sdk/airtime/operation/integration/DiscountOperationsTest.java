package software.reloadly.sdk.airtime.operation.integration;

import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Discount;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.filter.QueryFilter;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DiscountOperationsTest extends BaseIntegrationTest {

    @Test
    public void testListDiscounts() throws Exception {

        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<Page<Discount>> request = airtimeAPI.discounts().list();
        assertThat(request, is(notNullValue()));
        Page<Discount> discountPage = request.execute();
        discountPage.getContent().forEach(this::assertIsValidDiscount);
    }

    @Test
    public void testListDiscountsWithFilters() throws Exception {

        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        QueryFilter filter = new QueryFilter().withPage(1, 200);
        Request<Page<Discount>> request = airtimeAPI.discounts().list(filter);
        assertThat(request, is(notNullValue()));
        Page<Discount> discountPage = request.execute();
        discountPage.getContent().forEach(this::assertIsValidDiscount);
    }

    @Test
    public void testGetByOperatorId() throws Exception {

        Long operatorId = 174L;
        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();
        Request<Discount> request = airtimeAPI.discounts().getByOperatorId(operatorId);
        assertThat(request, is(notNullValue()));
        Discount discount = request.execute();
        assertIsValidDiscount(discount);
    }

    private void assertIsValidDiscount(Discount discount) {

        List<String> countryFields = Arrays.asList(
                "internationalPercentage", "localPercentage", "updatedAt", "operator"
        );

        countryFields.forEach(field -> assertThat(discount, hasProperty(field)));

        assertThat(discount.getPercentage(), is(notNullValue()));
        assertThat(discount.getInternationalPercentage(), is(notNullValue()));
        assertThat(discount.getLocalPercentage(), is(notNullValue()));
        assertThat(discount.getUpdatedAt(), is(notNullValue()));
        assertThat(discount.getOperator(), is(notNullValue()));
        assertThat(discount.getOperator().getId(), is(notNullValue()));
        assertThat(discount.getOperator().getName(), is(notNullValue()));
        assertThat(discount.getOperator().getCountryCode(), is(notNullValue()));
    }
}
