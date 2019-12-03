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

    googlePay.getWalletID((walletID)=>{
        console.log(walletID);
    },(error)=>{
        console.log(error);
    });
    
```


# Credits
Thanks to [**Raphael Godoi**](https://github.com/raphagodoi)