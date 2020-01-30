var exec = cordova.require('cordova/exec');

var GooglePayIssuer = function () {
    console.log('GooglePayIssuer instanced');
};

GooglePayIssuer.prototype.show = function (msg, onSuccess, onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'show', [msg]);
};

/**
 * Get the token status given an tokenReferenceId.
 *
 * @param [ string ] tsp The token service provider. VISA or MASTER
 * @param [ string ] tokenReferenceId The token returned by provisioning.
 * @param [ Function ] onSuccess The callback function to be execute later, in case of success.
 * @param [ Function ] onError Optional scope for the callback function, in case of error.
 *
 * @return [ Int ] tokenStateInt The int value that represent token status.
 */
GooglePayIssuer.prototype.getTokenStatus = function (tsp,tokenReferenceId,onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    var options = {};
    options.tsp = tsp;
    options.tokenReferenceId = tokenReferenceId;

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getTokenStatus', [options]);
};

/**
 * Get the active walletID.
 *
 * @param [ Function ] onSuccess The callback function to be execute later, in case of success.
 * @param [ Function ] onError Optional scope for the callback function, in case of error.
 *
 * @return [ String ] walletID The active walletID string.
 */
GooglePayIssuer.prototype.getActiveWalletID = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getActiveWalletID');
};

/**
 * Get the active hardwareID.
 *
 * @param [ Function ] onSuccess The callback function to be execute later, in case of success.
 * @param [ Function ] onError Optional scope for the callback function, in case of error.
 *
 * @return [ String ] hardwareID The active hardwareID string.
 */
GooglePayIssuer.prototype.getStableHardwareId = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getStableHardwareId');
};

/**
 * Get the current environment Google Pay is configured to use..
 *
 * @param [ Function ] onSuccess The callback function to be execute later, in case of success.
 * @param [ Function ] onError Optional scope for the callback function, in case of error.
 *
 * @return [ String ] environment The active environment string.
 */
GooglePayIssuer.prototype.getEnvironment = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };
  
    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getEnvironment');
};

/**
 * Get the token status given an tokenReferenceId.
 *
 * @param [ string ] opc The encrypted data that represents card and credit card brand informations.
 * @param [ string ] tsp The token service provider. VISA or MASTER
 * @param [ string ] clientName The client name in the card.
 * @param [ string ] lastDigits The last 4 digits of card.
 * @param [ object ] address The object.
 * @param [ Function ] onSuccess The callback function to be execute later, in case of success.
 * @param [ Function ] onError Optional scope for the callback function, in case of error.
 *
 * @return [ String ] tokenReferenceId The tokenID that represents the given card provisioning.
 */
GooglePayIssuer.prototype.pushProvision = function (opc,tsp,clientName,lastDigits,address,onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'pushProvision', [opc,tsp,clientName,lastDigits,address]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = GooglePayIssuer;
}
