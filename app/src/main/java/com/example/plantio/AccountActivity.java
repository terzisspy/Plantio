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

public class AccountActivity extends AppCompatActivity {
    private TextView mailText;
    private Button logoutButton,addButton;
    private FirebaseAuth mFirebaseAuth;
    RecyclerView recyclerView;

    DatabaseReference db;
    MyAdapter myAdapter;
    ArrayList<Plant> plants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mailText = (TextView) findViewById(R.id.mailText);
        logoutButton = (Button) findViewById(R.id.logout_button);
        addButton = (Button) findViewById(R.id.add_button);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddPlantActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
            }
        });
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
                    plant.setShort_description(dataSnapshot.child("lastWatered").getValue().toString());
                    System.out.println(dataSnapshot.toString());
                    System.out.println(plant.toString());
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