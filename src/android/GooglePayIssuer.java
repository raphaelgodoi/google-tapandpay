package com.raphaelgodoi;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.widget.Toast;
import android.util.Log;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import com.google.android.gms.tapandpay.TapAndPay;

import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_NO_ACTIVE_WALLET;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tapandpay.TapAndPayClient;
import com.google.android.gms.tapandpay.issuer.PushTokenizeRequest;
import com.google.android.gms.tapandpay.issuer.UserAddress;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class GooglePayIssuer extends CordovaPlugin {

    private static final String TAG = "GooglePayIssuer";
    private static final int REQUEST_CODE_PUSH_TOKENIZE = 3;
    private CordovaInterface cordova;
    private TapAndPayClient tapAndPayClient;
    private String walletId;
    private String hardwareId;

    private JSONObject joReturn = new JSONObject();

    public GooglePayIssuer() {
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordova = cordova;
        tapAndPayClient = TapAndPayClient.getClient(this.cordova.getActivity());

        Log.i(TAG, "INITIALIZED");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, action);
        Log.i(TAG, args.toString());

        if ("getActiveWalletID".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getActiveWalletID(callbackContext);
                }
            });
        } else if ("getStableHardwareId".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getStableHardwareId(callbackContext);
                }
            });
        } else if ("pushProvision".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String cardFirstNumber = args.getString(1);
                        int cardNetwork = 0;
                        int tokenServiceProvider = 0;

                        String opc = args.getString(0);

                        if (cardFirstNumber == "4") {
                            cardNetwork = TapAndPay.CARD_NETWORK_VISA;
                            tokenServiceProvider = TapAndPay.TOKEN_PROVIDER_VISA;
                        } else if (cardFirstNumber == "5") {
                            cardNetwork = TapAndPay.CARD_NETWORK_MASTERCARD;
                            tokenServiceProvider = TapAndPay.TOKEN_PROVIDER_MASTERCARD;
                        }
                        
                        String clientName = args.getString(2);
                        String lastDigits = args.getString(3);
                        JSONObject address = args.getJSONObject(4);

                        if (opc.isEmpty() || cardNetwork == 0 || tokenServiceProvider == 0 || clientName.isEmpty() || lastDigits.isEmpty())
                            callbackContext.error("The data provided was not fully completed");

                        pushProvision(opc, cardNetwork, tokenServiceProvider, clientName, lastDigits, address, callbackContext);
                    } catch (Exception e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
        }

        return false;
    }

    private void getActiveWalletID(CallbackContext callbackContext) {

        Log.i(TAG, "Google connected");
        tapAndPayClient
                .getActiveWalletId()
                .addOnCompleteListener(
                        new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                Log.i(TAG, "onComplete (getActiveWalletID) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    walletId = task.getResult();
                                    Log.i(TAG, "SUCCESS-getActiveWalletID");
//                                            joReturn.put("")
                                    callbackContext.success("success");
                                } else {
                                    Log.i(TAG, "ERROR-getActiveWalletID");
                                    ApiException apiException = (ApiException) task.getException();
                                    Log.i(TAG, apiException.getMessage());
                                    if (apiException.getStatusCode() == TAP_AND_PAY_NO_ACTIVE_WALLET) {
                                        // If no Google Pay wallet is found, create one and then call
                                        // getActiveWalletId() again.

                                    } else {
                                        //Failed to get active wallet ID

                                    }
                                    callbackContext.error("error");
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure (getActiveWalletID) - " + e.getMessage());
                        callbackContext.error("error");
                    }
                })
                .addOnCanceledListener(
                        new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.i(TAG, "onCanceled (getActiveWalletID) - ");
                                callbackContext.error("error");
                            }
                        });
    }

    private void getStableHardwareId(CallbackContext callbackContext) {
        tapAndPayClient
                .getStableHardwareId()
                .addOnCompleteListener(
                        new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                Log.i(TAG, "onComplete (getStableHardwareId) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    hardwareId = task.getResult();
                                    callbackContext.success("success");
                                } else {
                                    hardwareId = "";
                                    callbackContext.error("error");
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure (getStableHardwareId) - " + e.getMessage());
                                callbackContext.error("error");
                            }
                        })
                .addOnCanceledListener(
                        new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.i(TAG, "onCanceled (getStableHardwareId) - ");
                                callbackContext.error("error");
                            }
                        });
    }

    private void getEnvironment(CallbackContext callbackContext) {
        tapAndPayClient
                .getEnvironment()
                .addOnCompleteListener(
                        new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                Log.i(TAG, "onComplete (getEnvironment) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    String env = task.getResult();
                                    callbackContext.success("success");
                                } else {
                                    hardwareId = "";
                                    callbackContext.error("error");
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure (getEnvironment) - " + e.getMessage());
                                callbackContext.error("error");
                            }
                        })
                .addOnCanceledListener(
                        new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.i(TAG, "onCanceled (getEnvironment) - ");
                                callbackContext.error("error");
                            }
                        });
    }

    private void pushProvision(String opc, int cardNetwork, int tokenServiceProvider, String clientName, String lastDigits, JSONObject address, CallbackContext callbackContext) {

        try {
            UserAddress userAddress =
                    UserAddress.newBuilder()
                            .setName(address.getString("name"))
                            .setAddress1(address.getString("address"))
                            .setLocality(address.getString("locality"))
                            .setAdministrativeArea(address.getString("administrativeArea"))
                            .setCountryCode(address.getString("countryCode"))
                            .setPostalCode(address.getString("postalCode"))
                            .setPhoneNumber(address.getString("phoneNumber"))
                            .build();

            PushTokenizeRequest pushTokenizeRequest =
                    new PushTokenizeRequest.Builder()
                            .setOpaquePaymentCard(opc.getBytes())
                            .setNetwork(cardNetwork)
                            .setTokenServiceProvider(tokenServiceProvider)
                            .setDisplayName(clientName)
                            .setLastDigits(lastDigits)
                            .setUserAddress(userAddress)
                            .build();

            tapAndPayClient.pushTokenize(this.cordova.getActivity(), pushTokenizeRequest, REQUEST_CODE_PUSH_TOKENIZE);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_PUSH_TOKENIZE) {
//            if (resultCode == RESULT_CANCELED) {
//                // The user canceled the request.
//                return;
//            } else if (resultCode == RESULT_OK) {
//                // The action succeeded.
//                String tokenId = data.getStringExtra(TapAndPay.EXTRA_ISSUER_TOKEN_ID);
//                // Do something with tokenId.
//                return;
//            }
//        }
//        // Handle results for other request codes.
//        // ...
//    }

    private void show(String msg, CallbackContext callbackContext) {
        if (msg == null || msg.length() == 0) {
            callbackContext.error("Empty message!");
        } else {
            Toast.makeText(webView.getContext(), msg, Toast.LENGTH_LONG).show();
            callbackContext.success(msg);
        }
    }
}