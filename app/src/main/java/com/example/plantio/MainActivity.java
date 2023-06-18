package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.application.isradeleon.notify.Notify;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    DatabaseReference db;
    public static ArrayList<Plant> plants;

    SearchAdapter searchAdapter;

    public static FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser currentUser;
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Authentication

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        // Retrieve Firebase Cloud Messaging (FCM) registration token

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the token and do whatever you want with it
                    String token = task.getResult();
                    Log.d("TAG", "FCM registration token: " + token);
                });


        // Retrieve plants from the database

        db = FirebaseDatabase.getInstance().getReference("Plant");
        plants = new ArrayList<>();
        searchAdapter = new SearchAdapter(this,plants);
        recyclerView.setAdapter(searchAdapter);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    plants.add(plant);
                }
                if(plants.size()>0){
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set up click listener for bottom navigation view

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item->{
                if(item.getItemId()==R.id.bottom_home){
                    return true;
                }
                else if(item.getItemId()==R.id.bottom_search){
                    startActivity(new Intent(getApplicationContext(), FindPlantsActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                }
                else if(item.getItemId()==R.id.bottom_water){
                    if(currentUser != null){
                        startActivity(new Intent(getApplicationContext(), WaterNFCActivity.class));
                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                        finish();
                        return true;
                    }
                    else {
                        CharSequence message = "You should LogIn to use that feature";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                        toast.show();
                        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                        finish();
                    }
                }
                else if(item.getItemId()==R.id.bottom_account) {
                    // Changing the state of notifications for user to 1 so its only sent once
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
                    return true;
                }
            return false;
        });
    }
}