package com.app.smartwatchapp.Models;

import java.io.Serializable;

public class Watch implements Serializable {
    String watchName;
    String watchMACAddress;

    public Watch() {

    }

    public Watch(String watchName, String watchMACAddress) {
        this.watchName = watchName;
        this.watchMACAddress = watchMACAddress;
    }

    public String getWatchName() {
        return watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getWatchMACAddress() {
        return watchMACAddress;
    }

    public void setWatchMACAddress(String watchMACAddress) {
        this.watchMACAddress = watchMACAddress;
    }
}
