package com.faceidentification.vikas.v_tracking;

import android.*;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by vikas on 12/6/18.
 */

public class MyServices extends Service {

    private GPSTracker gps;
    private static final int REQUEST_CODE_PERMISSION=2;
    private String mPermission= android.Manifest.permission.ACCESS_FINE_LOCATION;

    private FirebaseAuth auth;
    private String id;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {



        auth=FirebaseAuth.getInstance();
        id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        try{
            if (ActivityCompat.checkSelfPermission(this,mPermission)!= MockPackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{mPermission},REQUEST_CODE_PERMISSION);
            }

        }catch (Exception e){
            e.getStackTrace();
        }

        gps=new GPSTracker(MyServices.this);
        if (gps.canGetLocation()){
            double latitude=gps.getLatitude();
            double longitude=gps.getLongitude();
            FirebaseDatabase.getInstance().getReference().child("user"+id).child("longitude").setValue(String.valueOf(longitude));
            FirebaseDatabase.getInstance().getReference().child("user"+id).child("latitude").setValue(String.valueOf(latitude));

        }
        else {
            gps.showSettingAlert();
        }
        return START_STICKY;
    }



    @Override
    public void onDestroy()
    {

    }




}
