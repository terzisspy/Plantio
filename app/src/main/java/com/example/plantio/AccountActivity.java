package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * The AccountActivity class represents the activity that displays the user account information.
 */

public class AccountActivity extends AppCompatActivity {
    private TextView mailText;
    private Button logoutButton,addButton;
    private FirebaseAuth mFirebaseAuth;
    RecyclerView recyclerView;

    DatabaseReference db;
    MyAdapter myAdapter;
    ArrayList<Plant> plants;

    /**
     * Called when the activity is created. Initializes the UI components and sets up event listeners.
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize UI components

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mailText = (TextView) findViewById(R.id.mailText);
        logoutButton = (Button) findViewById(R.id.logout_button);
        addButton = (Button) findViewById(R.id.add_button);

        // Set up RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();

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
                startActivity(new Intent(getApplicationContext(), WaterNFCActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if(item.getItemId()==R.id.bottom_account) {
                if(MyAdapter.notifsent>0){
                    MyAdapter.notifsent=1;
                }
                return true;
            }
            return false;
        });

        logoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                logout(v);
            }

        });

        // Set up event listeners
        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddPlantActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
            }
        });

        // Retrieve plant data from Firebase
        String userId = mFirebaseAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("plants");
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        plants = new ArrayList<>();
        myAdapter = new MyAdapter(this,plants);
        recyclerView.setAdapter(myAdapter);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    String dateAndFreq = dataSnapshot.child("lastWatered").getValue().toString();
                    String [] splitDates = dateAndFreq.split(",");
                    plant.setShort_description(splitDates[0]);
                    plant.setFrequency(Integer.parseInt(splitDates[1]));
                    plant.setImage(splitDates[2]);
                    //System.out.println(dataSnapshot.toString());
                    //System.out.println(plant.toString());
                    plants.add(plant);
                }
                if(plants.size()>0){
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * Called when the activity is starting or resuming. Displays the user account information.
     */
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

    /**
     * Logs out the current user and redirects to the login activity.
     * @param view The view that was clicked.
     */
    public void logout(View view){
        MainActivity.mFirebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
}