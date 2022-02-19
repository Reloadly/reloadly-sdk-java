# Giftcard API

The implementation is based on the [Giftcard API Docs](https://docs.reloadly.com/giftcards/).

## Usage
Create a `GiftcardAPI` instance by providing the Application details (client id & secret) from
the [dashboard](https://www.reloadly.com/developers/api-settings).

Some key things to keep in mind regarding the Gitfcard API :

* The API has 2 environments, SANDBOX (for development & testing) and LIVE.
* If neither environment is specified, the SDK defaults to SANDBOX.
* Each environment has a set of credentials (client id & secret) that are different from the other.<br />
    * SANDBOX credentials can only be used for SANDBOX environment.
    * LIVE credentials can only be used for LIVE environment.
* If no environment is specified, the SDK defaults to SANDBOX.
* You MUST supply either the credentials, or an access token in order to call the API
  <br /><br />

As stated above, requests to the Giftcard API require authentication/authorization, there are several options :

### Option 1

Set the `client id` & `client secret`; this is probably the most straight-forward or simplest way. An `access token` will be
acquired automatically before the API call is made.

```java
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .environment(Environment.SANDBOX) //Giftcard service has 2 environments, LIVE and SANDBOX. If not environment is specified, the SDK defaults to SANDBOX
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

### Option 2

You may alternatively acquire an `access token` from the
[AuthenticationAPI](https://github.com/reloadly/reloadly-sdk-java/blob/master/reloadly-java-sdk-authentication/USAGE.md)
and then set it.

```java
OAuth2ClientCredentialsRequest tokenRequest = AuthenticationAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .service(Service.GIFTCARD_SANDBOX)
        .build()
        .clientCredentials()
        .getAccessToken();

TokenHolder tokenHolder = null;
try {      
    tokenHolder = tokenRequest.execute();    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}

Request<GiftcardDiscount> request = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .accessToken(tokenHolder.getAccessToken()) //Set the access token to be used by here
        .environment(Environment.SANDBOX)
        .build()
        .discounts().getByProductId(productId);

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

**Note : Access tokens obtained for Reloadly APIs have a finite
lifetime. [See the API docs](https://developers.reloadly.com/#authentication_auth_anc)**

Using the example above has some benefits and drawbacks:

#### Pro

* API requests become efficient & performant.
* Setting the access token skips the automatic token acquisition API calls that would have otherwise been made before
  each Airtime API service calls.

#### Cons

* However, because access tokens have a finite lifetime, you now have to manage or handle the expiration of the access
  token in your application code.
* In the sample above, the `GiftcardAPI` will continue using the same access token until it expires. Therefore, the
  responsibility falls on you to handle token renewal when the token expires.

### Sample token expiration handling

```java
//Retrieve token using AuthenticationAPI
OAuth2ClientCredentialsRequest tokenRequest = AuthenticationAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .service(Service.GIFTCARD_SANDBOX)
        .build()
        .clientCredentials()
        .getAccessToken();

TokenHolder tokenHolder = null;
try {      
    tokenHolder = tokenRequest.execute();    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}

Request<GiftcardDiscount> request = GiftcardAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .accessToken(tokenHolder.getAccessToken()) //Set the access token to be used by here
        .environment(Environment.SANDBOX)
        .build()
        .discounts().getByProductId(productId);

GiftcardDiscount discount = null;
try {
    discount = request.execute();                                                       
} catch (APIException e) {
    if (e.getErrorCode().equals("TOKEN_EXPIRED")) {//Refresh the access token if it's expired
        giftcardAPI.refreshAccessToken(request);
        discount = request.execute(); //Re-execute the request
    } else {
        //Handle other errors....
    }
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}
```

### Logging request & response

To enable API request/response logging

```java
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .enableLogging(true)
        .redactHeaders(Collections.singletonList(HttpHeader.AUTHORIZATION)) //Prevent the access token from being displayed in the logs
        ....              
```

## Customizing The API Client Instance

### Configuring Timeouts

Used to configure additional options, connect and read timeouts can be configured globally:

```java
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .options(HttpOptions.builder()
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60)).build()
        )
        ....     
```

### Proxy Configuration

```java
int proxyPort = 8085; //Your proxy port
String username = "your-proxy-authentication-username"; //Optional proxy username if your proxy requires authentication
String password = "your-proxy-authentication-password"; //Optional proxy password if your proxy requires authentication
String proxyHost = "you-proxy-host-name.com";
Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .options(HttpOptions.builder()
                 .proxyOptions(new ProxyOptions(proxy, username, password.toCharArray()))
                 .build()
        )
        ....
        
//If proxy does NOT require authentication
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .options(HttpOptions.builder()
                 .proxyOptions(new ProxyOptions(proxy))
                 .build()
        )
        ....                
```

### Request latency telemetry

By default, the library sends request latency telemetry to `Reloadly`. These numbers help `Reloadly` improve the overall
latency of its API for all users.

You can disable this behavior if you prefer:

```java
GiftcardAPI giftcardAPI = GiftcardAPI.builder()
        .enableTelemetry(false)
        ....
```