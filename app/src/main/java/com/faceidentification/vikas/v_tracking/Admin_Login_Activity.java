package com.faceidentification.vikas.v_tracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Admin_Login_Activity extends AppCompatActivity {

    private Button adminLogin;
    private Button newAdmin;


    private EditText adminId;
    private EditText adminPass;

    private String id,pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__login_);

        adminLogin=findViewById(R.id.adminLogin);
        newAdmin=findViewById(R.id.newAdmin);

        adminId=findViewById(R.id.adminId);
        adminPass=findViewById(R.id.adminPassword);



        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=adminId.getText().toString();
                pass=adminPass.getText().toString();
               // Toast.makeText(Admin_Login_Activity.this,id+" "+pass,Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(id)&& !TextUtils.isEmpty(pass)) {
                    final ProgressDialog progressDialog=new ProgressDialog(Admin_Login_Activity.this);
                    progressDialog.setMessage("please wait");
                    progressDialog.show();
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(id+"@gmail.com",pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent=new Intent(Admin_Login_Activity.this,AdminPage_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(Admin_Login_Activity.this,"incorrect email password",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Admin_Login_Activity.this,"incorrect email password",Toast.LENGTH_SHORT).show();
                }

            }
        });
        newAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_Login_Activity.this,Register_Admin_Activity.class);
                startActivity(intent);
            }
        });


    }
}
