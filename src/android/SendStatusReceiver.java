package pl.wokatorek.cordova.plugin.miniSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

public class SendStatusReceiver extends BroadcastReceiver {
  private int partsCount;
  private CallbackContext callbackContext;
  private CordovaInterface cordovaInterface;

  public SendStatusReceiver(int partsCount, CallbackContext callbackContext, CordovaInterface cordovaInterface) {
    super();
    this.partsCount = partsCount;
    this.callbackContext = callbackContext;
    this.cordovaInterface = cordovaInterface;
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
			cordovaInterface.getActivity().unregisterReceiver(this);
		}
  }
}
