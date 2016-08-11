package com.example.ishmeetsingh.rideshine;

/**
 * Created by pronto on 25/7/16.
 */
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class LocationService extends Service implements LocationListener {

    private Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // Max time to listen for location in seconds
    private static final int LISTENER_TIMEOUT = 1000 * 60 * 2; // 2 minute

    // Duration in seconds during which the location is considered current
    private static final int LOCATION_VALIDITY_DURATION = 1000 * 60 * 2;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private Boolean listening = false;

    private Timer timer;

    private LocationCallback callBack;

    private LocationService() {}

    public LocationService(Context context, LocationCallback callBackLocation) {
        this.mContext = context;
        this.callBack = callBackLocation;
        if(isLocationEnabled()) {
            getLocation();
        } else {
            Toast.makeText(context, "Please Enable Location Provider", Toast.LENGTH_LONG).show();
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    listening = true;
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Logger.getLogger(getClass().getSimpleName()).info("Location Updates: " + location.toString());
                            if(callBack != null) {
                                callBack.lastLocation(location);
                            }
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        listening = true;
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Logger.getLogger(getClass().getSimpleName()).info("Location Updates: " + location.toString());
                                if(callBack != null) {
                                    callBack.lastLocation(location);
                                }
                            }
                        }
                    }
                }
            }

            if(listening) {
                // Stop listening after LISTENER_TIMEOUT if onLocationChanged is never received
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Logger.getLogger(getClass().getSimpleName()).info("Location timer timed out");
                        stopUsingGPS();
                    }
                }, LISTENER_TIMEOUT);
            }

        } catch (Exception e) {
            e.printStackTrace();
            listening = false;
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        Logger.getLogger(getClass().getSimpleName()).info("Location Service Stopped");
        listening = false;
        if (timer != null) {
            timer.cancel();
        }
        if (locationManager != null) {
            locationManager.removeUpdates(LocationService.this);
        }
    }

    /**
     * Function to get latitude
     * */
    private double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    private double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Gets the last location
     * Asks for location updates if last location seen is not valid anymore
     * @return last location or null if location wasn't seen yet
     */
    public Location getLastLocation() {
        // If the last location is null or older than LOCATION_VALIDITY_DURATION, start listening for a new one
        if (location == null || (System.currentTimeMillis() - location.getTime()) > LOCATION_VALIDITY_DURATION) {
            // Start listening if not already listening
            if (!listening) {
                Logger.getLogger(getClass().getSimpleName()).info("Last location is null or outdated");
                getLocation();
            }
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            Logger.getLogger(getClass().getSimpleName()).info("Location Updates: " + this.location.toString());
            if(callBack != null) {
                callBack.lastLocation(location);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Whether app has location permissions or not
     * @return app has location permissions or not
     */
    public boolean isLocationEnabled() {
        if (ActivityCompat.checkSelfPermission((Activity)mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity)mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

}