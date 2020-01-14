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

### GetActiveWalletID
Returns the Wallet ID of the active wallet. If there is no active wallet, a error is throw.

    getActiveWalletID(onSuccess,onError);
### GetStableHardwareId
Returns the stable hardware ID of the device. Each physical Android device has a stable hardware ID which is consistent between wallets for a given device. This ID will change as a result of a factory reset.

    getStableHardwareId(onSuccess,onError);

### PushProvision
Starts the push tokenization flow in which the issuer provides most or all card details needed for Google Pay to get a valid token. Tokens added using this method are added to the active wallet.

    googlePay.pushProvision(opc,cardFirstNumber,clientName,lastDigits,address,onSuccess,onError);

# Credits
Thanks to [**Raphael Godoi**](https://github.com/raphagodoi) [**Guilherme Rodrigues**](https://github.com/Guiles92)