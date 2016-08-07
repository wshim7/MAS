package android.guptamayank.com.crowdsourcedconnectivity;

import android.location.Location;
import android.net.wifi.ScanResult;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Mayank on 10/25/2015.
 */
public class DataManager {
    private static volatile DataManager m_instance;
    private DataManager() {}

    public static DataManager getInstance() {
        if (m_instance == null) {
            synchronized (DataManager.class) {
                if (m_instance == null) {
                    m_instance = new DataManager();
                }
            }
        }

        return m_instance;
    }

    private volatile List<ScanResult> m_currentWifiList = null;
    private volatile Location m_currentLocation = null;
    private volatile String m_phoneModel = null;
    private volatile String m_androidVersion = null;
    private volatile String m_device = null;
    private volatile String m_deviceId = null;
    private volatile String m_locationComment = null;

    public synchronized void updateWifiList(List<ScanResult> updatedWifiList) {
        m_currentWifiList = updatedWifiList;
    }

    public synchronized void updateLocation(Location updatedLocation) {
        m_currentLocation = updatedLocation;
    }

    public synchronized void updateLocationComment(String updatedLocationComment) {
        m_locationComment = updatedLocationComment;
    }

    public synchronized void updatePhoneDetails(String phoneModel, String androidVersion,
                                                String device, String deviceId) {
        m_phoneModel = phoneModel;
        m_androidVersion = androidVersion;
        m_device = device;
        m_deviceId = deviceId;
    }

    public List<ScanResult> getWifiList() {
        return m_currentWifiList;
    }

    public Location getLocation() { return m_currentLocation; }

    public String getLocationComment() { return m_locationComment; }

    public HashMap<String, String> getUserDetails() {
        HashMap phoneDetails = new HashMap<String, String>();
        phoneDetails.put("phone_model", m_phoneModel);
        phoneDetails.put("android_version", m_androidVersion);
        phoneDetails.put("device", m_device);
        phoneDetails.put("imei", m_deviceId);
        return phoneDetails;
    }


}
