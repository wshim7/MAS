package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayank on 11/2/15.
 */
public class AccessPointFragment extends Fragment {
    private Context context;
    private static List<ScanResult> scanResults;
    private TextView tv;
    private ImageView iv;
    private WifiManager wifiManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.fragment_access_point, null);
        Bundle bundle = getArguments();
        ScanResult scanResult = bundle.getParcelable("item_selected_key");
        tv = (TextView) v.findViewById(R.id.ssid);
        tv.setText(scanResult.SSID);
        tv = (TextView) v.findViewById(R.id.bssid);
        tv.setText(scanResult.BSSID);

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        int signalLevel = wifiManager.calculateSignalLevel(scanResult.level, 10);
        tv = (TextView) v.findViewById(R.id.signal);
        tv.setText("Strength: " + signalLevel + "/10");

        tv = (TextView) v.findViewById(R.id.dblevel);
        tv.setText("dbLevel: " + scanResult.level);

        tv = (TextView) v.findViewById(R.id.frequency);
        tv.setText("Frequency: " + scanResult.frequency + " Mhz");

        tv = (TextView) v.findViewById(R.id.capabilities);
        tv.setText("Capabilities: " + scanResult.capabilities);

        iv = (ImageView) v.findViewById(R.id.icon);
        if (signalLevel < 9 && signalLevel >= 6) {
            iv.setImageResource(R.drawable.wifi_mid);
        } else if(signalLevel < 6)  {
            iv.setImageResource(R.drawable.wifi_low);
        }

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }
}