package com.demo.signuploginrealtime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpAtivity extends AppCompatActivity {

    EditText signUpName, signUpEmail, signUpUsername, signUpPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_ativity);

        //Pegar as variaveis dos campos
        signUpName = findViewById(R.id.signup_name);
        signUpEmail = findViewById(R.id.signup_email);
        signUpUsername = findViewById(R.id.signup_username);
        signUpPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        //Evento do click do botão
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Inicializar o firebase
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Users");

                String name = signUpName.getText().toString();
                String email = signUpEmail.getText().toString();
                String username = signUpUsername.getText().toString();
                String password = signUpPassword.getText().toString();

                HelperClass object = new HelperClass(name, email, password, username);

                //Enviar para o firebase
                databaseReference.child(name).setValue(object);
                Toast.makeText(SignUpAtivity.this, "You have been signui successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpAtivity.this, LoginActivity.class);
                startActivity(intent);
                // finish();

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpAtivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}