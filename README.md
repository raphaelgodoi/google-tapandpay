cordova-android-googlepay-issuer
============================================

Cordova/Phonegap plugin for Android for Google Pay - Issuers


# Requirements

This plugin requires `cordova@8+` (CLI) and `cordova-android@8+` (Android platform).
Since the plugin uses hook scripts it will not work in Cloud Build environments such as Phonegap Build.

# Installation

    # override using default component versions
    $ cordova plugin add path_to_plugin

You must add maven url to your TapAndPay SDK into your main build.gradle, e.g.:

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