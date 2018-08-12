package com.faceidentification.vikas.v_tracking;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    private TextView add;
    private TextView addCity;
    private TextView addState;
    private TextView addCountry;
    private TextView addPostalCode;
    private TextView addName;
    private Button button;
    private Button logOut;
    private Button button2;
    private GPSTracker gps;
    private static final int REQUEST_CODE_PERMISSION=2;
    private String mPermission= Manifest.permission.ACCESS_FINE_LOCATION;

    private FirebaseAuth auth;
    private String id;
    private String ownerId;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.textView);
        button=findViewById(R.id.button);
        logOut=findViewById(R.id.logOutUser);

        add=findViewById(R.id.add);
        addState=findViewById(R.id.addState);
        addCountry=findViewById(R.id.addCountry);
        addCity=findViewById(R.id.addCity);
        addPostalCode =findViewById(R.id.addCode);
        addName=findViewById(R.id.addName);
        button2=findViewById(R.id.button2);
        auth=FirebaseAuth.getInstance();
        id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        startService(new Intent(this, MyServices.class));
        try{
            if (ActivityCompat.checkSelfPermission(this,mPermission)!= MockPackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{mPermission},REQUEST_CODE_PERMISSION);
            }

        }catch (Exception e){
            e.getStackTrace();
        }

        gps=new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()){
            latitude=gps.getLatitude();
            longitude=gps.getLongitude();
            FirebaseDatabase.getInstance().getReference().child("user"+id).child("longitude").setValue(String.valueOf(longitude));
            FirebaseDatabase.getInstance().getReference().child("user"+id).child("latitude").setValue(String.valueOf(latitude));
            textView.setText(latitude+"  "+longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                add.setText(address);
                addCity.setText(city);
                addState.setText(state);
                addCountry.setText(country);
                addPostalCode.setText(postalCode);
                addName.setText(knownName);

            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        else {
            gps.showSettingAlert();
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                gps=new GPSTracker(MainActivity.this);
                if (gps.canGetLocation()){
                    double latitude=gps.getLatitude();
                    double longitude=gps.getLongitude();
                    FirebaseDatabase.getInstance().getReference().child("user"+id).child("longitude").setValue(String.valueOf(longitude));
                    FirebaseDatabase.getInstance().getReference().child("user"+id).child("latitude").setValue(String.valueOf(latitude));
                    textView.setText(latitude+"  "+longitude);

                }
                else {
                    gps.showSettingAlert();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("user"+FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownerId=dataSnapshot.child("ownerId").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ownerId)){
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("AllVehicle","AllVehicle");
                intent.putExtra("ownerId",ownerId);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);

                startActivity(intent);}
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this,WelcomePage_Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

            if (auth==null){
           Intent intent=new Intent(MainActivity.this,WelcomePage_Activity.class);
            startActivity(intent);
            finish();}

    }
}
