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

GooglePayIssuer.prototype.getActiveWalletID = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getActiveWalletID');
};

GooglePayIssuer.prototype.getStableHardwareId = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getStableHardwareId');
};

GooglePayIssuer.prototype.getEnvironment = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getEnvironment');
};

GooglePayIssuer.prototype.pushProvision = function (opc,cardFirstNumber,clientName,lastDigits,address,onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    var options = {};
    options.opc = opc;
    options.cardFirstNumber = cardFirstNumber;
    options.clientName = clientName;
    options.lastDigits = lastDigits;
    options.address = address;

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'pushProvision', [options]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = GooglePayIssuer;
}
