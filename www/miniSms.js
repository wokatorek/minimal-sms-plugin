var exec = require('cordova/exec');

var miniSms = {};

miniSms.isSupported = function(successCallback, failureCallback){
  cordova.exec(successCallback, failureCallback, 'miniSms', 'isSupported', []);
}

miniSms.send = function(number, message, successCallback, failureCallback) {
  if(typeof address !== 'string'){
    if(typeof failureCallback === 'function') {
                        failureCallback("require address, phone number as string, or array of string");
                }
                return;
  }
  cordova.exec(successCallback, failureCallback, 'miniSms', 'send', [ number, message ]);
}

miniSms.getLatestReceived = function(number, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'miniSms', 'getLatestReceived', [ number ]);
}

miniSms.startListening = function(isIntercepting, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'miniSms', 'startListening', [ isIntercepting ]);
}

miniSms.stopListening = function( successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'miniSms', 'stopListening', []);
}

module.exports = miniSms;
