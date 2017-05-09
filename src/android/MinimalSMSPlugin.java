package pl.wokatorek.cordova.plugin.sms;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MinimalSMSPlugin extends CordovaPlugin {

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Log.d("MinimalSMSPlugin", "MinimalSMSPlugin.execute");
    if (action.equals("echo")) {
      Log.d("MinimalSMSPlugin", "MinimalSMSPlugin.execute true");
      String message = args.getString(0);
      this.echo(message, callbackContext);
      return true;
    }
    return false;
  }

  private void echo(String message, CallbackContext callbackContext) {
    Log.d("MinimalSMSPlugin", "MinimalSMSPlugin.echo");
    if (message != null && message.length() > 0) {
      Log.d("MinimalSMSPlugin", "MinimalSMSPlugin.echo true");
      callbackContext.success(message);
    } else {
      Log.d("MinimalSMSPlugin", "MinimalSMSPlugin.echo false");
      callbackContext.error("Expected one non-empty string argument.");
    }
  }

}
