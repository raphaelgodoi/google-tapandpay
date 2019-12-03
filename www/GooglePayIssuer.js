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

GooglePayIssuer.prototype.getWalletID = function (onSuccess,onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'GooglePayIssuer', 'getWalletID');
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = GooglePayIssuer;
}