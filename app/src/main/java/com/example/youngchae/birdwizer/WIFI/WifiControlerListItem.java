package com.example.youngchae.birdwizer.WIFI;

/**
 * Created by youngchae on 2016-07-16.
 */
public class WifiControlerListItem{
    private String WifiSSID;
    private String Location;

    public void setWifiSSID(String ssid){
        this.WifiSSID = ssid;
    }
    public void setLocation(String location){
        this.Location = location;
    }
    public String getWifiSSID(){
        return this.WifiSSID;
    }
    public String getLocation(){
        return this.Location;
    }

}
