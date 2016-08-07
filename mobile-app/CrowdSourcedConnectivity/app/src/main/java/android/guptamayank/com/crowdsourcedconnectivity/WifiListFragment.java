package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.guptamayank.com.crowdsourcedconnectivity.model.WapData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayank on 11/2/15.
 */
public class WifiListFragment extends Fragment {
    private RecyclerView wifiListRecycler;
    private RecyclerView.LayoutManager wifiLayoutManager;
    private static WifiListAdapter wifiAdapter;
    static WifiManager wifiManager = null;
    WifiReceiverList wifiReceiverList;
    private Context context;
    private static List<ScanResult> scanResults;
    private static TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/

        View v = inflater.inflate(R.layout.fragment_wifi_list, null);
        wifiListRecycler = (RecyclerView) v.findViewById(R.id.my_recycler_view);
            wifiListRecycler.setHasFixedSize(false);
//            wifiLayoutManager = ;
            wifiListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            scanResults = new ArrayList<ScanResult>();
            wifiAdapter = new WifiListAdapter(scanResults, getActivity());
            wifiListRecycler.setAdapter(wifiAdapter);

        tv = (TextView) v.findViewById(R.id.total_ap);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        wifiReceiverList = new WifiReceiverList();
        context.registerReceiver(wifiReceiverList, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


    public static class WifiReceiverList extends BroadcastReceiver {
        List<ScanResult> wifiList;
        StringBuilder sb = new StringBuilder();
        // Number of WiFi connections changed
        public void onReceive(Context c, Intent intent) {
            Log.i("WifiList", "Recieved scan");
            if (wifiManager == null) {
                return;
            }
            wifiList =  wifiManager.getScanResults();
            tv.setText("Number of Access Points: " + wifiList.size());
            scanResults.clear();
            wifiAdapter.setmDataset(wifiList);
        }

    }
}