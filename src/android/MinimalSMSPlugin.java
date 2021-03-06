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
  private SmsReceiver smsReceiver = null;

  public JSONArray cur2Json(Cursor cursor) {
    JSONArray resultSet = new JSONArray();
    cursor.moveToFirst();
    while (cursor.isAfterLast() == false) {
        int totalColumn = cursor.getColumnCount();
        JSONObject rowObject = new JSONObject();
        for (int i = 0; i < totalColumn; i++) {
            if (cursor.getColumnName(i) != null) {
                try {
                    rowObject.put(cursor.getColumnName(i),cursor.getString(i));
                } catch (Exception e) {
                    Log.d("minimal-sms-plugin", e.getMessage());
                }
            }
        }
        resultSet.put(rowObject);
        cursor.moveToNext();
    }
    cursor.close();
    return resultSet;
  }

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

  private PluginResult getLatestReceivedAction(int number, CallbackContext callbackContext){
    Activity context = this.cordova.getActivity();
    Uri uri = Uri.parse("content://sms/inbox");
    String sortOrderExpression = "_id desc";
    if(number > 0) {
      sortOrderExpression.concat(" LIMIT "+number);
    }
    Cursor cursor = context.getContentResolver().query(uri, (String[])null, "", (String[])null, sortOrderExpression);
    String[] columnNames = cursor.getColumnNames();
    StringBuilder builder = new StringBuilder();
    for(String s : columnNames) {
        builder.append(s);
        builder.append(", ");
    }
    String str = builder.toString();
    callbackContext.success(cur2Json(cursor));
    cursor.close();
    return null;
  }

  private boolean isSupported() {
    return this.cordova.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
  }

  public void onDestroy() {
    this.stopListeningAction(null);
  }

  private void send(String number, String message, CallbackContext callbackContext){
    SmsManager smsManager = SmsManager.getDefault();
		final ArrayList<String> parts = smsManager.divideMessage(message);
    final SendStatusReceiver broadcastReceiver = new SendStatusReceiver(parts.size(), callbackContext, cordova);
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

  private PluginResult startListeningAction(boolean isIntercepting, CallbackContext callbackContext){
    Log.i("minimal-sms-plugin","startListeningAction");
    this.smsReceiver = new SmsReceiver(isIntercepting,callbackContext,webView,cordova);
    IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    intentFilter.setPriority(1000);
    this.cordova.getActivity().registerReceiver(this.smsReceiver, intentFilter);
    callbackContext.success();
    return null;
  }

  private PluginResult stopListeningAction(CallbackContext callbackContext){
    Log.i("minimal-sms-plugin","stopListeningAction");
    try {
      this.cordova.getActivity().unregisterReceiver(this.smsReceiver);
    } catch (IllegalArgumentException e) {
      Log.e("minimal-sms-plugin","smsReceiver service not registered!");
      e.printStackTrace();
    } catch (Exception e ){
      Log.e("minimal-sms-plugin","Unknown error during stopListeningAction()");
      e.printStackTrace();
    }
    if(callbackContext != null){
      callbackContext.success();
    }
    return null;
  }
}
