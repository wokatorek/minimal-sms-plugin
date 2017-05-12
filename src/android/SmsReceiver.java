package pl.wokatorek.cordova.plugin.miniSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
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
    Bundle extras = intent.getExtras();
    Log.i("minimal-sms-plugin","gotExtras");
    if (extras != null) {
      Object[] pdus;
      if ((pdus = (Object[])extras.get("pdus")).length != 0) {
      Log.i("minimal-sms-plugin","gotPdus");
        for (int i = 0; i < pdus.length; i++) {
          SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[i]);
          final JSONObject json = new JSONObject();
          try {
          	json.put( "id", sms.getOriginatingAddress() );
          	json.put( "address", sms.getOriginatingAddress() );
          	json.put( "body", sms.getMessageBody() );
          	json.put( "date_sent", sms.getTimestampMillis() );
          	json.put( "date", System.currentTimeMillis() );
          	json.put( "status", sms.getStatus() );
          	json.put( "service_center", sms.getServiceCenterAddress());

          } catch ( Exception e ) {
              e.printStackTrace();
              Log.e("minimal-sms-plugin",Log.getStackTraceString(e));
          }
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
      }
    }
    if(!this.isBroadcastingFurther){
      this.abortBroadcast();
    }
  }
}
