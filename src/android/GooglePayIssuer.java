package com.raphaelgodoi;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;
import android.os.Bundle;

import android.content.Context;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import static com.google.android.gms.tapandpay.TapAndPay.TAP_AND_PAY_API;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_ATTESTATION_ERROR;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_INVALID_TOKEN_STATE;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_NO_ACTIVE_WALLET;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_TOKEN_NOT_FOUND;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_UNAVAILABLE;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.tapandpay.TapAndPay;

public class GooglePayIssuer extends CordovaPlugin {

    private static final String TAG = "cordova-plugin-googlepay-issuer";
    private CordovaInterface cordova;
    private GoogleApiClient mClient;

    private TapAndPay tapAndPay = TapAndPay.TapAndPay;
    private String walletId;
    private String hardwareId;

    public GooglePayIssuer() {
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
        this.cordova = cordova;
        Log.i(TAG, "INICIALIZOU");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if("getWalletID".equals(action)){
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getWalletID(callbackContext);
                }
            });
        }

        return false;
    }

    private void getWalletID(CallbackContext callbackContext){
        
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this.cordova.getActivity());
        builder.addApi(TAP_AND_PAY_API);

        builder.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(TAG, "Google connected");

                tapAndPay.getActiveWalletId(mClient)
                    .setResultCallback(new ResultCallback<TapAndPay.GetActiveWalletIdResult>() {
                        @Override
                        public void onResult(TapAndPay.GetActiveWalletIdResult result) {
                            if (result.getStatus().isSuccess()) {
                                callbackContext.success("success");
                                String walletID = result.getActiveWalletId();
                                Log.i(TAG, "WALLET ID ");
                                Log.i(TAG, walletID);
                                // Next: look up token ids for the active wallet
                                // This typically involves network calls to a server with knowledge
                                // of wallets and tokens.
                            } else {
                                int code = result.getStatus().getStatusCode();
                                Log.e(TAG, "###### ERRO AQUI ######");
                                Log.e(TAG, String.valueOf(code) );
                                Log.e(TAG, String.valueOf(TAP_AND_PAY_UNAVAILABLE) );

                                callbackContext.error("error");
                            }
                        }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.e(TAG, "Google suspended");
            }
        });

        builder.addOnConnectionFailedListener(
                new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Connection to Fit failed, cause: " + result.getErrorMessage());
                    }
                }
        );

        mClient = builder.build();
        mClient.connect();

        
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