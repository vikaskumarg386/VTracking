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
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register_Admin_Activity extends AppCompatActivity {

    private EditText adminId;
    private EditText adminPass;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__admin_);

        adminId=findViewById(R.id.adminRegId);
        adminPass=findViewById(R.id.adminRegPass);

        register=findViewById(R.id.adminReg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminId!=null&&adminPass!=null){
                    final ProgressDialog progressDialog=new ProgressDialog(Register_Admin_Activity.this);
                    progressDialog.setMessage("please wait");
                    progressDialog.show();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(adminId.getText().toString()+"@gmail.com",adminPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {
                              Map userMap = new HashMap();

                              userMap.put("id",adminId.getText().toString());
                              userMap.put("pass",adminPass.getText().toString());
                              FirebaseDatabase.getInstance().getReference().child("admin"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("admin").setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful()) {

                                          Intent intent = new Intent(Register_Admin_Activity.this, AdminPage_Activity.class);
                                          startActivity(intent);
                                          finish();
                                          progressDialog.dismiss();
                                      }
                                  }
                              });
                          }
                          else {
                              progressDialog.dismiss();
                              Toast.makeText(Register_Admin_Activity.this,"Password is weak or already registered",Toast.LENGTH_SHORT).show();
                          }


                        }
                    });
                }
            }
        });
    }

}
