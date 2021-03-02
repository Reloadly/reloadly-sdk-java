# Report Operations

Retrieve various reports such as transaction history etc...

## Reports - List transaction history

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<Page<TopupTransaction>> request;

try {
    request = airtimeAPI.reports().transactionsHistory().list();
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<TopupTransaction> transactionHistoryPage = null;
try {
    transactionHistoryPage = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Reports - List transaction history with filters

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

LocalDateTime startDate = LocalDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

TransactionHistoryFilter filter = new TransactionHistoryFilter().withPage(1, 5)
        .startDate(startDate)
        .endDate(endDate);
        
        //Additional filters are available
        /*.operatorId(operatorId)
        .countryCode(CountryCode.HT)
        .operatorName("Digicel Haiti")
        .customIdentifier("Your-Transaction-Custom-Identifier");*/
                
Request<Page<TopupTransaction>> request;

try {
    request = airtimeAPI.reports().transactionsHistory().list(filter);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<TopupTransaction> transactionHistoryPage = null;
try {
    transactionHistoryPage = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Reports - Get transaction history by id

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();
                
Long transactionId = 10658L;//From Transaction.id                
Request<TopupTransaction> request;

try {
    request = airtimeAPI.reports().transactionsHistory().getById(transactionId);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

TopupTransaction transaction = null;
try {
    transaction = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```