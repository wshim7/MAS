package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


/**
 * Created by Mayank on 11/3/15.
 */
public class SignalStrengthFragment extends Fragment implements LocationListener {

    MapView mapView;
    GoogleMap map;
    private Context mContext;
    private LocationManager locationManager;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signal_strength, container, false);
        // Gets the MapView from the XML layout and creates it

        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
        {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                if(mapView != null)
                {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.setMyLocationEnabled(true);
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(33.77, 84.39), 10);
//                    map.animateCamera(cameraUpdate);

                    // Getting LocationManager object from System Service LOCATION_SERVICE
                    locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

                    // Creating a criteria object to retrieve provider
                    Criteria criteria = new Criteria();

                    // Getting the name of the best provider
                    String provider = locationManager.getBestProvider(criteria, true);

                    // Getting Current Location
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location == null) {
                        location = getLastKnownLocation();
                    }
                    if(location != null){
                        onLocationChanged(location);
                    }
                    locationManager.requestLocationUpdates(provider, 10000, 0, this);
                    String requestURL = "/data/heatmap?ssid=GTwifi";
                    new MakeRequest().execute(requestURL);
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
//                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
//                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default: Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }




        // Updates the location and zoom of the MapView

        return v;
    }

    @Override
    public void onLocationChanged(Location location) {

//        TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
//        map.animateCamera(CameraUpdateFactory.zoomTo(10));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
        map.animateCamera(cameraUpdate);

        // Setting latitude and longitude in the TextView tv_location
//        tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );

    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);


            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    // HeatMap Code

    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();
    public class ReturnHelper {
        public List<WeightedLatLng> list;
        public LatLng latLng;
        public ReturnHelper(List<WeightedLatLng> a, LatLng l) { this.list = a; this.latLng = l; }
    }


    private ReturnHelper processJsonArray(JSONArray jsonArray) throws Exception {
        ArrayList<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        int size = jsonArray.length();
        WeightedLatLng latLng;
        String lat = null, lng = null, weight;
        JSONArray ja = new JSONArray();
        JSONArray jarray;
        for (int i = 0; i < size; i++) {
            try {
                jarray = (JSONArray) jsonArray.get(i);
                weight = (String) jarray.get(0);
                lat = (String) jarray.get(1);
                lng = (String) jarray.get(2);
                latLng = new WeightedLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), Double.parseDouble(weight));
                list.add(latLng);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("NULLNULL", Log.getStackTraceString(e));
            }
        }
        Log.i("HEATMAP", list.toString());
        return new ReturnHelper(list, new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
//        JSONObject finalObject = new JSONObject();
//        finalObject.put(ja);
//        return finalObject;
    }

    protected void drawHeatMap(View v, JSONArray jsonArray) {
        ReturnHelper rH = null;
        try {
            rH = processJsonArray(jsonArray);
        } catch (Exception e) {

        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(rH.latLng, 15));
        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(rH.list)
                .build();
        mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        if (mProvider == null) {
            mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(rH.list)
                    .build();
            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
                mProvider.setWeightedData(rH.list);
                mOverlay.clearTileCache();
        }
        // Set up the spinner/dropdown list
//        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
//                R.array.ap_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new SpinnerActivity());
//        String requestURL = "/data/heatmap?ssid=GTwifi";
//        JSONArray jsonArray = new MakeRequest().execute(requestURL);

//        try {
//            mLists.put(getString(R.string.police_stations), new DataSet(readItems(R.raw.police),
//                    getString(R.string.police_stations_url)));
//            mLists.put(getString(R.string.medicare), new DataSet(readItems(R.raw.medicare),
//                    getString(R.string.medicare_url)));
//        } catch (JSONException e) {
////            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
//        }

        // Make the handler deal with the map
        // Input: list of WeightedLatLngs, minimum and maximum zoom levels to calculate custom
        // intensity from, and the map to draw the heatmap on
        // radius, gradient and opacity not specified, so default are used
    }

    public class SpinnerActivity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            String dataset = parent.getItemAtPosition(pos).toString();

            String requestURL = "/data/heatmap?ssid=" + dataset;
            new MakeRequest().execute(requestURL);

//            // Check if need to instantiate (avoid setData etc twice)
//            if (mProvider == null) {
//                mProvider = new HeatmapTileProvider.Builder().data(
//                        mLists.get(getString(R.string.police_stations)).getData()).build();
//                mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
//                // Render links
////                attribution.setMovementMethod(LinkMovementMethod.getInstance());
//            } else {
//                mProvider.setData(mLists.get(dataset).getData());
//                mOverlay.clearTileCache();
//            }
            // Update attribution
//            attribution.setText(Html.fromHtml(String.format(getString(R.string.attrib_format),
//                    mLists.get(dataset).getUrl())));

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    // Datasets from http://data.gov.au
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet {
        private ArrayList<LatLng> mDataset;
        private String mUrl;

        public DataSet(ArrayList<LatLng> dataSet, String url) {
            this.mDataset = dataSet;
            this.mUrl = url;
        }

        public ArrayList<LatLng> getData() {
            return mDataset;
        }

        public String getUrl() {
            return mUrl;
        }
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_main;
//    }


    public class MakeRequest extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... requestStrings) {
            String baseURL = "http://a.ashwinikhare.in:3050";
            String requestURL = baseURL + requestStrings[0];
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(requestURL);
            HttpResponse response = null;
            String sContent = null;
            try {
                response = httpclient.execute(httpGet);

                BufferedReader reader = null;

                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                sContent = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = null;
            if (sContent != null) {
                try {
                    jsonArray = new JSONArray(sContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }

        protected void onProgressUpdate(Integer... progress) {
            // TODO You are on the GUI thread, and the first element in
            // the progress parameter contains the last progress
            // published from doInBackground, so update your GUI
        }

        protected void onPostExecute(JSONArray jsonArray) {
            drawHeatMap(v, jsonArray);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ap_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String dataset = null;
        switch(item.getItemId()) {
            case R.id.gtwifi:
                dataset = getResources().getString(R.string.gtwifi);
                break;
            case R.id.gtvisitor:
                dataset = getResources().getString(R.string.gtvisitor);
                break;
            case R.id.gtother:
                dataset = getResources().getString(R.string.gtother);
                break;
            default:
                break;
        }
        if (dataset != null) {
            String requestURL = "/data/heatmap?ssid=" + dataset;
            new MakeRequest().execute(requestURL);
        }
//
        return false;
    }


}


