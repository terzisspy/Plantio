package com.example.plantio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class FindPlantsActivity extends AppCompatActivity {
    private TextView lightText,lowTemp,highTemp,TempIns,LightIns;
    private EditText highTempInput,lowTempInput;

    private SeekBar lightBar;
    private ImageView darknessimageView;

    Context context;
    private Button searchButton,detectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_plants);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);
        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
        bottomNavigationView.setOnItemSelectedListener(item->{
            if(item.getItemId()==R.id.bottom_home){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if(item.getItemId()==R.id.bottom_search){
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
                return true;
            }
            return false;
        });
        lightText = (TextView) findViewById(R.id.textView5);
        lowTemp = (TextView) findViewById(R.id.textView);
        highTemp = (TextView) findViewById(R.id.textView2);
        TempIns = (TextView) findViewById(R.id.textView4);
        LightIns = (TextView) findViewById(R.id.textView3);
        lowTempInput = (EditText) findViewById(R.id.editTextLowTemp);
        highTempInput = (EditText) findViewById(R.id.editTextHighTemp);
        lowTemp.setText(R.string.lowtemperature);
        highTemp.setText(R.string.hightemperature);
        TempIns.setText(R.string.temperature);
        LightIns.setText(R.string.instructions);
        context = this;

        lightBar = (SeekBar) findViewById(R.id.seekBarLight);
        darknessimageView = (ImageView) findViewById(R.id.darknessimageView);
        darknessimageView.setImageAlpha(0);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setText(getString(R.string.searchButtonText));
        detectButton = (Button) findViewById(R.id.detectButton);
        detectButton.setText(getString(R.string.detectButtonText));
        lightBar.setProgress(100);
        lightText.setText("100%");
        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lightText.setText("" + progress +"%");
                int alpha = 100-progress;
                Double newalpha = alpha*2.5;
                int normalpha = Integer.valueOf(newalpha.intValue());
                darknessimageView.setImageAlpha(normalpha);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String light = "";
                if(lightBar.getProgress()<50){
                    light = "Low";
                }
                else{
                    light="High";
                }
                if(!(lowTempInput.getText().toString().isEmpty()&&highTempInput.getText().toString().isEmpty())) {
                    String LowTemp = String.valueOf((lowTempInput.getText()));
                    String HighTemp = String.valueOf((highTempInput.getText()));
                    System.out.println(LowTemp);
                    System.out.println(HighTemp);
                    if (Integer.parseInt(LowTemp) >= Integer.parseInt(HighTemp)) {
                        CharSequence message = "The low temperature should be lower than the high temperature";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
                    }
                    else {
                        Intent intent = new Intent(context, SearchResults.class);
                        intent.putExtra("light", light);
                        intent.putExtra("lowTemp", LowTemp);
                        intent.putExtra("highTemp", HighTemp);
                        startActivity(intent);
                    }
                }
                else{
                    CharSequence message = "You should insert temperature values";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();
                }
            }
        });

        detectButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetectPlants.class);
                startActivity(intent);
            }
        }));

    }
}