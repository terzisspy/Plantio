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

/**
 * The SearchResults class is used to display the results from user's search.
 */

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

        // Retrieve plants matching search criteria
        db = FirebaseDatabase.getInstance().getReference("Plant");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        plants = new ArrayList<>();
        searchAdapter = new SearchAdapter(this,plants);
        recyclerView.setAdapter(searchAdapter);
        context =this;

        // Set up event listener
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    if(plant.getLight().equals(lightText)&&plant.getHigh_temperature()>=Double.parseDouble(highTempText)&&plant.getLow_temperature()<=Double.parseDouble(lowTempText))
                        plants.add(plant);
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

        // Set up bottom navigation bar
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

        // Updating notification to 1 if it;s not done already
        if(MyAdapter.notifsent>0){
            MyAdapter.notifsent=1;
        }

    }

}