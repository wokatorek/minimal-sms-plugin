var exec = require('cordova/exec');

var sms = {};

sms.isSupported = function(successCallback, failureCallback){
  cordova.exec(successCallback, failureCallback, 'sms', 'isSupported', []);
}

sms.send = function(number, message, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'sms', 'send', [ number, message ]);
}

sms.getLatestReceived = function(number, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'sms', 'getLatestReceived', [ number ]);
}

sms.startListening = function(isIntercepting, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'sms', 'startListening', [ isIntercepting ]);
}

sms.stopListening = function( successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'sms', 'stopListening', []);
}

module.exports = sms;
