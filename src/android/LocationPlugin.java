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
	public static final String TAG_NAME = "LocationPlugin";
	public static final int SERVICE_RUNNING = 1;
	public static final int SERVICE_STOP = 0;
	public LocationPlugin(){
	}
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.v(TAG_NAME,"Init LocationPlugin");
	}
	public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("isServiceRunning")) {
			this.callback = callbackContext;
            cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                		if (!isServiceRunning())
                		{
							Intent serviceIntent =(new Intent(cordova.getActivity().getApplicationContext(), TrackUserLocationService.class));
							cordova.getActivity().getApplicationContext().startService(serviceIntent);
                			callback.success(SERVICE_RUNNING);
                		}
                		else
                		{
                			callback.success(SERVICE_STOP);
                		}
                    }
            });
            return true;
			
		}
		
        return false;
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
			Log.d(TAG_NAME, "isServiceRunning failed", e);
		}
	    return false;
	}
}
