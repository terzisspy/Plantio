package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

/**
 * The LogInActivity class is used so user can login
 */
public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginMail,loginPassword;
    private TextView signupRedirectTextView;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize UI components

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        loginMail = (EditText) findViewById(R.id.login_mail);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        signupRedirectTextView = (TextView) findViewById(R.id.signupRedirectText);

        // Set up click listener for loginButton

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = loginMail.getText().toString();
                String password = loginPassword.getText().toString();

                if(!(mail.isEmpty()&&password.isEmpty())){
                    if(Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                        auth.signInWithEmailAndPassword(mail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(LogInActivity.this,"Log In Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LogInActivity.this, AccountActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LogInActivity.this,"Log In Failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        loginMail.setError("Enter a Valid Mail Address");
                    }
                }
                else{
                    if(password.isEmpty()){
                        loginPassword.setError("Password cannot be empty");
                    }
                    if(mail.isEmpty()){
                        loginMail.setError("Password cannot be empty");
                    }
                }
            }
        });

        // Set up click listener so the user gets redirected

        signupRedirectTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));
            }
        });


        // Set up bottom navigation view

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_account);


        bottomNavigationView.setOnItemSelectedListener(item->{
            if(item.getItemId()==R.id.bottom_home){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if(item.getItemId()==R.id.bottom_search){
                startActivity(new Intent(getApplicationContext(), FindPlantsActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if(item.getItemId()==R.id.bottom_water){
                    CharSequence message = "You should LogIn to use that feature";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                    toast.show();
                    startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
            }
            else if(item.getItemId()==R.id.bottom_account) {
                return true;
            }
            return false;
        });
    }
}