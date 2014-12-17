package com.taptester.tappapp;

/**
 * Created by air on 17.09.14.
 */
import android.location.Location;

public interface GPSCallback {
    public abstract void onGPSUpdate(Location location);
}
