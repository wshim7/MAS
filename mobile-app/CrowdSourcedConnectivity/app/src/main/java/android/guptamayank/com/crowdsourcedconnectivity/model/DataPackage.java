package android.guptamayank.com.crowdsourcedconnectivity.model;

/**
 * Created by MLandes on 10/24/2015.
 */
public class DataPackage {
    private LocationData locationData;
    private WapData wapData;
    private UserData userData;

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }

    public WapData getWapData() {
        return wapData;
    }

    public void setWapData(WapData wapData) {
        this.wapData = wapData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
