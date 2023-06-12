package com.example.plantio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPlantActivity extends AppCompatActivity {
    Button addPlant;
    EditText nameText,dateText;
    String name;
    Date date;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        addPlant = (Button) findViewById(R.id.add_button);
        nameText = (EditText) findViewById(R.id.nameText);
        dateText = (EditText) findViewById(R.id.dateText);
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        //bottomNavigationView.setSelectedItemId(R.id.bottom_water);

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

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int frequency=7;
                String image=new String();
                String plantName =  nameText.getText().toString();
                String date = dateText.getText().toString();
                for(Plant plant: MainActivity.plants){
                    if(plant.getName().equals(plantName)){
                        frequency=plant.getFrequency();
                        image=plant.getImage();
                    }
                }

                if(image==null){
                    image = "https://drive.google.com/file/d/1YsvRA7ALpVDTEfwYzILl-LPdx9EV1RNd/view?usp=sharing";
                }

                Map<String, Object> plant = new HashMap<>();
                plant.put("name",plantName);
                plant.put("lastWatered",date+","+frequency+","+image);

                userRef.child("plants").push().setValue(plant);
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(), plantName+ " successfully added.", duration);
                toast.show();
            }
        });
    }
}