package pl.wokatorek.cordova.plugin.miniSms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import java.util.ArrayList;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MinimalSMSPlugin extends CordovaPlugin {

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    PluginResult result = null;
    if (action.equals("isSupported")){
      result = new PluginResult(PluginResult.Status.OK, this.isSupported());
    } else if (action.equals("send")){
      String number = args.optString(0);
      String message = args.optString(1);
      result = this.sendAction(number, message, callbackContext);
    } else if (action.equals("getLatestReceived")){
      int number = args.optInt(0);
      result = this.getLatestReceivedAction(number, callbackContext);
    } else if (action.equals("startListening")){
      boolean isIntercepting = args.optBoolean(0);
      result = this.startListeningAction(isIntercepting, callbackContext);
    } else if (action.equals("stopListening")){
      result = this.stopListeningAction(callbackContext);
    } else {
      Log.e("minimal-sms-plugin", String.format("Invalid action: %s!", action));
      result = new PluginResult(PluginResult.Status.INVALID_ACTION);
    }
    if(result != null){
      callbackContext.sendPluginResult(result);
    }
    return true;
  }

  private PluginResult sendAction(final String number, final String message, final CallbackContext callbackContext){
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run(){
        if(!isSupported()){
          callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SMS is not supported"));
          return;
        }
        send(number,message,callbackContext);
      }
    });
    return null;
  }

  private boolean isSupported() {
    return this.cordova.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
  }

  private void send(String number, String message, CallbackContext callbackContext){
    SmsManager smsManager = SmsManager.getDefault();
		final ArrayList<String> parts = smsManager.divideMessage(message);
    final SmsBroadcastReceiver broadcastReceiver = new SmsBroadcastReceiver(parts.size(), callbackContext, cordova);
    String smsActionRandom = "SMS_SENT"+java.util.UUID.randomUUID().toString();
		this.cordova.getActivity().registerReceiver(broadcastReceiver, new IntentFilter(smsActionRandom));
    PendingIntent sentIntent = PendingIntent.getBroadcast(this.cordova.getActivity(), 0, new Intent(smsActionRandom), 0);
    if (parts.size() > 1) {
			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			for (int i = 0; i < parts.size(); i++) {
				sentIntents.add(sentIntent);
			}
			smsManager.sendMultipartTextMessage(number, null, parts, sentIntents, null);
		}
		else {
			smsManager.sendTextMessage(number, null, message, sentIntent, null);
		}
  }

  private PluginResult getLatestReceivedAction(int number, CallbackContext callbackContext){
    Activity context = this.cordova.getActivity();
    Uri uri = Uri.parse("content://sms/inbox");
    Cursor cursor = context.getContentResolver().query(uri, (String[])null, "", (String[])null, "LIMIT 100");
    cur.close();
    String[] columnNames = cursor.getColumnNames();
    StringBuilder builder = new StringBuilder();
    for(String s : columnNames) {
        builder.append(s);
    }
    String str = builder.toString();
    callbackContext.success(str);
    return null;
  }

  private PluginResult startListeningAction(boolean isIntercepting, CallbackContext callbackContext){
    throw new java.lang.UnsupportedOperationException("Not supported yet.");
  }

  private PluginResult stopListeningAction(CallbackContext callbackContext){
    throw new java.lang.UnsupportedOperationException("Not supported yet.");
  }
}
