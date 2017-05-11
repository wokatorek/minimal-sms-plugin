package pl.wokatorek.cordova.plugin.miniSms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
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

  public SmsReceiver(boolean isBroadcastingFurther, CallbackContext callbackContext, CordovaWebView cordovaWebView, CordovaInterface cordovaInterface) {
    super();
    this.isBroadcastingFurther = isBroadcastingFurther;
    this.callbackContext = callbackContext;
    this.cordovaWebView = cordovaWebView;
    this.cordovaInterface = cordovaInterface;
  }

  @Override
  public void onReceive(Context context, Intent intent){
    Bundle extras = intent.getExtras();
    if (extras != null) {
      Object[] pdus;
      if ((pdus = (Object[])extras.get("pdus")).length != 0) {
        for (int i = 0; i < pdus.length; ++i) {
          SmsMessage sms = SmsMessage.createFromPdu((byte[])((byte[])pdus[i]));
          JSONObject json = new JSONObject();
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
          }
          cordovaInterface.getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {
            	String eventUrl = String.format("javascript:cordova.fireDocumentEvent(\"%s\", {\"data\":%s});", "onSMSArrive", json.toString());
            	cordovaWebView.loadUrl( eventUrl );
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
