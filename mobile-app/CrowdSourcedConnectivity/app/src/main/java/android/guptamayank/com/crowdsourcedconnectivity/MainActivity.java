package android.guptamayank.com.crowdsourcedconnectivity;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks  {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private AlarmManager alarmManager;
    private Intent globalAalarmRecieverIntent;
    private PendingIntent pendingIntent;
    private int intervalInMinutes = 1;
    private String TAG = MainActivity.class.getName();
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData("Administrator", "admin@crowdconnect.com", BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
        startAlarmManager();

    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");
        Monitor.startDataPackageService(this);
        WifiScan.startWifiScanService(this);
        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        globalAalarmRecieverIntent = new Intent(context, GlobalAlarmReciever.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, globalAalarmRecieverIntent, 0);


        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                30000, // 60000 = 1 minute
                pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");


        Context context = getBaseContext();
        Intent alarmRecieverIntent = new Intent(context, GlobalAlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmRecieverIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void switchFragment(int position) {
        Log.i(TAG, "The position right now is " + position);
        Fragment fragment = null;
        String tag = "";
        switch (position) {
            case 0:
                fragment = new WifiListFragment();
                tag = "WifiList";
                break;
            case 1:
                fragment =  new SignalStrengthFragment();
                tag = "SignalStrength";
            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(id, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
//        if (position == 0) {
//            Intent myIntent = new Intent(MainActivity.this, WifiList.class);
//            MainActivity.this.startActivity(myIntent);
//        }
        switchFragment(position);
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
