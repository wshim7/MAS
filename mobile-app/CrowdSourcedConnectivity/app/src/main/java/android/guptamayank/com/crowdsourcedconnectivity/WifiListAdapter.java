package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.Fragment;
import android.content.Context;
import android.guptamayank.com.crowdsourcedconnectivity.model.WapData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {
    private List<ScanResult> mDataset;
    WifiManager wifiManager;
    private Context mContext;
    private View.OnClickListener itemOnClick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtMiddle;
        public TextView txtFooter;
        public ImageView icon;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtMiddle = (TextView) v.findViewById(R.id.secondLine);
            txtFooter = (TextView) v.findViewById(R.id.thirdline);
            icon = (ImageView) v.findViewById(R.id.icon);
        }
    }

    public void add(int position, ScanResult item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void setmDataset(List<ScanResult> dataset) {
        mDataset = dataset;
        notifyDataSetChanged();
    }

    public void removeAll() {
        int size = mDataset.size();
        mDataset.clear();
//        if (size > 0) {
//            for (int i = 0; i < size; i++) {
//                mDataset.remove(0);
//            }

            this.notifyItemRangeRemoved(0, size);
//        }
    }

//    public void removeAll() {
//        mDataset.remove
//    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WifiListAdapter(List<ScanResult> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WifiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wifi_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        final String name = mDataset.get(position);
        ScanResult scanResult = mDataset.get(position);
        itemOnClick =  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                switchFragment(mDataset.get(position));
            }
        };
        holder.txtHeader.setText(scanResult.BSSID);
        holder.txtHeader.setOnClickListener(itemOnClick);
        holder.txtMiddle.setText(scanResult.SSID);
        holder.txtMiddle.setOnClickListener(itemOnClick);
        int signalLevel = wifiManager.calculateSignalLevel(scanResult.level, 10);
        holder.txtFooter.setText("Strength: " + signalLevel + "/10");
        holder.txtFooter.setOnClickListener(itemOnClick);
        if (signalLevel < 9 && signalLevel >= 6) {
            holder.icon.setImageResource(R.drawable.wifi_mid);
        } else if(signalLevel < 6)  {
            holder.icon.setImageResource(R.drawable.wifi_low);
        }
        holder.icon.setOnClickListener(itemOnClick);

    }

    private void switchFragment(ScanResult item) {
        AccessPointFragment apFragment = new AccessPointFragment();
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("item_selected_key", item);
        apFragment.setArguments(mBundle);
        switchContent(R.id.container, apFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        MainActivity mainActivity = (MainActivity) mContext;
        Fragment frag = fragment;
        mainActivity.switchContent(id, frag);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
