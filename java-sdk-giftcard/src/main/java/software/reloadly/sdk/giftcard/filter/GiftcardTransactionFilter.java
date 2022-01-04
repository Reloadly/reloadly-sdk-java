package software.reloadly.sdk.giftcard.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import software.reloadly.sdk.core.internal.filter.QueryFilter;
import software.reloadly.sdk.core.internal.util.Asserter;
import software.reloadly.sdk.giftcard.enums.GiftCardTransactionStatus;
import software.reloadly.sdk.giftcard.operation.GiftcardTransactionsOperations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used to filter the results received when calling the giftcard transactions endpoint.
 * Related to the {@link GiftcardTransactionsOperations}.
 */
@Getter
@NoArgsConstructor
public class GiftcardTransactionFilter extends QueryFilter {

    private static final String STATUS = "status";
    private static final String BRAND_ID = "brandId";
    private static final String END_DATE = "endDate";
    private static final String BRAND_NAME = "brandName";
    private static final String PRODUCT_ID = "productId";
    private static final String START_DATE = "startDate";
    private static final String PRODUCT_NAME = "productName";
    private static final String RECIPIENT_EMAIL = "recipientEmail";
    private static final String CUSTOM_IDENTIFIER = "customIdentifier";

    @Override
    public GiftcardTransactionFilter withPage(int pageNumber, int pageSize) {
        super.withPage(pageNumber, pageSize);
        return this;
    }

    public GiftcardTransactionFilter status(GiftCardTransactionStatus status) {
        Asserter.assertNotNull(status, "Status");
        parameters.put(STATUS, status.name());
        return this;
    }

    public GiftcardTransactionFilter brandId(Long brandId) {
        Asserter.assertNotNull(brandId, "Brand id");
        Asserter.assertGreaterThanZero(brandId, "Brand id");
        parameters.put(BRAND_ID, brandId);
        return this;
    }

    public GiftcardTransactionFilter startDate(LocalDateTime date) {
        Asserter.assertNotNull(date, "Start date");
        parameters.put(START_DATE, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this;
    }

    public GiftcardTransactionFilter endDate(LocalDateTime date) {
        Asserter.assertNotNull(date, "End date");
        parameters.put(END_DATE, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this;
    }

    public GiftcardTransactionFilter brandName(String brandName) {
        Asserter.assertNotBlank(brandName, "Brand name");
        parameters.put(BRAND_NAME, brandName);
        return this;
    }

    public GiftcardTransactionFilter productId(Long productId) {
        Asserter.assertNotNull(productId, "Product id");
        Asserter.assertGreaterThanZero(productId, "Product id");
        parameters.put(PRODUCT_ID, productId);
        return this;
    }

    public GiftcardTransactionFilter productName(String productName) {
        Asserter.assertNotBlank(productName, "Product name");
        parameters.put(PRODUCT_NAME, productName);
        return this;
    }

    public GiftcardTransactionFilter recipientEmail(String email) {
        Asserter.assertNotBlank(email, "Recipient email");
        Asserter.assertValidEmail(email, "Recipient email");
        parameters.put(RECIPIENT_EMAIL, email);
        return this;
    }

    public GiftcardTransactionFilter customIdentifier(String customIdentifier) {
        Asserter.assertNotBlank(customIdentifier, "Custom Identifier");
        parameters.put(CUSTOM_IDENTIFIER, customIdentifier);
        return this;
    }
}
