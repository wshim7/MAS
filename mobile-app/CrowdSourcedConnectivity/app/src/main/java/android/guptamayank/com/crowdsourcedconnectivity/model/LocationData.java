package android.guptamayank.com.crowdsourcedconnectivity.model;

/**
 * Created by Mayank on 10/24/2015.
 */
public class LocationData {
    private long timestamp;
    private long dbLevel;
    private double gpsLat;
    private double gpsLong;
    private int signalLevel;
    private float locationSpeed;
    private String locationComment;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSignalLevel() { return signalLevel; }

    public void setSignalLevel(int signalLevel) { this.signalLevel = signalLevel; }

    public long getDbLevel() {
        return dbLevel;
    }

    public void setDbLevel(long dbLevel) {
        this.dbLevel = dbLevel;
    }

    public double getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(double gpsLat) {
        this.gpsLat = gpsLat;
    }

    public double getGpsLong() {
        return gpsLong;
    }

    public void setGpsLong(double gpsLong) {
        this.gpsLong = gpsLong;
    }

    public float getLocationSpeed() { return locationSpeed; }

    public void setLocationSpeed(float locationSpeed) { this.locationSpeed = locationSpeed; }

    public String getLocationComment() {
        return locationComment;
    }

    public void setLocationComment(String locationComment) {
        this.locationComment = locationComment;
    }
}
