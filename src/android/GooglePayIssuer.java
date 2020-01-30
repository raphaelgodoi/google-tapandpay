package com.raphaelgodoi;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import com.google.android.gms.tapandpay.TapAndPay;

import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_NO_ACTIVE_WALLET;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_TOKEN_NOT_FOUND;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tapandpay.TapAndPayClient;
import com.google.android.gms.tapandpay.issuer.PushTokenizeRequest;
import com.google.android.gms.tapandpay.issuer.TokenStatus;
import com.google.android.gms.tapandpay.issuer.UserAddress;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.nio.charset.Charset;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class GooglePayIssuer extends CordovaPlugin {

    private static final String TAG = "GooglePayIssuer";
    private static final int REQUEST_CODE_PUSH_TOKENIZE = 3;
    private static final int REQUEST_CREATE_WALLET = 4;
    private CordovaInterface cordova;
    private TapAndPayClient tapAndPayClient;
    private String walletId;
    private String hardwareId;
    private CallbackContext callbackContext;

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
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        Log.i(TAG, action);
        Log.i(TAG, args.toString());

        this.callbackContext  = callbackContext;

        if ("getTokenStatus".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject options = args.getJSONObject(0);
                        String tsp = options.getString("tsp");
                        String token = options.getString("tokenReferenceId");

                        int tokenServiceProvider = (tsp.equalsIgnoreCase("VISA")) ? TapAndPay.TOKEN_PROVIDER_VISA : TapAndPay.TOKEN_PROVIDER_MASTERCARD;

                        getTokenStatus(tokenServiceProvider, token, callbackContext);
                    } catch (Exception e) {
                        Log.i(TAG, "ERRO TOKEN STATUS --- " + e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                }
            });

            return true;
        } else if ("getActiveWalletID".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getActiveWalletID(callbackContext);
                }
            });

            return true;
        } else if ("getStableHardwareId".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getStableHardwareId(callbackContext);
                }
            });

            return true;
        } else if ("getEnvironment".equals(action)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getEnvironment(callbackContext);
                }
            });

            return true;
        } else if ("pushProvision".equals(action)) {
            final CordovaPlugin plugin = (CordovaPlugin) this;

            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tsp = args.getString(1);
                        String clientName = args.getString(2);
                        String lastDigits = args.getString(3);
                        JSONObject address = args.getJSONObject(4);

                        String opc = args.getString(0);
                        plugin.cordova.setActivityResultCallback(plugin);

                        pushProvision(opc, tsp, clientName, lastDigits, address, callbackContext);
                    } catch (Exception e) {
                        Log.i(TAG, "ERRO PROVISIONAMENTO --- " + e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                }
            });

            return true;
        } else {
            callbackContext.error("\"" + action + "\" is not a recognized action.");
            return false;
        }
    }

    private void getTokenStatus(int tsp, String tokenReferenceId, CallbackContext callbackContext){
        Log.i(TAG, "Google connected -- getTokenStatus");
        tapAndPayClient
                .getTokenStatus(tsp, tokenReferenceId)
                .addOnCompleteListener(
                        new OnCompleteListener<TokenStatus>() {
                            @Override
                            public void onComplete(@NonNull Task<TokenStatus> task) {
                                Log.i(TAG, "onComplete (getTokenStatus) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    @TapAndPay.TokenState int tokenStateInt = task.getResult().getTokenState();
                                    boolean isSelected = task.getResult().isSelected();
                                    // Next: update payment card UI to reflect token state and selection
                                    Log.i(TAG, "SUCCESS-getTokenStatus");
                                    callbackContext.success(tokenStateInt);
                                } else {
                                    ApiException apiException = (ApiException) task.getException();
                                    if (apiException.getStatusCode() == TAP_AND_PAY_TOKEN_NOT_FOUND) {
                                        // Could not get token status
                                        callbackContext.error("error");
                                    }
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure (getTokenStatus) - " + e.getMessage());
                        callbackContext.error("error");
                    }
                })
                .addOnCanceledListener(
                        new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.i(TAG, "onCanceled (getTokenStatus) - ");
                                callbackContext.error("error");
                            }
                        });
    }

    private void getActiveWalletID(CallbackContext callbackContext) {
        Log.i(TAG, "Google connected -- getActiveWalletID");
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
                                    callbackContext.success(walletId);
                                } else {
                                    Log.i(TAG, "ERROR-getActiveWalletID");
                                    ApiException apiException = (ApiException) task.getException();
                                    Log.i(TAG, apiException.getMessage());
                                    if (apiException.getStatusCode() == TAP_AND_PAY_NO_ACTIVE_WALLET) {
                                        // If no Google Pay wallet is found, create one and then call
                                        // getActiveWalletId() again.
                                        createWallet();
                                        getActiveWalletID(callbackContext);
                                    } else {
                                        //Failed to get active wallet ID
                                        callbackContext.error("error");
                                    }
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

    private void createWallet() {
        Log.i(TAG, "Google connected -- createWallet");
        tapAndPayClient.createWallet(this.cordova.getActivity(), REQUEST_CREATE_WALLET);
    }

    private void getStableHardwareId(CallbackContext callbackContext) {
        Log.i(TAG, "Google connected -- getStableHardwareId");
        tapAndPayClient
                .getStableHardwareId()
                .addOnCompleteListener(
                        new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                Log.i(TAG, "onComplete (getStableHardwareId) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    hardwareId = task.getResult();
                                    callbackContext.success(hardwareId);
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
        Log.i(TAG, "Google connected -- getEnvironment");
        tapAndPayClient
                .getEnvironment()
                .addOnCompleteListener(
                        new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                Log.i(TAG, "onComplete (getEnvironment) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    String env = task.getResult();
                                    callbackContext.success(env);
                                } else {
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

    private void pushProvision(String opc, String tsp, String clientName, String lastDigits, JSONObject address, CallbackContext callbackContext) {
      
        try {
            Log.i(TAG, "pushProvision");

//           int cardNetwork = (tsp.equals("VISA")) ? TapAndPay.CARD_NETWORK_VISA : TapAndPay.CARD_NETWORK_MASTERCARD;
            int tokenServiceProvider = (tsp.equals("VISA")) ? TapAndPay.TOKEN_PROVIDER_VISA : TapAndPay.TOKEN_PROVIDER_MASTERCARD;
 
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
                            .setOpaquePaymentCard(opc.getBytes(Charset.forName("UTF-8")))
                            .setNetwork(cardNetwork)
                            .setTokenServiceProvider(tokenServiceProvider)
                            .setDisplayName(clientName)
                            .setLastDigits(lastDigits)
                            .setUserAddress(userAddress)
                            .build();

            tapAndPayClient.pushTokenize(this.cordova.getActivity(), pushTokenizeRequest, REQUEST_CODE_PUSH_TOKENIZE);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult --- " + resultCode + " --- " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        // Push provisioning
        if (requestCode == REQUEST_CODE_PUSH_TOKENIZE) {
            if (resultCode == cordova.getActivity().RESULT_CANCELED) {
                // The user canceled the request.
                // Send parameters to retrieve in cordova.
                PluginResult resultado = new PluginResult(PluginResult.Status.ERROR, "canceled");
                resultado.setKeepCallback(true);
                callbackContext.sendPluginResult(resultado);
                return;
            } else if (resultCode == cordova.getActivity().RESULT_OK) {
                // The action succeeded.
                String tokenId = data.getStringExtra(TapAndPay.EXTRA_ISSUER_TOKEN_ID);
                // Do something with tokenId.
                PluginResult resultado = new PluginResult(PluginResult.Status.OK, tokenId);
                resultado.setKeepCallback(true);
                callbackContext.sendPluginResult(resultado);
                return;
            }
        }
        // Create Wallet
        else if (requestCode == REQUEST_CREATE_WALLET) {
            if (resultCode == cordova.getActivity().RESULT_CANCELED) {
                // The user canceled the request.
                PluginResult resultado = new PluginResult(PluginResult.Status.ERROR, "canceled");
                resultado.setKeepCallback(true);
                callbackContext.sendPluginResult(resultado);
                return;
            } else if (resultCode == cordova.getActivity().RESULT_OK) {
                // The action succeeded.
                PluginResult resultado = new PluginResult(PluginResult.Status.OK);
                resultado.setKeepCallback(true);
                callbackContext.sendPluginResult(resultado);
                return;
            }
        }
        // ?
        else{
            PluginResult resultado = new PluginResult(PluginResult.Status.OK);
            resultado.setKeepCallback(true);
            callbackContext.sendPluginResult(resultado);
            return;
        }
        // Handle results for other request codes.
        // ...
    }

    private void show(String msg, CallbackContext callbackContext) {
        if (msg == null || msg.length() == 0) {
            callbackContext.error("Empty message!");
        } else {
            Toast.makeText(webView.getContext(), msg, Toast.LENGTH_LONG).show();
            callbackContext.success(msg);
        }
    }
}