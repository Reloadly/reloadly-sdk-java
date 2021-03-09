# Discounts Operations

Discounts or commissions are a way for you to check what percentage discount rate will you get for each operator when
you send a successful top-up. These operations can be used to calculate your profits. All Commissions are paid instantly
when a top-up is processed.

One thing to note on the response is that All Operators provide two types of discounts, One is the international
discount, and the other is the local discount. These are returned as the internationalPercentage and localPercentage
fields in the response object. Depending on your account currency the country you are sending the topup to, you are
eligible for either one of these discounts. For example if you're sending from the US to Canada you will be eligible for
the international discount for the canadian operator. While sending within the same country you will be eligible for the
local discount of the operator **if available**. Note that, local discount may not always be available; in which case
the international discount will be applied.

## Discounts - List

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<Page<Discount>> request;
try {
        request = airtimeAPI.discounts().list();
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<Discount> discountPage = null;
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

## Discount - List with filters

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

int pageNumber = 1;
int pageSize = 5;
QueryFilter filter = new QueryFilter().withPage(pageNumber, pageSize);
Request<Page<Discount>> request;

try {
    request = airtimeAPI.discounts().list(filter);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<Discount> discountPage = null;
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

## Discounts - Get by operator id

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Long operatorId = 174L; //From Operator.id
Request<Discount> request;

try {
    request = airtimeAPI.discounts().getByOperatorId(operatorId);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<Discount> discount = null;
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