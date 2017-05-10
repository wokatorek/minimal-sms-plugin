package pl.wokatorek.cordova.plugin.miniSms;

import android.content.BroadcastReceiver;
import org.apache.cordova.CallbackContext;

public class SmsBroadcastReceiver extends BroadcastReceiver {
  private int partsCount;
  private CallbackContext callbackContext;

  SmsBroadcastReceiver(int partsCount, CallbackContext callbackContext) {
    this.partsCount = partsCount;
    this.callbackContext = callbackContext;
  }

  @Override
  public void onReceive(Context context, Intent intent){
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
