package com.faceidentification.vikas.v_tracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomePage_Activity extends AppCompatActivity {

    private Button user;
    private Button admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page_);
        user=findViewById(R.id.user);
        admin=findViewById(R.id.admin);


        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            final ProgressDialog progressDialog=new ProgressDialog(WelcomePage_Activity.this);
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            user.setEnabled(false);
            admin.setEnabled(false);
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("admin"+FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Intent intent=new Intent(WelcomePage_Activity.this,AdminPage_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                    else if (dataSnapshot.hasChild("user"+FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Intent intent=new Intent(WelcomePage_Activity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                    else {
                        user.setEnabled(true);
                        admin.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomePage_Activity.this,Login_Activity.class);
                startActivity(intent);
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WelcomePage_Activity.this,Admin_Login_Activity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
