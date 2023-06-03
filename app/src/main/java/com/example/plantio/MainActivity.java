package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    DatabaseReference db;
    ArrayList<Plant> plants;

    SearchAdapter searchAdapter;

    public static FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*FirebaseRecyclerOptions<Plant> options =
                new FirebaseRecyclerOptions.Builder<Plant>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Plant"), Plant.class)
                        .build();*/

        //myAdapter = new MyAdapter(options);
        //recyclerView.setAdapter(myAdapter);
        mFirebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        currentUser = mFirebaseAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference("Plant");
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
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
                    startActivity(new Intent(getApplicationContext(), WaterNFCActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                }
                else if(item.getItemId()==R.id.bottom_account) {
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

    /*@Override
    protected void onStart(){
        super.onStart();
        myAdapter.startListening();
        if(mFirebaseAuth!=null)
            loggedin=1;
        else{
            loggedin=0;
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        myAdapter.stopListening();
    }*/
}