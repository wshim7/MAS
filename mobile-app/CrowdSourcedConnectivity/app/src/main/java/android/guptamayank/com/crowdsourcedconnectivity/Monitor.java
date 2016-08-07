package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.guptamayank.com.crowdsourcedconnectivity.model.DataPackage;
import android.guptamayank.com.crowdsourcedconnectivity.model.LocationData;
import android.guptamayank.com.crowdsourcedconnectivity.model.UserData;
import android.guptamayank.com.crowdsourcedconnectivity.model.WapData;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
/**
 * Created by Mayank on 10/31/15.
 */
public class Monitor extends IntentService {
    private static final String TAG = Monitor.class.getName();
    private static final int INTERVAL = 7;
    private static final String URL = "http://178.62.220.153:3050/add";

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String SEND_DATA_PACKAGE =
            "android.guptamayank.com.crowdsourcedconnectivity.action.SEND_DATA_PACKAGE";

    private static List<String> backlog = new ArrayList<>();

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static PendingIntent startDataPackageService(Context context) {
        Intent intent = new Intent(context, Monitor.class);
        intent.setAction(SEND_DATA_PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long frequency = TimeUnit.SECONDS.toMillis(INTERVAL);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, INTERVAL);
        long startTime = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, frequency, pendingIntent);

        return pendingIntent;
    }

    public static void stopDataPackageService(Context context, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public Monitor() {
        super("Monitor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("DataService", "ICame here");
        if (intent != null) {
            final String action = intent.getAction();
            if (SEND_DATA_PACKAGE.equals(action)) {
                handleActionDataPackageService();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDataPackageService() {
        Log.i(TAG, "handle action data package service");
        int signalLevel;
        String activeBssid = "";
        String activeSsid = "";
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // get active connection info
        if (wifi.isConnected()) {
            activeBssid = wifiManager.getConnectionInfo().getBSSID();
            activeSsid = wifiManager.getConnectionInfo().getSSID();
        }

        activeBssid = activeBssid.replace("\"", "").replace("'", "");
        activeSsid = activeSsid.replace("\"", "").replace("'", "");

        String locationComment = DataManager.getInstance().getLocationComment();
        if (locationComment == null) {
            locationComment = "";
        }
        Location location = DataManager.getInstance().getLocation();

        // get user data
        HashMap<String, String> userDetails = DataManager.getInstance().getUserDetails();
        UserData userData = new UserData();
        userData.setPhoneModel(userDetails.get("phone_model"));
        userData.setAndroidVersion(userDetails.get("android_version"));
        userData.setDeviceId(userDetails.get("imei"));
        userData.setDevice(userDetails.get("device"));

        // get WIFI data
        List<ScanResult> currentWifiData = DataManager.getInstance().getWifiList();
        // setup data package
        List<DataPackage> dataPackageList = new ArrayList<>();
        if (currentWifiData == null) {
            return;
        }
        for (ScanResult scanResult : currentWifiData) {
            DataPackage dataPackage = new DataPackage();
            LocationData locationData = new LocationData();

            dataPackage.setUserData(userData);

            // TODO: Fix this ugly hack, locationData somehow remains static if not within this loop
            locationData.setTimestamp(System.currentTimeMillis());
            locationData.setGpsLat(location != null ? location.getLatitude() : 0);
            locationData.setGpsLong(location != null ? location.getLongitude(): 0);
            locationData.setLocationComment(locationComment);
            if (location != null && location.hasSpeed()) {
                locationData.setLocationSpeed(location.getSpeed());
            } else {
                // No speed, or zero speed
                locationData.setLocationSpeed((float) 0.0);
            }
            locationData.setDbLevel(scanResult.level);
            signalLevel = wifiManager.calculateSignalLevel(scanResult.level, 10);
            locationData.setSignalLevel(signalLevel);
            dataPackage.setLocationData(locationData);

            // wifi scan result
            WapData wapData = new WapData();
            wapData.setSsid(scanResult.SSID);
            wapData.setBssid(scanResult.BSSID);
            wapData.setCapabilities(scanResult.capabilities);
            wapData.setFrequency(scanResult.frequency);
            if (scanResult.SSID.replace("\"", "").replace("'", "").equals(activeSsid) &&
                    scanResult.BSSID.replace("\"", "").replace("'", "").equals(activeBssid)) {
                wapData.setIsActiveConnection(true);
            } else {
                wapData.setIsActiveConnection(false);
            }

            dataPackage.setWapData(wapData);

            // finally, add datapackage to list
            dataPackageList.add(dataPackage);
            dataPackage = null;
            locationData = null;
        }

        // convert to json object
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        String data = gson.toJson(dataPackageList);
//        Log.i(TAG, "built data package: " + data);

        // send to backend
        boolean success = sendData(data, false);

        // attempt to resend other information
        if (!success) {
            Log.i(TAG, "send failed, adding to backlog");
            if (backlog.size() < 50)
                backlog.add(data);
        } else {
            for (int items = 0; items < 5 && !backlog.isEmpty(); ++items) {
                Log.i(TAG, "trying to clean backlog");
                String temp = backlog.remove(0);
                boolean tSuccess = sendData(temp, true);
                if (!tSuccess) {
                    Log.i(TAG, "backlog clean aborting");
                    if (backlog.size() < 50)
                        backlog.add(temp);
                    break;
                }
            }
        }
    }

    private static boolean sendData(String data, boolean backlog) {
        boolean success = false;

        try {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 1000);
            HttpConnectionParams.setSoTimeout(params, 1000);
            DefaultHttpClient client = new DefaultHttpClient(params);
            HttpPost post = new HttpPost(backlog ? URL + "/offline" : URL);
            StringEntity entity = null;
            entity = new StringEntity(data);
            post.setEntity(entity);
            post.setHeader("Content-Type", "application/json");

            Log.i(TAG, "posting data...");
            HttpResponse response = client.execute(post);
            Log.i(TAG, "response = " + response.getStatusLine().getReasonPhrase());
            Log.i(TAG, "response status = " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200)
                success = true;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "failed to convert data to a string", e);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "failed to execute POST", e);
        } catch (IOException e) {
            Log.e(TAG, "unknown error", e);
        } catch(Throwable t) {
            Log.e(TAG, "fatal error", t);
        }

        return success;
    }
}
