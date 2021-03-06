
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android.content.Intent;

public class TrackUserLocationService extends Service {
	private LocationManager locationManager;
    private LocationListener mlocListener;
	 public TrackUserLocationService() {

    }
     @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "locationService Started", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener(locationManager);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 30000, 0, mlocListener);
        return START_STICKY;
    }

    private class MyLocationListener implements LocationListener {
        public MyLocationListener(LocationManager locationManager) {
        }

        @Override
        public void onLocationChanged(Location location) {
            new WriteGpsData(Double.toString(location.getLatitude()), Double.toString(location.getLongitude())).execute(" ");
            new PostGpsData(Double.toString(location.getLatitude()), Double.toString(location.getLongitude())).execute(" ");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private class PostGpsData extends AsyncTask<String, Void, String> {

        private String objLon;
        private String objLat;

        public PostGpsData(String lat, String lon) {
            this.objLon = lon;
            this.objLat = lat;
        }

        @Override
        protected String doInBackground(String... urls) {
            File baseDirectory = Environment.getExternalStorageDirectory();
            BufferedReader fileReader;
            StringBuilder user_profile;
            String ClientID = "";
            String CountryCode = "";
            String DeviceID = "";
            try {
                user_profile = new StringBuilder();
                fileReader = new BufferedReader(new FileReader(new File(baseDirectory,"mservice/user.txt")));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    user_profile.append(line);
                }
                fileReader.close();
                if(user_profile.toString() != "") {
                    JSONArray args = new JSONArray("["+user_profile.toString()+"]");
                    JSONObject arg_object = args.getJSONObject(0);

                    ClientID = arg_object.getString("client_id");
                    CountryCode = arg_object.getString("country_code");
                    DeviceID = arg_object.getString("device_id");

                    fileReader = new BufferedReader(new FileReader(new File(baseDirectory,"mservice/client_functional_access_package" + "/" + ClientID + "/" + CountryCode + "/client_functional_access.xml" )));
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(new InputSource(fileReader));
                    doc.getDocumentElement().normalize();

                    String Protocol = doc.getElementsByTagName("protocol_type").item(0).getTextContent();
                    String Domain = doc.getElementsByTagName("domain_name").item(0).getTextContent();
                    String Port = doc.getElementsByTagName("port_no").item(0).getTextContent();
                    String url = Protocol + "//" + Domain + ":" + Port + "/common/components/GeoLocation/update_device_location.aspx";

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    StringEntity reqloc = new StringEntity("<location_xml><client_id>" + ClientID + "</client_id>" + "<country_code>" + CountryCode + "</country_code>" + "<device_id>" + DeviceID + "</device_id>" + "<latitude>" + this.objLat + "</latitude>" + "<longitude>" + this.objLon + "</longitude>" + "</location_xml>",HTTP.UTF_8);
                    reqloc.setContentType("text/xml");
                    httppost.setEntity(reqloc);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }
    }

    private class WriteGpsData extends AsyncTask<String, Void, String> {

        private String objLon;
        private String objLat;

        public WriteGpsData(String lat, String lon) {
            this.objLon = lon;
            this.objLat = lat;
        }

        @Override
        protected String doInBackground(String... urls) {
            File baseDirectory = Environment.getExternalStorageDirectory();
            File dir = new File (baseDirectory.getAbsolutePath() + "/mservice");
            if(dir.exists()) {
                try {
                    FileWriter out = new FileWriter(new File(baseDirectory, "mservice/LastKnownGpsLocation.txt"));
                    out.write("{\"lat\":"+ "\""+ this.objLat +"\"" +",\"lon\":" + "\""+ this.objLon  +"\",\"timeStamp\":\"" + DateFormat.getDateTimeInstance().format(new Date()) +"\"}");
                    out.close();
                } catch (IOException e) {

                }
            }
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }
    }
}