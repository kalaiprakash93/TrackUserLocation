import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.provider.Settings;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.apache.cordova.*;

public class LocationPlugin extends CordovaPlugin{
	public LocationPlugin(){
	}
	public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
				Toast.makeText(cordova.getActivity().getApplicationContext(), "first", Toast.LENGTH_SHORT).show();
				if(action.equals("isServiceRunning")){
					Toast.makeText(cordova.getActivity().getApplicationContext(), "second", Toast.LENGTH_SHORT).show();
				if (!isServiceRunning()) {
					Toast.makeText(cordova.getActivity().getApplicationContext(), "third", Toast.LENGTH_SHORT).show();
					Intent serviceIntent = (new Intent(cordova.getActivity().getApplicationContext(), TrackUserLocationService.class));
					cordova.getActivity().getApplicationContext().startService(serviceIntent);
					callbackContext.success("ok");
				} else {
					Toast.makeText(cordova.getActivity().getApplicationContext(), "else", Toast.LENGTH_SHORT).show();
					callbackContext.success("error");
				}
				return true;
			}
	}
	private boolean isServiceRunning()
	{
		try 
		{
			ActivityManager manager = (ActivityManager)this.cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE); 
			for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){ 
				if (TrackUserLocationService.class.getName().equals(service.service.getClassName()))
				{ 
					return true;
				} 
			} 
		} catch (Exception e)
		{		
			Toast.makeText(cordova.getActivity().getApplicationContext(), "catchtoast", Toast.LENGTH_SHORT).show();
		}
	    return false;
	}
}
