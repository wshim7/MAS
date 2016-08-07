package android.guptamayank.com.crowdsourcedconnectivity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Mayank on 11/2/15.
 */

public class GlobalAlarmReciever extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationService.class));
    }
}