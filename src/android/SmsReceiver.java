package pl.wokatorek.cordova.plugin.miniSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

public class SmsReceiver extends BroadcastReceiver {
  private boolean isBroadcastingFurther = true;
  private CallbackContext callbackContext;
  private CordovaWebView cordovaWebView;
  private CordovaInterface cordovaInterface;

  public SmsReceiver() {
    super();
  }

  public SmsReceiver(boolean isBroadcastingFurther, CallbackContext callbackContext, CordovaWebView cordovaWebView, CordovaInterface cordovaInterface) {
    super();
    this.isBroadcastingFurther = isBroadcastingFurther;
    this.callbackContext = callbackContext;
    this.cordovaWebView = cordovaWebView;
    this.cordovaInterface = cordovaInterface;
    Log.i("minimal-sms-plugin","SmsReceiver");
  }

  public void setIsBroadcastingFurther(boolean isBroadcastingFurther){
    this.isBroadcastingFurther = isBroadcastingFurther;
  }

  public void setCallbackContext(CallbackContext callbackContext){
    this.callbackContext = callbackContext;
  }

  public void setCordovaWebView(CordovaWebView cordovaWebView){
    this.cordovaWebView = cordovaWebView;
  }

  public void setCordovaInterface(CordovaInterface cordovaInterface){
    this.cordovaInterface = cordovaInterface;
  }

  public boolean getIsBroadcastingFurther(){
    return this.isBroadcastingFurther;
  }

  public CallbackContext getCallbackContext(){
    return this.callbackContext;
  }

  public CordovaWebView getCordovaWebView(){
    return this.cordovaWebView;
  }

  public CordovaInterface getCordovaInterface(){
    return this.cordovaInterface;
  }

  @Override
  public void onReceive(Context context, Intent intent){
    Log.i("minimal-sms-plugin","onReceive");
    Map<String, JSONObject> messages = RetrieveMessages(intent);
    for (final JSONObject json : messages.values()){
      cordovaInterface.getActivity().runOnUiThread(new Runnable(){
        @Override
        public void run() {
          Log.i("minimal-sms-plugin","cordova.run()");
          String eventUrl = String.format("javascript:cordova.fireDocumentEvent(\"%s\", {\"data\":%s});", "onSMSArrive", json.toString());
          Log.i("minimal-sms-plugin","string formatted");
          try{
             cordovaWebView.loadUrl( eventUrl );
          } catch (Exception e) {
            e.printStackTrace();
            Log.e("minimal-sms-plugin",Log.getStackTraceString(e));
          }
          Log.i("minimal-sms-plugin","loadUrl");
        }
      });
    }
    if(!this.isBroadcastingFurther){
      this.abortBroadcast();
    }
  }

  private static Map<String,JSONObject> RetrieveMessages(Intent intent){
    Map<String, JSONObject> msg = null;
    SmsMessage[] msgs = null;
    Bundle extras = intent.getExtras();
    Log.i("minimal-sms-plugin","gotExtras");
    if (extras != null && extras.containsKey("pdus")) {
      Object[] pdus = (Object[])extras.get("pdus");
      if (pdus != null && pdus.length != 0) {
        Log.i("minimal-sms-plugin","gotPdus");
        int numberOfpdus = pdus.length;
        msg = new HashMap<String, JSONObject>(numberOfpdus);
        msgs = new SmsMessage[numberOfpdus];
        // There can be multiple SMS from multiple senders, there can be a maximum of numberOfpdus different senders
        // However, send long SMS of same sender in one message
        for (int i = 0; i < numberOfpdus; i++) {
          msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
          String originatinAddress = msgs[i].getOriginatingAddress();
          // Check if index with number exists
          if (!msg.containsKey(originatinAddress)) {
            // Index with number doesn't exist
            // Save JSON into associative array with sender number as index
            final JSONObject json = new JSONObject();
            try {
              json.put("originating_address", msgs[i].getOriginatingAddress());
              json.put("message_body", msgs[i].getMessageBody());
              json.put("timestamp_millis", msgs[i].getTimestampMillis());
              json.put("status", msgs[i].getStatus());
              json.put("service_center_address", msgs[i].getServiceCenterAddress());
              json.put("index_on_icc", msgs[i].getIndexOnIcc());
            } catch ( Exception e ) {
                e.printStackTrace();
                Log.e("minimal-sms-plugin",Log.getStackTraceString(e));
            }
            msg.put(originatinAddress, json);
          } else {
            // Number has been there, add content but consider that
            // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
            // so just add the part of the current PDU
            JSONObject previousparts = null;
            try {
              previousparts = msg.get(originatinAddress);
              String msgString = previousparts.get("message_body") + msgs[i].getMessageBody();
              previousparts.put("message_body", msgString);
              msg.put(originatinAddress, previousparts);
            } catch ( Exception e ) {
                e.printStackTrace();
                Log.e("minimal-sms-plugin",Log.getStackTraceString(e));
            }
          }
        }
      }
    }
    return msg;
  }
}
