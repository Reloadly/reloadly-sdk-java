# Topup Operations

In order to send a successful topup. There are a few prerequisites to the system. We need to know the phone number to
send the topup to, the operator id of the phone number, the country of the operator, the amount for the topup.

## Topups - Send topup to phone

```java
Long operatorId = 174L; //From Operator.id
double amount = 5.00; //The amount is in your Reloadly account currency
String internalReferenceId = UUID.randomUUID().toString(); //Optional, your own internal reference.

PhoneTopupRequest topupRequest = PhoneTopupRequest.builder()
        .amount(amount)
        .operatorId(operatorId)
        .customIdentifier(internalReferenceId) //Optional
        .senderPhone(new Phone("+17862541236", CountryCode.US)) //Optional
        .recipientPhone(new Phone("+50936377111", CountryCode.HT))
        .build();
        
Request<TopupTransaction> request; 

try {
    request = airtimeAPI.topups().send(topupRequest);
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

## Topups - Send topup to phone in local amount

Reloadly also provides the ability to top up using local values. By default, the values will be available in your
account’s currency, but you can maintain one wallet and top up in different local values from many countries. **Please
note that this is only possible when local values are supported in Reloadly system**. If you want to send exact amounts
in Local/Operator's/Receiver's currency, then simply set the ```.useLocalAmount(true)``` on the request object. This
will tell the platform that you are sending the amount in local currency and not in your dashboard's currency or
international pipe. Not all operator's support a local amount yet so make sure to check the operator's details to know
whether it supports local or not.

```java
Long operatorId = 342L; //From Operator.id (Airtel Nigeria)
double amount = 2000.00; //In this example here, the amount is two thousand Nigerian Naira
String internalReferenceId = UUID.randomUUID().toString(); //Optional, your own internal reference.

PhoneTopupRequest topupRequest = PhoneTopupRequest.builder()
        .amount(amount)
        .operatorId(operatorId)
        .useLocalAmount(true) // <===
        .customIdentifier(internalReferenceId) //Optional
        .senderPhone(new Phone("+17862541236", CountryCode.US)) //Optional
        .recipientPhone(new Phone("+2349045150334", CountryCode.NG))
        .build();
        
Request<TopupTransaction> request;

try {
    request = airtimeAPI.topups().send(topupRequest);
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

## Topups - Send Nauta Cuba topups

Reloadly also supports Nauta Cuba for top-ups. However, the process is a bit different from sending phone topups. Instead
of using ```PhoneTopupRequest``` use ```EmailTopupRequest```, substitute ```recipientPhone(phone)``` with
```recipientEmail(email)``` and that's it. The rest of the process is exactly the same as sending any other topup.

Note, There are two types of email domains that are allowed for Nauta Cuba Top-up : ```@nauta.com.cu```
and ```@nauta.co.cu```

```java
Long operatorId = 683L; //From Operator.id
double amount = 15.00; //The amount is in your Reloadly account currency
String internalReferenceId = UUID.randomUUID().toString(); //Optional, your own internal reference.

EmailTopupRequest topupRequest = EmailTopupRequest.builder()
        .amount(amount)
        .operatorId(operatorId)
        .customIdentifier(internalReferenceId) //Optional
        .senderPhone(new Phone("+17862541236", CountryCode.US)) //Optional
        .recipientEmail("example@nauta.com.cu")
        .build();
        
Request<TopupTransaction> request;

try {
    request = airtimeAPI.topups().send(topupRequest);
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
