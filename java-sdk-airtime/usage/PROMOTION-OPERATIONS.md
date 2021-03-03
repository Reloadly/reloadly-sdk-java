# Promotions Operations

Reloady also support operators promotions. These are provided by the operators and can be activated by sending a
specific topup amount as per the details of the promotion. Using the promotion operations you can retrieve all details
on the different operators promotions and to showcase these to your customers.

## Promotions - List

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<Page<Promotion>> request;

try {
    request = airtimeAPI.promotions().list();
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<Promotion> promotionPage = null;
try {
    promotionPage = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Promotions - List with filters

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

int pageNumber = 1;
int pageSize = 5;
QueryFilter filter = new QueryFilter().withPage(pageNumber, pageSize);
Request<Page<Promotion>> request;

try {
    request = airtimeAPI.promotions().list(filter);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<Promotion> promotionPage = null;
try {
    promotionPage = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Promotions - Get by id

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Long promotionId = 5441L;//From Promotion.id
Request<Promotion> request;

try {
    request = airtimeAPI.promotions().getById(promotionId);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Promotion promotion = null;
try {
    promotion = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Promotions - Get by operator id

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Long operatorId = 681L;//From Operator.id
Request<List<Promotion>> request;

try {
    request = airtimeAPI.promotions().getByOperatorId(operatorId);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

List<Promotion> promotions = null;
try {
    promotions = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Promotions - Get by country code

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<List<Promotion>> request;

try {
    request = airtimeAPI.promotions().getByCountryCode(CountryCode.EC);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}


List<Promotion> promotions = null;
try {
    promotions = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```