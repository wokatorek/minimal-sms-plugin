var exec = require('cordova/exec');

var sms = {};

sms.echo = function(msg, successCallback, failureCallback) {
  console.log('sms.echo def');
  cordova.exec(successCallback, failureCallback, 'sms', 'echo', [ msg ]);
}

module.exports = sms;
