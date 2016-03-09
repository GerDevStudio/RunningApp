package fr.gerdevstudio.runningapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * sert à récupérer les coordonnées gps
 */
public class GpsListener implements LocationListener {

    public final static String TAG = "GpsListener";
    public final static int UPDATE_TIME = 1000;
    public final static int UPDATE_DISTANCE = 3;

    private Location mLocation;

    private Context mContext;
    private LocationManager mLocationManager;
    private boolean mGpsEnabled;

    public GpsListener(Context c) {
        this.mContext = c;
        mGpsEnabled = false;
        mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
    }

    private Location getLocation() {

        // getting GPS status
        mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!mGpsEnabled) {
            // gps is disabled
        } else {
            // if GPS Enabled get lat/long using GPS Services
            if (mGpsEnabled) {
                if (mLocation == null) {
                    // active updates automatiques de la position
                    try {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                UPDATE_TIME,
                                UPDATE_DISTANCE, this);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Gps is Enabled");

                    if (mLocationManager != null) {
                        try {
                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return mLocation;
    }


    public Location getLastLocation() {
        return mLocation;
    }

    public void stopUsingGPS() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(GpsListener.this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    // Location Listener implementation
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged : Provider : " + provider + " / Status : " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        mGpsEnabled = true;
        Log.d(TAG, "onProviderEnabled : Provider : " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        mGpsEnabled = false;
        Log.d(TAG, "onProviderDisable : Provider : " + provider);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged : Latitude : " + location.getLatitude() + " Longitude : " + location.getLongitude());
        mLocation = location;
    }
}
