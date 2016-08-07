package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.guptamayank.com.crowdsourcedconnectivity.model.Devices;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mayank on 11/1/15.
 */


public class WifiScan extends IntentService {
    static WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    private static final int INTERVAL = 3;
    private PendingIntent monitorService = null;

    // Get phone details
    String phoneModel = android.os.Build.MODEL.toString();
    String androidVersion = android.os.Build.VERSION.RELEASE.toString();
    String device = Devices.getDeviceName().toString();

    private static final String START_WIFI_SCAN =
            "android.guptamayank.com.indoorlocation.action.START_WIFI_SCAN";

    public static PendingIntent startWifiScanService(Context context) {
        Intent intent = new Intent(context, WifiScan.class);
        intent.setAction(START_WIFI_SCAN);
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

    public static void stopWifiScanService(Context context, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
//        WifiScan.stopMonitorService();
    }


    public WifiScan() {
        super("WifiScan");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (START_WIFI_SCAN.equals(action)) {
                starWifiScan();
                startMonitorService();
            }
        }
    }

    private void startMonitorService() {
        if (monitorService == null) {
//            monitorService = Monitor.startDataPackageService(this);
        }
    }

    private void stopMonitorService() {
        if (monitorService != null) {
            Monitor.stopDataPackageService(this, monitorService);
        }
    }

    private void starWifiScan() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId().toString();
        DataManager.getInstance().updatePhoneDetails(phoneModel, androidVersion, device,
                deviceId);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled() == false) {
            // If wifi disabled then enable it
            Log.i("WifiService", "Wifi Was disabled");
            wifiManager.setWifiEnabled(true);
        }
        wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    public static class WifiReceiver extends BroadcastReceiver {
        List<ScanResult> wifiList;
        StringBuilder sb = new StringBuilder();
        // Number of WiFi connections changed
        public void onReceive(Context c, Intent intent) {
//        sb = new StringBuilder();
            if (wifiManager == null) {
                return;
            }
            wifiList =  wifiManager.getScanResults();
            Log.i("WifiService", "The wifi list has " + wifiList.size());
            DataManager.getInstance().updateWifiList(wifiList);
    //        // Retrieve GPS location
                // gps_lat, gps_lng (Degree string)
                // Phone Details are global
            Log.i("WifiService", "Recieved scan");
            sb.append("\n        Number Of WiFi connections :" + wifiList.size() + "\n\n");
            for(int i = 0; i < wifiList.size(); i++) {
                sb.append(new Integer(i+1).toString() + ". ");
                ScanResult scanresult = wifiList.get(i);
                int level = wifiManager.calculateSignalLevel(scanresult.level, 10);
                sb.append((wifiList.get(i)).toString() + ",\nSignal strength: " + level + "/10");
    //                sb.append("," + gps_lat);
                sb.append("\n\n");
            }
//            textView.setText(sb);
        }


    }
}

