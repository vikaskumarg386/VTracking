package com.faceidentification.vikas.v_tracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
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
    private String name;
    private String vehicleNo;
    private ImageButton changeImage;
    private ImageView image;


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
        image=findViewById(R.id.vehicleImage);
        changeImage=findViewById(R.id.changeImage);


        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),1);
            }
        });

        latitude=getIntent().getStringExtra("latitude");
        longitude=getIntent().getStringExtra("longitude");
        name=getIntent().getStringExtra("name");
        vehicleNo=getIntent().getStringExtra("vehicleNumber");

        FirebaseDatabase.getInstance().getReference().child("admin"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child(vehicleNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("imageUrl")){
                    Picasso.with(Address_Activity.this).load(dataSnapshot.child("imageUrl").getValue().toString()).placeholder(R.drawable.vehicle).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        Picasso.with(Address_Activity.this).load(dataSnapshot.child("imageUrl").getValue().toString()).placeholder(R.drawable.vehicle).into(image);
                        }
                    });
                }
                addName.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            //addName.setText(name);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==RESULT_OK){

            final ProgressDialog progressDialog = new ProgressDialog(Address_Activity.this);
            progressDialog.setTitle("Uploading image...");
            progressDialog.setMessage("Please wait until video upload");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final Uri imageUri=data.getData();

            StorageReference thumbStorage = FirebaseStorage.getInstance().getReference().child("image").child(vehicleNo).child(vehicleNo+"jpg");

            UploadTask uploadTask=thumbStorage.putFile(imageUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                       final String imageUrl = task.getResult().getDownloadUrl().toString();
                        FirebaseDatabase.getInstance().getReference().child("admin"+ FirebaseAuth.getInstance().getCurrentUser().getUid()).child(vehicleNo).child("imageUrl").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Picasso.with(Address_Activity.this).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.vehicle).into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                    Picasso.with(Address_Activity.this).load(imageUrl).placeholder(R.drawable.vehicle).into(image);
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        });

                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(Address_Activity.this, "failed to upload in storage", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });

        }
    }
}
