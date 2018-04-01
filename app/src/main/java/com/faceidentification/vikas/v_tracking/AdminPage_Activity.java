package com.faceidentification.vikas.v_tracking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPage_Activity extends AppCompatActivity {


    private EditText vehicle;
    private Button getLocation;
    private String latitude,longitude;
    private String id;
    private RecyclerView recyclerView;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page_);

        vehicle=findViewById(R.id.vehicleN);
        getLocation=findViewById(R.id.getLocation);
        recyclerView=findViewById(R.id.adminRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminPage_Activity.this));

         mRef=FirebaseDatabase.getInstance().getReference().child("admin"+FirebaseAuth.getInstance().getCurrentUser().getUid());


        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(vehicle.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        id=dataSnapshot.child("userRefKey").getValue().toString();

                        FirebaseDatabase.getInstance().getReference().child("user"+id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                latitude=dataSnapshot.child("latitude").getValue().toString();
                                longitude=dataSnapshot.child("longitude").getValue().toString();
                               // Toast.makeText(AdminPage_Activity.this,longitude+"  "+latitude,Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(AdminPage_Activity.this,MapsActivity.class);
                                intent.putExtra("latitude",latitude);
                                intent.putExtra("longitude",longitude);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

        showList();
    }

    private void showList(){

        FirebaseRecyclerOptions<vehicle> options =
                new FirebaseRecyclerOptions.Builder<vehicle>()
                        .setQuery(mRef, vehicle.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<vehicle,vehicleViewHolder>(options) {


            @Override
            public vehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_layout,parent,false);
                return new vehicleViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull vehicleViewHolder holder, int position, @NonNull final vehicle model) {
               final String ref=getRef(position).getKey();
                holder.vehicleNo.setText(ref);
                holder.userName.setText(model.getName());

                if(!ref.equals("admin")) {
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("user" + model.getUserRefKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    latitude = dataSnapshot.child("latitude").getValue().toString();
                                    longitude = dataSnapshot.child("longitude").getValue().toString();
                                    // Toast.makeText(AdminPage_Activity.this,longitude+"  "+latitude,Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AdminPage_Activity.this, Address_Activity.class);
                                    intent.putExtra("latitude", latitude);
                                    intent.putExtra("longitude", longitude);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         switch (item.getItemId()){

             case R.id.adminMenuNewUser:{
                 Intent intent=new Intent(AdminPage_Activity.this,RegisterUser_Activity.class);
                 startActivity(intent);
                 break;

             }
             case R.id.adminMenuLogout:{
                 FirebaseAuth.getInstance().signOut();
                 Intent intent=new Intent(AdminPage_Activity.this,WelcomePage_Activity.class);
                 startActivity(intent);
                 finish();
             }

         }

         return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent=new Intent(AdminPage_Activity.this,WelcomePage_Activity.class);
            startActivity(intent);
            finish();

        }
    }

    public static class vehicleViewHolder extends RecyclerView.ViewHolder{

        TextView vehicleNo,userName;
        View view;
        public vehicleViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            vehicleNo=itemView.findViewById(R.id.vehicleNo);
            userName=itemView.findViewById(R.id.userName);
        }
    }
}
