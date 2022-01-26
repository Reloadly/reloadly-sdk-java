# Giftcard Redeem Instructions Operations

Reloadly provides detailed instructions on how gift cards can be redeemed. The Redeem Instructions Operations allow you 
to provide information on how to retrieve these redeem instructions.

## Redeem Instructions - List

You can get a list of the redeem instructions that exist for all the gift card brands.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

        Request<List<GiftcardRedeemInstruction>> request = null;

        try {
        request = giftcardAPI.redeemInstructions().list();
        } catch (ReloadlyException e) {
        log.error("api error retrieving access token");
        }

        List<GiftcardRedeemInstruction> redeemInstructions = null;

        try {
        assert request != null;
        redeemInstructions = request.execute();
        } catch (APIException e) {
        log.error("Api Error");
        } catch (ReloadlyException e) {
        log.error("Request Error");
        } catch (Exception e) {
        log.error("Other Errors");
        }

```

## Redeem Instructions - Get by brand id

You can get a redeem instruction using the brand id

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

        Request<GiftcardRedeemInstruction> request = null;

        try {
        request = giftcardAPI.redeemInstructions().getByBrandId(23L);
        } catch (ReloadlyException e) {
        log.error("api error retrieving access token");
        }

        GiftcardRedeemInstruction redeemInstruction = null;

        try {
        assert request != null;
        redeemInstruction = request.execute();
        } catch (APIException e) {
        log.error("Api Error");
        } catch (ReloadlyException e) {
        log.error("Request Error");
        } catch (Exception e) {
        log.error("Other Errors");
        }

```