package com.faceidentification.vikas.v_tracking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Created by vikas on 30/3/18.
 */

public class GPSTracker implements LocationListener {

    private final Context mContext;

    private boolean isGPSEnabled=false;
    private boolean isNetworkEnabled=false;
    private boolean canGetLocation=false;

    private Location location;
    private double longitude;
    private double latitude;

    private static final long MIN_DIS_FOR_UPDATES=0;
    private static final long MIN_TIME_BW_UPADTE=1000;
    protected LocationManager locationManager;

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public Location getLocation(){

        try{

            locationManager=(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled&&!isNetworkEnabled){

            }
            else {
                this.canGetLocation=true;
                if (isNetworkEnabled){
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPADTE,MIN_DIS_FOR_UPDATES,this);
                    if (locationManager!=null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location!=null){
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();

                        }
                    }
                }
                if (isGPSEnabled){
                    if (location==null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPADTE,MIN_DIS_FOR_UPDATES,this);
                        if (locationManager!=null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location!=null){
                                latitude=location.getLatitude();
                                longitude=location.getLongitude();

                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return location;
    }
    public void stopGPSTracking(){
        if (locationManager!=null){
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                return;
            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
    public double getLatitude(){
        if (location!=null){
            this.latitude=location.getLatitude();
        }
        return this.latitude;
    }
    public double getLongitude(){
        if (location!=null){
            this.longitude=location.getLongitude();
        }
        return this.longitude;
    }

    public boolean canGetLocation(){
       return this.canGetLocation;
    }

    public void showSettingAlert(){

        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("GPS settings");
        builder.setMessage("GPS is not enabled do you want to go settings menu");

        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
