# minimal-sms-plugin
Minimalist Cordova SMS plugin for Android.
Does not check for permissions.
Does not need configuration.

sms.isSupported(successCallback, failureCallback)
sms.send(number, message, successCallback, failureCallback)
sms.get(number, successCallback, failureCallback) - number=-1 => get all received SMS
sms.startListening(isIntercepting, successCallback, failureCallback) - isIntercepting: is plugin intercepting from user's messages application
sms.stopListening(successCallback, failureCallback)

fires 'onSMSReception' event

Cordova 6.4, Android SDK min 14 
