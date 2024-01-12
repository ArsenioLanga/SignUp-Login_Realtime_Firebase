package com.demo.signuploginrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText loginUsername, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validatePassword() | !validateUsername()){
                    Toast.makeText(LoginActivity.this, "Dados invalidos", Toast.LENGTH_LONG).show();
                } else{
                    checkUser();
                }
            }
        });


        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(LoginActivity.this, SignUpAtivity.class);
               startActivity(intent);
                finish();
            }
        });
    }

    public Boolean validateUsername(){
        String val = loginUsername.getText().toString();
        if(val.isEmpty()){
            loginUsername.setError("Username cannot be empty");
            Toast.makeText(LoginActivity.this, "Campos Obrigatirios", Toast.LENGTH_LONG).show();
            return false;
        }else{
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Username cannot be empty");
            Toast.makeText(LoginActivity.this, "Campos Obrigatirios", Toast.LENGTH_LONG).show();
            return false;
        }else{
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = databaseReference.orderByChild("username").equalTo(username);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    loginUsername.setError(null);
                    String passwordForDB  = snapshot.child(username).child("password").getValue(String.class);

                    if(!Objects.equals(passwordForDB, username)){
                        loginUsername.setError(null);
                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else{
                        loginPassword.setError("Invalid credentials");
                        Toast.makeText(LoginActivity.this, "Senha invalida", Toast.LENGTH_LONG).show();
                        loginPassword.requestFocus();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "User invalida", Toast.LENGTH_LONG).show();
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}