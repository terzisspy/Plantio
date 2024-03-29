package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * The SignUpActivity class is used so user can login
 */
public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupMail,signupPassword;
    private Button signupButton;
    private TextView loginRedirectTextView;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set UI components
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        auth = FirebaseAuth.getInstance();
        signupMail = (EditText) findViewById(R.id.signup_mail);
        signupPassword = (EditText) findViewById(R.id.signup_password);
        signupButton = (Button) findViewById(R.id.signup_button);
        loginRedirectTextView = (TextView) findViewById(R.id.loginRedirectText);

        // Set click listener for the sign-up button
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String userid = signupMail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                // Checking if the fields are filled
                if (userid.isEmpty())
                    signupMail.setError("Mail field cannot be empty");
                if (password.isEmpty())
                    signupPassword.setError("Mail field cannot be empty");
                else{
                    // Create user account using Firebase Authentication
                    auth.createUserWithEmailAndPassword(userid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if (task.isSuccessful()){
                                String userId = auth.getCurrentUser().getUid();
                                Map<String,Object> newUser = new HashMap<>();
                                // Store user information in Firebase Realtime Database
                                newUser.put("email",userid);
                                newUser.put("password",password);
                                newUser.put("plant",new HashMap<>());
                                usersRef.child(userId).setValue(newUser);
                                Toast.makeText(SignUpActivity.this, "Sing Up Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                            }else{
                                Toast.makeText(SignUpActivity.this, "Sing Up Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        // Set up click listener so the user gets redirected
        loginRedirectTextView.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 startActivity(new Intent(SignUpActivity.this,LogInActivity.class));
                }
        });


        // Set up bottom navigation bar
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
                CharSequence message = "You should SignUp to use that feature";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                toast.show();
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
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