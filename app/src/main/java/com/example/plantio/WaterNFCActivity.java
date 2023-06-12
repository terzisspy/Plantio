package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class WaterNFCActivity extends AppCompatActivity {

    TextView instructionText,content;
    Button activateButton;
    Button readButton;
    EditText plantName,date;

    private FirebaseAuth mFirebaseAuth;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_nfcactivity);
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_water);

        activateButton = (Button) findViewById(R.id.writebutton);
        readButton = (Button) findViewById(R.id.readbutton);
        instructionText = (TextView) findViewById(R.id.textView);
        instructionText.setText("Enter the name and date and click I watered it or enter the name of the plant to see when it was watered");
        plantName = (EditText) findViewById(R.id.nameEditText);
        date = (EditText) findViewById(R.id.editTextDate);
        content =(TextView) findViewById(R.id.nfctextView);
        mFirebaseAuth = FirebaseAuth.getInstance();

        String userId = mFirebaseAuth.getCurrentUser().getUid();

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

        activateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (plantName.getText().toString().isEmpty() | date.getText().toString().isEmpty()){
                    CharSequence message = "You should fill both fields";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                    toast.show();
                }
                else{
                    if(isValidDate(date.getText().toString())){
                        String name = plantName.getText().toString();
                        mFirebaseAuth = FirebaseAuth.getInstance();
                        db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("plants");
                        String userId = mFirebaseAuth.getCurrentUser().getUid();
                        Query plantQuery = db.orderByChild("name").equalTo(name);
                        HashMap<String,Object> updateData = new HashMap<>();
                        plantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                                    // Update the water date of the plant from the database
                                    String dateAndFreq = plantSnapshot.child("lastWatered").getValue().toString();
                                    String [] splitDates = dateAndFreq.split(",");
                                    updateData.put("lastWatered",date.getText().toString()+","+splitDates[1]+","+splitDates[2]);
                                    plantSnapshot.getRef().updateChildren(updateData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
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
                                                    int duration = Toast.LENGTH_LONG;
                                                    Toast toast = Toast.makeText(getApplicationContext(), name+ " successfully watered.", duration);
                                                    toast.show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    int duration = Toast.LENGTH_LONG;
                                                    Toast toast = Toast.makeText(getApplicationContext(), name+ " failed to get watered.", duration);
                                                    toast.show();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });


                    }
                    else{
                        CharSequence message = "Enter a valid date dd/MM/yyyy";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                        toast.show();

                    }
                }
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plantName.getText().toString().isEmpty()) {
                    CharSequence message = "You should fill Name Field";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                    toast.show();
                } else {
                    String name = plantName.getText().toString();
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("plants");
                    String userId = mFirebaseAuth.getCurrentUser().getUid();
                    Query plantQuery = db.orderByChild("name").equalTo(name);

                    plantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                                // Display the water date
                                String dateAndFreq = plantSnapshot.child("lastWatered").getValue().toString();
                                String[] splitDates = dateAndFreq.split(",");
                                content.setText(splitDates[0]);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    public boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Disable lenient parsing

        try {
            Date date = sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
