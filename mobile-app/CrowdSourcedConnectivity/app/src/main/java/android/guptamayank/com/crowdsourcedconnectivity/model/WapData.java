package android.guptamayank.com.crowdsourcedconnectivity.model;

/**
 * Created by Mayank on 10/25/2015.
 */
public class WapData {
    private String bssid;
    private String ssid;
    private String capabilities;
    private long frequency;
    private String extras;
    private boolean isActiveConnection;
    private int signalLevel;

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public boolean getIsActiveConnection() { return isActiveConnection; }

    public void setIsActiveConnection(boolean isActiveConnection) { this.isActiveConnection = isActiveConnection; }

    public int getSignalLevel() { return signalLevel; }

    public void setSignalLevel(int signalLevel) { this.signalLevel = signalLevel; }
}
