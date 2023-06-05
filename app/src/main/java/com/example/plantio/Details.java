package com.example.plantio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Details extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Plant plant =  getIntent().getParcelableExtra("planttag");
        TextView name,fulldescription,light,lowtemp,hightemp,frequency;
        CircleImageView image;
        name = (TextView)findViewById(R.id.nametext);
        fulldescription = (TextView)findViewById(R.id.shortdescriptiontext);
        light = (TextView)findViewById(R.id.lighttextView);
        frequency = (TextView)findViewById(R.id.frequencyText);
        lowtemp = (TextView)findViewById(R.id.lowtemptextView);
        hightemp = (TextView)findViewById(R.id.hightemptextView);
        image = (CircleImageView)findViewById(R.id.img1);
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        name.setText(plant.getName());
        fulldescription.setText(plant.getFull_description());
        light.setText(plant.getLight());
        lowtemp.setText(plant.getLow_temperature()+" °C");
        hightemp.setText(plant.getHigh_temperature()+" °C");
        frequency.setText(plant.getFrequency()+" days.");

        Glide.with(image.getContext())
                .load(plant.getImage())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark)
                .into(image);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
                if(MyAdapter.notifsent>0){
                    MyAdapter.notifsent=1;
                }
                if(currentUser != null){
                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                }
            }
            return false;
        });
    }
}