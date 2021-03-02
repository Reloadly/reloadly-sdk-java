# Operators Operations

Apart from supporting Over 140 Countries, Reloadly also supports 600+ Operators. The SDK operators options allows for
the retrieval complete detail of each operator, including what type of operator this is, what topup types it support and
even details on the commissions for the operator.

Within the reloadly platform, There exists two types of Operators. One that support Range values (Anything between the
minimum and maximum range). While the other that support Fixed values (Only a certain values are supported). Reloadly
will return you the type of the operator within the response in denominationType variable. If this is set to ```RANGE```
you will receive the minimum and maximum values in the minAmount and maxAmount variables for that operator. However, if
the denomination type is ```FIXED``` you will not get these values but rather get an array of all values supported in
the fixedAmounts variable. **Now a point to remember here is that these values are already converted into your account's
currency**.

## Operators - List

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .accessToken(accessToken)
        .environment(Environment.SANDBOX)
        .build();
        
Request<Page<Operator>> request;

try {
    request = airtimeAPI.operators().list();
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Page<Operator> operatorsPage = null;
try {
    operatorsPage = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}         
```

By default, ```airtimeAPI.operators().list()``` returns a paginated list of operators with 200 operators per page. Only
the first page is returned, subsequent pages can be requested like so :

```java
int pageNumber = 1;
int pageSize = 200;
OperatorFilter filter = new OperatorFilter().withPage(pageNumber, pageSize);
Request<Page<Operator>> request = airtimeAPI.operators().list(filter);

Set<Operator> operatorsSet = new HashSet<>();
try {

    Page<Operator> operatorsPage;
    do {
        operatorsPage = request.execute();
        operatorsSet.addAll(operatorsPage.getContent());//Accumulate the operators
        filter = filter.withPage(pageNumber++, pageSize)//Increment the page number by 1
        request = airtimeAPI.operators().list();
    } while (operatorsPage.hasNext());
    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 

//Use the operatorsSet ...

```

## Operators - List with filters

Additional operator filters may be enabled

```java
int page = 1;
int pageSize = 200;
OperatorFilter filter = new OperatorFilter().withPage(pageNumber, pageSize)
        .includeBundles(true) //Whether to include bundle operators in the returned resource list. See field "bundle" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).
        .includePin(true) //Whether to include PIN based operators in the returned resource list. See field "pin" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).
        .includeData(true) //Whether to include data (internet) operators in the returned resource list. See field "data" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).
        .includeSuggestedAmounts(true) //Whether to populate the suggestedAmounts field on the operators in the returned resource list, this only applies to operators where denominationType is RANGE. See field "suggestedAmounts" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).
        .includeSuggestedAmountsMap(true) //Whether to populate the suggestedAmountsMap field on the operators in the returned resource list. This field represents a map of international amounts to local amounts for a given operator where applicable. See field "suggestedAmountsMap" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).
        .includeRangeDenominationType(true) //Whether to include operators with denomination type RANGE in the returned resource list. See field "denominationType" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).
        .includeFixedDenominationType(true); //Whether to include operators with denomination type FIXED in the returned resource list. See field "denominationType" on the [API Docs](https://developers.reloadly.com/api.html#list-all-operators).

Request<Page<Operator>> request = airtimeAPI.operators().list(filter); 
...   
```

By default all the above filters are set to ```true``` except for ```includeSuggestedAmounts```
and ```includeSuggestedAmountsMap```

## Operators - Get by id

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();
        
Request<Operator> request;

try {
    request = airtimeAPI.operators().getById(103L);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Operator operator = null;
try {
    operator = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Operators - Get by id with filters

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

OperatorFilter filter = new OperatorFilter()
        .includeSuggestedAmounts(true)
        .includeSuggestedAmountsMap(true); //Suggested amounts map will get populated only when possible
                        
Request<Operator> request;

try {
        request = airtimeAPI.operators().getById(103L, filter);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Operator operator = null;
try {
    operator = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Operators - Get by country

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<List<Operator>> request;

try {
    request = airtimeAPI.operators().listByCountryCode(CountryCode.ES);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

List<Operator> operators = null;
try {
    operators = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Operators - Get by country with filters

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

OperatorFilter filter = new OperatorFilter().withPage(pageNumber, pageSize)
        .includeBundles(true)
        .includePin(true) 
        .includeData(true) 
        .includeSuggestedAmounts(true) 
        .includeSuggestedAmountsMap(true) 
        .includeRangeDenominationType(false) 
        .includeFixedDenominationType(false); 

Request<List<Operator>> request;

try {
    request = airtimeAPI.operators().listByCountryCode(CountryCode.ES, filter);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

List<Operator> operators = null;
try {
    operators = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Operators - Auto-detect

Reloadly also provide a simple way to automatically detect the operator for any given number.

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

String phoneNumber = "+50936377111";
Request<Operator> request;

try {
    request = airtimeAPI.operators().autoDetect(phoneNumber, CountryCode.HT);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Operator operator = null;
try {
    operator = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Operators - Auto-detect with filters

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

OperatorFilter filter = new OperatorFilter()
        .includeSuggestedAmounts(true)
        .includeSuggestedAmountsMap(true);

String phoneNumber = "+50936377111";
Request<Operator> request;

try {
    request = airtimeAPI.operators().autoDetect(phoneNumber, CountryCode.HT, filter);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Operator operator = null;
try {
    operator = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```

## Operators - Calculate FX rate

In order to estimate what amount will be received on the receiver end. For example, If your account is in US Dollar and
you are trying to send a transaction to a nigerian operator, you can quickly calculate what amount you will receive in
Nigerian Naira.

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Long operatorId = 342L //Operator.id
double amount = 5.00; //Amount to calculate fx for. This amount is assumed to be in your Reloadly account currency
Request<OperatorFxRate> request;

try {
    request = airtimeAPI.operators().calculateFxRate(operatorId, amount);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

OperatorFxRate operatorFxRate = null;
try {
    operatorFxRate = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
} 
```