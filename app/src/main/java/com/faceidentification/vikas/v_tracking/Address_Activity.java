package com.faceidentification.vikas.v_tracking;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Address_Activity extends AppCompatActivity {

    private TextView add;
    private TextView addCity;
    private TextView addState;
    private TextView addCountry;
    private TextView addPostalCode;
    private TextView addName;
    private String longitude;
    private String latitude;

    private Button map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_);

        add=findViewById(R.id.address_);
        addState=findViewById(R.id.addState_);
        addCountry=findViewById(R.id.addCountry_);
        addCity=findViewById(R.id.addCity_);
        addPostalCode =findViewById(R.id.addCode_);
        addName=findViewById(R.id.addName_);
        map=findViewById(R.id.map);

        latitude=getIntent().getStringExtra("latitude");
        longitude=getIntent().getStringExtra("longitude");
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
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

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Address_Activity.this,MapsActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);
            }
        });
    }
}
