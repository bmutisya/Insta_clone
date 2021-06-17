package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username;
    private EditText name;
    private  EditText email;
    private  EditText password;
    private Button register;
    private TextView loginUser;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
        name =findViewById(R.id.name);
        email =findViewById(R.id.email);
        password =findViewById(R.id.password);
        loginUser=findViewById(R.id.login_user);
        register=findViewById(R.id.register);

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth =FirebaseAuth.getInstance();
        pd= new ProgressDialog(this);

        //FirebaseDatabase.getInstance().getReference().child("progra").child("android").setValue("abcdfhhj");

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername=username.getText().toString();
                String txtName=name.getText().toString();
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                if (TextUtils.isEmpty(txtUsername)|| TextUtils.isEmpty(txtName)||TextUtils.isEmpty(txtEmail)||
                TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(RegisterActivity.this,"Provide details for all fields",Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password Too short", Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(txtUsername,txtName,txtEmail,txtPassword);
                }

            }
        });
    }

    private void registerUser(String username, String name, String email, String password) {
        pd.setMessage("Please Wait");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object>  map=new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("username",username);
            map.put("id",mAuth.getCurrentUser().getUid());

                mRootRef.child("users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "update Profile for Better Experience", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent( RegisterActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }
        });
                   }
              //  }).addOnFailureListener(new OnFailureListener() {
            //@Override
            //public void onFailure(@NonNull  Exception e) {
              //  pd.dismiss();
                //Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

          //  }
        });
            }


}//37.22