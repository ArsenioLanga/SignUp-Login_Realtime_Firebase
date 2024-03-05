package com.demo.signuploginrealtime;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private FirebaseDatabase auth;
    TextView forgotPassword;
    GoogleSignInButton googleBtn;
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        googleBtn = findViewById(R.id.googleBtn);

        auth = FirebaseDatabase.getInstance();
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


       forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = view.findViewById(R.id.emailBox);

                builder.setView(view);
                AlertDialog dialog = builder.create();

                view.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userEmail = emailBox.getText().toString();

                        if(TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(LoginActivity.this, "Enter your resgistered email",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    /*     auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        if (dialog.getWindow() != null){
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        dialog.show();
                        */
                    }
                });

                //Inside onCreate
                gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                gClient = GoogleSignIn.getClient(LoginActivity.this, gOptions);
                GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
                if (gAccount != null){
                    finish();
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
                ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == Activity.RESULT_OK){
                                    Intent data = result.getData();
                                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                                    try {
                                        task.getResult(ApiException.class);
                                        finish();
                                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                    } catch (ApiException e){
                                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

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
        String username = loginUsername.getText().toString().trim();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = databaseReference.orderByChild("username").equalTo(username);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("CheckUser", "Snapshot exists: " + snapshot.exists());

                if(snapshot.exists()){
                    String password = loginPassword.getText().toString().trim();
                    loginUsername.setError(null);

                    String passwordFromDB = snapshot.child(username).child("password").getValue(String.class);
                    String nameFromDB = snapshot.child(username).child("name").getValue(String.class);
                    String emailFromDB = snapshot.child(username).child("email").getValue(String.class);
                    String usernameFromDB = snapshot.child(username).child("username").getValue(String.class);

                    Log.d("CheckUser", "Password for DB: " + passwordFromDB);

                    //if(Objects.equals(passwordForDB, password)){
                    if(passwordFromDB.equals(password)){
                        Log.d("CheckUser", "Password match. Logging in...");
                        loginUsername.setError(null);
                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("CheckUser", "Invalid credentials");
                        loginPassword.setError("Credenciais inválidas");
                        Toast.makeText(LoginActivity.this, "Senha inválida", Toast.LENGTH_LONG).show();
                        loginPassword.requestFocus();
                    }
                } else {
                    Log.d("CheckUser", "User does not exist");
                    Toast.makeText(LoginActivity.this, "Usuário inválido", Toast.LENGTH_LONG).show();
                    loginUsername.setError("Usuário não existe");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CheckUser", "Database error: " + error.getMessage());
                // Adicione um log ou tratamento de erro adequado aqui, se necessário
            }
        });
    }





}