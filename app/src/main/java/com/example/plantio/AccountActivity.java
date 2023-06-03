package com.example.plantio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {
    private TextView mailText;
    private Button logoutButton;
    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mailText = (TextView) findViewById(R.id.mailText);
        logoutButton = (Button) findViewById(R.id.logout_button);
        mFirebaseAuth = FirebaseAuth.getInstance();
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
                startActivity(new Intent(getApplicationContext(), WaterNFCActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if(item.getItemId()==R.id.bottom_account) {
                return true;
            }
            return false;
        });

        logoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                logout(v);
            }

        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mFirebaseAuth!=null) {
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                mailText.setText(mFirebaseUser.getEmail());
            }
        }
        else{
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        }
    }

    public void logout(View view){
        MainActivity.mFirebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
}