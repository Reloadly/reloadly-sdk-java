# Authentication API

The implementation is based on the [Authentication API Docs](https://developers.reloadly.com/#authentication-api).

## Usage

Create an `AuthenticationAPI` instance by providing the Application credentials details (client id & secret) from
the [dashboard](https://www.reloadly.com/developers/api-settings).

```java
OAuth2ClientCredentialsRequest request = AuthenticationAPI.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .service(Service.AIRTIME_SANDBOX) //The service to request a access token for. For live Service.AIRTIME_LIVE
        .build()
        .clientCredentials()
        .getAccessToken();

TokenHolder tokenHolder = null;
try {       
    tokenHolder = request.execute();    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}

//Access token
System.out.printlng(tokenHolder.getToken());
```

### Logging request & response

To enable API request/response logging

```java
OAuth2ClientCredentialsRequest request = AuthenticationAPI.builder()
        .enableLogging(true)
        ....    

TokenHolder tokenHolder = null;
try {       
    request.execute();    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}         
```

## Customizing The API Client Instance

### Configuring Timeouts

Used to configure additional options, connect and read timeouts can be configured globally:

```java
OAuth2ClientCredentialsRequest request = AuthenticationAPI.builder()
        .options(HttpOptions.builder()
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60)).build()
        )
        ....

TokenHolder tokenHolder = null;
try {       
    request.execute();    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}        
```

### Proxy Configuration

```java
int proxyPort = 8085; //Your proxy port
String username = "your-proxy-authentication-username"; //Optional proxy username if your proxy requires authentication
String password = "your-proxy-authentication-password"; //Optional proxy password if your proxy requires authentication
String proxyHost = "you-proxy-host-name.com";
Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

OAuth2ClientCredentialsRequest request = AuthenticationAPI.builder()
        .options(HttpOptions.builder()
                 .proxyOptions(new ProxyOptions(proxy, username, password.toCharArray()))
                 .build()
        )
        ....
        
//If proxy does NOT require authentication

OAuth2ClientCredentialsRequest request = AuthenticationAPI.builder()
        .options(HttpOptions.builder()
                 .proxyOptions(new ProxyOptions(proxy))
                 .build()
        )
        ....        

TokenHolder tokenHolder = null;
try {       
    request.execute();    
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}         
```

### Request latency telemetry

By default, the library sends request latency telemetry to Reloadly. These numbers help Reloadly improve the overall
latency of its API for all users.

You can disable this behavior if you prefer:

```java
OAuth2ClientCredentialsRequest request = AuthenticationAPI.builder()
            .enableTelemetry(false)
            ....
            
TokenHolder tokenHolder = null;
try {    
    request.execute();
} catch (APIException e) {
    // api error
} catch (ReloadlyException e) {
    // request error
} catch (Exception e) {
    // all others
}    
```