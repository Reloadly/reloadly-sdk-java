package software.reloadly.sdk.airtime.operation.integration;

import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.response.Promotion;
import software.reloadly.sdk.airtime.interfaces.IntegrationTest;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;
import software.reloadly.sdk.core.internal.filter.QueryFilter;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PromotionOperationsTest extends BaseIntegrationTest {

    @IntegrationTest
    public void testListPromotionsWithNoFilters() throws Exception {

        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<Page<Promotion>> request = airtimeAPI.promotions().list();
        assertThat(request, is(notNullValue()));
        Page<Promotion> promotionPage = request.execute();
        promotionPage.getContent().forEach(this::assertIsValidPromotion);
    }

    @IntegrationTest
    public void testListPromotionsWithFilters() throws Exception {

        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        int page = 1;
        int pageSize = 5;
        QueryFilter filter = new QueryFilter().withPage(page, pageSize);
        Request<Page<Promotion>> request = airtimeAPI.promotions().list(filter);
        assertThat(request, is(notNullValue()));
        Page<Promotion> promotionPage = request.execute();
        assertThat(promotionPage.getContent().size(), equalTo(pageSize));
        promotionPage.getContent().forEach(this::assertIsValidPromotion);
    }

    @IntegrationTest
    public void testGetById() throws Exception {

        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        int page = 1;
        int pageSize = 5;
        QueryFilter filter = new QueryFilter().withPage(page, pageSize);
        Request<Page<Promotion>> promotionRequest = airtimeAPI.promotions().list(filter);
        Page<Promotion> promotionPage =  promotionRequest.execute();
        Long promotionId = promotionPage.getContent().get(0).getId();

        Request<Promotion> request = airtimeAPI.promotions().getById(promotionId);
        assertThat(request, is(notNullValue()));
        Promotion promotion = request.execute();
        assertIsValidPromotion(promotion);
    }

    @IntegrationTest
    public void testGetByCountryCode() throws Exception {

        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<List<Promotion>> request = airtimeAPI.promotions().getByCountryCode( CountryCode.HT);
        assertThat(request, is(notNullValue()));
        List<Promotion> promotions = request.execute();
        promotions.forEach(this::assertIsValidPromotion);
    }

    @IntegrationTest
    public void testGetByOperatorId() throws Exception {

        Long operatorId = 173L;
        AirtimeAPI airtimeAPI =AirtimeAPI.builder().environment(Environment.LIVE).accessToken(accessToken).build();

        Request<List<Promotion>> request = airtimeAPI.promotions().getByOperatorId(operatorId);
        assertThat(request, is(notNullValue()));
        List<Promotion> promotions = request.execute();
        promotions.forEach(promotion -> {
            assertIsValidPromotion(promotion);
            assertThat(promotion.getOperatorId(), equalTo(operatorId));
        });
    }

    private void assertIsValidPromotion(Promotion promotion) {

        List<String> countryFields = Arrays.asList("id", "operatorId", "title", "title2", "description", "startDate",
                "endDate", "denominations", "localDenominations"
        );

        countryFields.forEach(field -> assertThat(promotion, hasProperty(field)));

        assertThat(promotion.getId(), is(notNullValue()));
        assertThat(promotion.getOperatorId(), is(notNullValue()));
        assertThat(promotion.getTitle(), is(notNullValue()));
        assertThat(promotion.getTitle2(), is(notNullValue()));
        assertThat(promotion.getDescription(), is(notNullValue()));
        assertThat(promotion.getStartDate(), is(notNullValue()));
        assertThat(promotion.getEndDate(), is(notNullValue()));
    }
}
