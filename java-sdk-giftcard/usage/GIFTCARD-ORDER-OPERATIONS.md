# Giftcard Order Operations

Use the Order Operations to order a gift card and to retrieve the details of a purchased gift card. 

## Order - Send a gift card

In order to send a gift card to a recipient, a couple of information will be required such as the quantity, product id, 
name of sender, unit price, the recipient's email or phone number and a custom identifier.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        int quantity = 3;
        Long productId = 13L;
        String senderName = "Doctor Vee";
        BigDecimal unitPrice = BigDecimal.valueOf(43.34);
        String recipientEmail = "johndoe@gmail.com";
        String customIdentifier = UUID.randomUUID().toString(); // Use your own internal reference

        GiftCardOrderRequest orderRequest = GiftCardOrderRequest.builder()
                .quantity(quantity)
                .productId(productId)
                .senderName(senderName)
                .unitPrice(unitPrice)
                .recipientEmail(recipientEmail)
                .recipientPhone(new GiftCardOrderRequest.Phone("+2348081234567", CountryCode.NG)) // Optional
                .customIdentifier(customIdentifier)
                .build();

        Request<GiftcardTransaction> request = null;

        try {
            request = giftcardAPI.orders().placeOrder(orderRequest);
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        GiftcardTransaction transaction = null;

        try {
            assert request != null;
            transaction = request.execute();
        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }
```

## Orders - Retrieve the details of a gift card

You can also retrieve the details (card number and pin code) of a gift card that has been earlier purchase. You'd just 
have to provide the transaction id.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        Long transactionId = 3L;

        Request<List<GiftcardInfo>> request = null;

        try {
            request = giftcardAPI.orders().redeem(transactionId);
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        List<GiftcardInfo> giftcardInfoList = null;

        try {
            assert request != null;
            giftcardInfoList = request.execute();
        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }

```

