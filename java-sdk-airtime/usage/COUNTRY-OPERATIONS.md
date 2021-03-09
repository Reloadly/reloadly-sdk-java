# Countries Operations

Reloadly supports 140+ Countries around the globe. You can get a list of all or specific supported countries. The
response will give you a list with complete details, iso, flag as well as calling codes for each country. You can also
further filter the countries by getting details for a specific country by its ISO-Alpha2 code.
See https://www.nationsonline.org/oneworld/country_code_list.htm for more details regarding country ISO codes.

## Countries - List

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<List<Country>> request;
try {
    request = airtimeAPI.countries().list();
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

List<Country> countries = null;
try {
    countries = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}  
```

## Countries - Get by country ISO code

```java
AirtimeAPI airtimeAPI = AirtimeAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

Request<Country> request;

try {
    request = airtimeAPI.countries().getByCode(CountryCode.CO);
} catch (ReloadlyException e) {
    // api error retrieving access_token
}

Country country = null;
try {
    country = request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}  
```