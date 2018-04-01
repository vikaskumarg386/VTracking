package com.faceidentification.vikas.v_tracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser_Activity extends AppCompatActivity {

    private EditText vehicleNo;
    private EditText userName;
    private EditText userPass;
    private EditText userId;
    private Button save;
    private String name,vehicle,pass,id;
    private String adminId,adminPass;
    private String adminRefKey;
    private String userRefKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_);

        save=findViewById(R.id.saveUser);

        vehicleNo=findViewById(R.id.vehicleNo);
        userName=findViewById(R.id.userRegName);
        userId=findViewById(R.id.userRegId);
        userPass=findViewById(R.id.userRegPass);
        adminRefKey=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("admin"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adminId=dataSnapshot.child("id").getValue().toString();
                adminPass=dataSnapshot.child("pass").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(RegisterUser_Activity.this);
                progressDialog.setMessage("please wait");
                progressDialog.show();
                name=userName.getText().toString();
                vehicle=vehicleNo.getText().toString();
                pass=userPass.getText().toString();
                id=userId.getText().toString();
                Toast.makeText(RegisterUser_Activity.this,"button clicked",Toast.LENGTH_SHORT).show();
                if (name!=null&&vehicle!=null&&pass!=null&&id!=null) {
                    vehicleNo.setText("");
                    userName.setText("");
                    userId.setText("");
                    userPass.setText("");
                    FirebaseAuth.getInstance().signOut();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(id+"@gmail.com",pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Map userMap = new HashMap();
                                userMap.put("name", name);
                                userMap.put("id",id);
                                userMap.put("pass",pass);
                                userMap.put("userRefKey",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                FirebaseDatabase.getInstance().getReference().child("admin"+adminRefKey).child(vehicle).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            final Map map=new HashMap();
                                            map.put("latitude","0.0");
                                            map.put("longitude","0.0");
                                            map.put("vehicle",vehicle);
                                            FirebaseDatabase.getInstance().getReference().child("user"+FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(adminId+"@gmail.com",adminPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(RegisterUser_Activity.this,"Saved",Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(RegisterUser_Activity.this, RegisterUser_Activity.class);
                                                                    startActivity(intent);
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterUser_Activity.this,"User id is used, try another user id",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterUser_Activity.this,"field is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent = new Intent(RegisterUser_Activity.this, WelcomePage_Activity.class);
            startActivity(intent);
        }
    }
}
