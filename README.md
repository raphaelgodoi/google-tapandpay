cordova-android-googlepay-issuer
============================================

Cordova/Phonegap plugin for Android for Google Pay - Issuers


# Requirements

This plugin requires `cordova@8+` (CLI) and `cordova-android@8+` (Android platform).

# Installation

    cordova plugin add path_to_plugin

Update main build.gradle file with Google TapAndPay SKD path, e.g.:

    {
        allprojects {
            repositories {
                google()
                jcenter()
                maven { url "path_to_tap_and_pay_sdk" }
            }
        }
    }

# How to use

```javascript
    declare var GooglePayIssuer: any;

    var googlePay = new GooglePayIssuer();

    googlePay.getActiveWalletID((walletID)=>{
        console.log(walletID);
    },(error)=>{
        console.log(error);
    });
    
```

# Functions

### GetTokenStatus
Return the token status for a token in the active wallet.

```javascript
    let tsp = "VISA";
    let tokenReferenceId = "dP4Pwaq7WQY:APA9";

    getTokenStatus(tsp,tokenReferenceId,onSuccess,onError);
```

### GetEnvironment
Return the current environment Google Pay is configured to use.

```javascript
    getEnvironment(onSuccess,onError);
```

### GetActiveWalletID
Returns the Wallet ID of the active wallet. If there is no active wallet, a error is throw.

```javascript
    getActiveWalletID(onSuccess,onError);
```

### GetStableHardwareId
Returns the stable hardware ID of the device. Each physical Android device has a stable hardware ID which is consistent between wallets for a given device. This ID will change as a result of a factory reset.

```javascript
    getStableHardwareId(onSuccess,onError);
```

### PushProvision
Starts the push tokenization flow in which the issuer provides most or all card details needed for Google Pay to get a valid token. Tokens added using this method are added to the active wallet.

```javascript
    let opc = "eyJhb...0zfF20w";
    let tsp = "VISA";
    let clientName = "My name";
    let lastDigits = "1234";
    let address = {
        name:"My address name on Google",
        address: "Rua 1 Casa 2",
        locality: "São Paulo",
        administrativeArea: "SP",
        countryCode:"BR",
        postalCode: "19999",
        phoneNumber: "1199999999"
    };
    googlePay.pushProvision(opc,tsp,clientName,lastDigits,address,onSuccess,onError);
```   

# Credits
Thanks to [**Raphael Godoi**](https://github.com/raphagodoi) [**Guilherme Rodrigues**](https://github.com/Guiles92)