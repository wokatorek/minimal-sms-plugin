var exec = require('cordova/exec');

var sms = {};

sms.echo = function(msg, successCallback, failureCallback) {
  cordova.exec(successCallback, failureCallback, 'sms', 'echo', [ msg ]);
}
