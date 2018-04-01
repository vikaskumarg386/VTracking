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

public class Login_Activity extends AppCompatActivity {

    private EditText userId,userPass;
    private Button userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        userId=findViewById(R.id.userId);
        userPass=findViewById(R.id.userPass);
        userLogin=findViewById(R.id.userLogin);

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog=new ProgressDialog(Login_Activity.this);
                progressDialog.setMessage("please wait");
                progressDialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(userId.getText().toString()+"@gmail.com",userPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                        Intent intent=new Intent(Login_Activity.this,MainActivity.class);
                        intent.putExtra("id",userId.getText().toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();}
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(Login_Activity.this,"Wrong id password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
