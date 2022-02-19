# Giftcard Products Operations

Reloadly supports over 13,000 gift card products spanning multiple brands in multiple countries. The Products Operations 
allow you to get information about these products

## Products - List

You can get a list of all the gift card products offered by Reloadly.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        Request<Page<GiftcardProduct>> request = null;

        try {
            request = giftcardAPI.products().list();
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        Page<GiftcardProduct> giftcardProducts = null;

        try {
            assert request != null;
            giftcardProducts = request.execute();
        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }
```
By default, ```giftcardAPI.products().list()``` returns a paginated list of products with 200 products per page. Only 
the first page is returned, subsequent pages can be returned like so:

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        int pageNumber = 1;
        int pageSize = 200;
        GiftcardProductFilter filter = new GiftcardProductFilter().withPage(pageNumber, pageSize);

        Request<Page<GiftcardProduct>> request = null;
        try {
            request = giftcardAPI.products().list(filter);
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        Set<GiftcardProduct> giftCardProductSet = new HashSet<>();
        Page<GiftcardProduct> giftCardProducts = null;

        try {
            do {
                assert request != null;
                giftCardProducts = request.execute();
                giftCardProductSet.addAll(giftCardProducts.getContent());
                filter = filter.withPage(pageNumber++, pageSize);
                request = giftcardAPI.products().list(filter);
            } while (giftCardProducts.hasNext());

        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }
        // Use the giftCardProductSet
```

## Products - List with filters

You can filter by product name, country code, etc.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX)
        .build();

        int pageNumber = 1;
        int pageSize = 200;
        String productName = "Amazon Spain";
        GiftcardProductFilter filter = new GiftcardProductFilter().withPage(pageNumber, pageSize)
        .productName(productName)
        .countryCode(CountryCode.NG)
        .simplified(true)
        .includeRange(true)
        .includeFixed(true);

        Request<Page<GiftcardProduct>> request = null;

        try {
        request = giftcardAPI.products().list(filter);
        } catch (ReloadlyException e) {
        log.error("api error retrieving access token");
        }

        Page<GiftcardProduct> giftcardProducts = null;

        try {
        assert request != null;
        giftcardProducts = request.execute();
        } catch (APIException e) {
        log.error("Api Error");
        } catch (ReloadlyException e) {
        log.error("Request Error");
        } catch (Exception e) {
        log.error("Other Errors");
        }

```
By default, all the boolean values in the filter above are set to ```true``` except for ```simplified```

## Products - List products by country code

You can search for products that exist within a given country by searching using the country code.

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        Request<List<GiftcardProduct>> request = null;

        try {
            request = giftcardAPI.products().listByCountryCode(CountryCode.NG);
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        List<GiftcardProduct> giftcardProducts = null;

        try {
            assert request != null;
            giftcardProducts = request.execute();
        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }

```

## Products - List products by country code using filters

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        int pageNumber = 1;
        int pageSize = 200;
        String productName = "Amazon Spain";
        GiftcardProductFilter filter = new GiftcardProductFilter().withPage(pageNumber, pageSize)
                .productName(productName)
                .simplified(true)
                .includeRange(true)
                .includeFixed(true);

        Request<List<GiftcardProduct>> request = null;

        try {
            request = giftcardAPI.products().listByCountryCode(CountryCode.NG, filter);
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        List<GiftcardProduct> giftcardProducts = null;

        try {
            assert request != null;
            giftcardProducts = request.execute();
        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }

```

## Products - Get by id

You can get a product using its id

```java
        GiftcardAPI giftcardAPI = GiftcardAPI.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(Environment.SANDBOX)
                .build();

        Request<GiftcardProduct> request = null;

        try {
            request = giftcardAPI.products().getById(234L);
        } catch (ReloadlyException e) {
            log.error("api error retrieving access token");
        }

        GiftcardProduct giftcardProduct = null;

        try {
            assert request != null;
            giftcardProduct = request.execute();
        } catch (APIException e) {
            log.error("Api Error");
        } catch (ReloadlyException e) {
            log.error("Request Error");
        } catch (Exception e) {
            log.error("Other Errors");
        }

```