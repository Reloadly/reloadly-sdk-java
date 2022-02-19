# Giftcard Transactions Operations

Here, you can retrieve details of giftcard transactions.

## Transactions - List transaction history

You can get the transaction history 

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

        Request<Page<GiftcardTransaction>> request = null;

        try {
        request = giftcardAPI.transactionsHistory().list();
        } catch (ReloadlyException e) {
        log.error("api error retrieving access token");
        }

        Page<GiftcardTransaction> giftcardTransactionPage = null;

        try {
        assert request != null;
        giftcardTransactionPage = request.execute();
        } catch (APIException e) {
        log.error("Api Error");
        } catch (ReloadlyException e) {
        log.error("Request Error");
        } catch (Exception e) {
        log.error("Other Errors");
        }

```

## Transactions - List transaction history with filters

You can filter by start date, end date, status, brand id, product name, etc.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

        int pageNumber = 1;
        int pageSize = 200;
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        GiftcardTransactionFilter filter = new GiftcardTransactionFilter().withPage(pageNumber, pageSize)
        .status(GiftCardTransactionStatus.SUCCESSFUL)
        .startDate(startDate)
        .endDate(endDate);

        //Additional filters are available
        /*      .brandId(12L)
                .brandName("Amazon")
                .productId(3L)
                .productName("Amazon Spain")
                .recipientEmail("johndoe@gmail.com")
                .customIdentifier("Your-Transaction-Custom-Identifier");*/

        Request<Page<GiftcardTransaction>> request = null;

        try {
        request = giftcardAPI.transactionsHistory().list(filter);
        } catch (ReloadlyException e) {
        log.error("api error retrieving access token");
        }

        Page<GiftcardTransaction> giftcardTransactionPage = null;

        try {
        assert request != null;
        giftcardTransactionPage = request.execute();
        } catch (APIException e) {
        log.error("Api Error");
        } catch (ReloadlyException e) {
        log.error("Request Error");
        } catch (Exception e) {
        log.error("Other Errors");
        }

```

## Transactions - Get by id

You can retrieve a specific transaction using its id

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

        Request<GiftcardTransaction> request = null;

        try {
        request = giftcardAPI.transactionsHistory().getById(234L);
        } catch (ReloadlyException e) {
        log.error("api error retrieving access token");
        }

        GiftcardTransaction giftcardTransaction = null;

        try {
        assert request != null;
        giftcardTransaction = request.execute();
        } catch (APIException e) {
        log.error("Api Error");
        } catch (ReloadlyException e) {
        log.error("Request Error");
        } catch (Exception e) {
        log.error("Other Errors");
        }

```