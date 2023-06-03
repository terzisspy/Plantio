package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {
    private RecyclerView recyclerView;
    public Intent intent = getIntent();
    private static String lightText = null;
    private static String lowTempText = null;
    private static String highTempText = null;
    Context context;
    DatabaseReference db;
    ArrayList<Plant> plants;

    SearchAdapter searchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        lightText = getIntent().getStringExtra("light");
        lowTempText = getIntent().getStringExtra("lowTemp");
        highTempText = getIntent().getStringExtra("highTemp");

        recyclerView = (RecyclerView) findViewById(R.id.searchResults);

        /*FirebaseRecyclerOptions<Plant> options =
                new FirebaseRecyclerOptions.Builder<Plant>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Plant").orderByChild("light").equalTo(lightText), Plant.class)
                        .build();
        myAdapter = new MyAdapter(options);
        recyclerView.setAdapter(myAdapter);*/

        db = FirebaseDatabase.getInstance().getReference("Plant");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        plants = new ArrayList<>();
        searchAdapter = new SearchAdapter(this,plants);
        recyclerView.setAdapter(searchAdapter);
        context =this;
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    //System.out.println(plant.getLight()+" "+lightText);
                    //System.out.println(plant.getHigh_temperature()+" "+highTempText);
                    //System.out.println(plant.getLow_temperature()+" "+lowTempText);
                    if(plant.getLight().equals(lightText)&&plant.getHigh_temperature()>=Double.parseDouble(highTempText)&&plant.getLow_temperature()<=Double.parseDouble(lowTempText))
                        plants.add(plant);
                    //System.out.println(plants);
                }
                if(plants.size()>0){
                    searchAdapter.notifyDataSetChanged();
                }
                else{
                    CharSequence message = "There are no results matching your criteria. Please Try again!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();
                    startActivity(new Intent(getApplicationContext(), FindPlantsActivity.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);

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

    /*@Override
    protected void onStart(){
        super.onStart();
        searchAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        searchAdapter.stopListening();
    }*/
}