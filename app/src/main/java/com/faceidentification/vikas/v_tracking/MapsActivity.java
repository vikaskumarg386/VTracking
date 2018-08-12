package com.faceidentification.vikas.v_tracking;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String longitude;
    private String latitude;
    private DatabaseReference mRef;
    private String allVehicle;
    private String userId;
    private String ownerId;
    private String ownerLat;
    private String ownerLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude=getIntent().getStringExtra("latitude");
        longitude=getIntent().getStringExtra("longitude");
        allVehicle=getIntent().getStringExtra("AllVehicle");
        ownerId=getIntent().getStringExtra("ownerId");
        if(!TextUtils.isEmpty(ownerId)){
            userId=ownerId;
           GPSTracker gps=new GPSTracker(MapsActivity.this);
            if (gps.canGetLocation()){
                latitude=String.valueOf(gps.getLatitude());
                longitude=String.valueOf(gps.getLongitude());
                FirebaseDatabase.getInstance().getReference().child("user"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("longitude").setValue(String.valueOf(longitude));
                FirebaseDatabase.getInstance().getReference().child("user"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("latitude").setValue(String.valueOf(latitude));




            }
            else {
                gps.showSettingAlert();
            }


        }
        else {
            userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            GPSTracker ownerGps=new GPSTracker(MapsActivity.this);
            if(ownerGps.canGetLocation()){
                ownerLat=String.valueOf(ownerGps.getLatitude());
                ownerLng=String.valueOf(ownerGps.getLongitude());
            }
        }

        mRef= FirebaseDatabase.getInstance().getReference();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(!TextUtils.isEmpty(ownerLat)&&!TextUtils.isEmpty(ownerLng)){
            LatLng owner = new LatLng(Double.parseDouble(ownerLat), Double.parseDouble(ownerLng));
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            markerOptions.getInfoWindowAnchorU();
            mMap.addMarker(markerOptions.position(owner).title("Your Position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(owner));}

        Log.i("inOnMapReadyFunction","onMapReadyFunction");
        mRef.child("admin"+userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for(DataSnapshot d:dataSnapshot.getChildren()){
                   if(d.hasChild("userRefKey")){
                       Log.i("inOnMapReadyFunction","onMapReadyFunctionUserRefKey"+d.child("userRefKey").getValue().toString());
                       mRef.child("user"+d.child("userRefKey").getValue().toString()).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               if (dataSnapshot.hasChild("latitude")&&dataSnapshot.hasChild("longitude")) {
                                   String lat = dataSnapshot.child("latitude").getValue().toString();
                                   String lng = dataSnapshot.child("longitude").getValue().toString();
                                   String vehicle = dataSnapshot.child("vehicle").getValue().toString();
                                   if (allVehicle == null||!TextUtils.isEmpty(ownerId)) {
                                       if (lat.equals(latitude) && lng.equals(longitude)) {
                                           LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                           MarkerOptions markerOptions=new MarkerOptions();
                                           markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                           markerOptions.getInfoWindowAnchorU();
                                           mMap.addMarker(markerOptions.position(sydney).title("Your Vehicle position "+vehicle));
                                           mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                       } else {
                                           LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                           mMap.addMarker(new MarkerOptions().position(sydney).title("Your Vehicle position "+vehicle));
                                           mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                                       }
                                   } else {
                                       LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                       mMap.addMarker(new MarkerOptions().position(sydney).title("Your vehicle postion "+vehicle));
                                       mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                                   }
                               }
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });
                   }
               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add a marker in Sydney and move the camera

    }
}
