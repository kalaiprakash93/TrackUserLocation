import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.provider.Settings;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.apache.cordova.*;

public class LocationPlugin extends CordovaPlugin{
	private CallbackContext callback;
	public static final int SERVICE_RUNNING = 1;
	public static final int SERVICE_STOP = 0;
	public LocationPlugin(){
	}
	public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
                		if (!isServiceRunning())
                		{
							Toast.makeText(cordova.getActivity().getApplicationContext(), "3rd", Toast.LENGTH_SHORT).show();
							/*Intent serviceIntent =(new Intent(cordova.getActivity().getApplicationContext(), TrackUserLocationService.class));
							cordova.getActivity().getApplicationContext().startService(serviceIntent);*/
                			callback.success(SERVICE_RUNNING);
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
			
		}
	    return false;
	}
}
