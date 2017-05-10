package pl.wokatorek.cordova.plugin.miniSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.telephony.SmsManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

public class SmsBroadcastReceiver extends BroadcastReceiver {
  private int partsCount;
  private CallbackContext callbackContext;
  private CordovaInterface cordova;

  SmsBroadcastReceiver(int partsCount, CallbackContext callbackContext, CordovaInterface cordova) {
    this.partsCount = partsCount;
    this.callbackContext = callbackContext;
    this.cordova = cordova;
  }

  @Override
  public void onReceive(Context context, Intent intent){
    boolean anyError = false;
    switch (getResultCode()) {
      case SmsManager.STATUS_ON_ICC_SENT:
      case Activity.RESULT_OK:
        break;
      case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			case SmsManager.RESULT_ERROR_NO_SERVICE:
			case SmsManager.RESULT_ERROR_NULL_PDU:
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				anyError = true;
				break;
    }
    partsCount--;
		if (partsCount == 0) {
			if (anyError) {
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
			} else {
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
			}
			cordova.getActivity().unregisterReceiver(this);
		}
  }
}
