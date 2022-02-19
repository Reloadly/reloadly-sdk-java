# Giftcard Discounts Operations

Discounts or commissions are a way for you to check what percentage discount rate you will get for each **Giftcard
Product** when you make a successful order. These operations can be used to calculate your profits. All Commissions are
paid instantly when an order is processed.

Thing to note on the response are the fields **global**, which indicate if the gift card can be used globally across
different countries, and **percentage**, which indicates the percentage discount available for every purchase of the
gift card.

## Giftcard Discounts - List

```java
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<Page<GiftcardDiscount>> request;
try {
        request = giftcardAPI.discounts().list();
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<GiftcardDiscount> discountPage = null;
try {
    discountPage = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}  
```

## Giftcard Discounts - Get by product id

```java
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Long productId = 174L; //From GiftcardProduct.id
Request<GiftcardDiscount> request;

try {
    request = giftcardAPI.discounts().getByProductId(productId);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

GiftcardDiscount discount = null;
try {
    discount = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}  
```
