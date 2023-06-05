package com.example.plantio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.example.plantio.ml.Model;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetectPlants extends AppCompatActivity {

    RecyclerView recyclerViewer;
    TextView result, classified;
    ImageView imageView;
    Button picture, gallery;
    int imageSize = 224;

    DatabaseReference db;
    ArrayList<Plant> plants;

    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_plants);

        result = (TextView) findViewById(R.id.result);
        classified = (TextView) findViewById(R.id.classified);
        classified.setText("Take or Upload a pic of a plant");
        imageView = (ImageView) findViewById(R.id.imageView);
        picture = (Button) findViewById(R.id.picture);
        gallery = (Button) findViewById(R.id.gallery);
        recyclerViewer = (RecyclerView) findViewById(R.id.recyclerView);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch gallery by starting a new activity
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 3);

            }
        });

        FirebaseUser currentUser = MainActivity.mFirebaseAuth.getCurrentUser();
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

        if(MyAdapter.notifsent>0){
            MyAdapter.notifsent=1;
        }
    }

    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {"Rose", "Tulip", "Lavender", "Snake Plant", "ZZ Plant", "Peace Lily", "Cast Iron Plant", "Sunflower", "Orchid", "Chinese Evergreen"};
            String plantName = classes[maxPos];
            result.setText(plantName);

            /* String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }*/
            classified.setText(String.format("%s %.1f%% %s", "It's more likely (", confidences[maxPos] * 100, ") to be:"));

            db = FirebaseDatabase.getInstance().getReference("Plant");
            recyclerViewer.setLayoutManager(new LinearLayoutManager(this));
            plants = new ArrayList<>();
            searchAdapter = new SearchAdapter(this,plants);
            recyclerViewer.setAdapter(searchAdapter);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Plant plant = dataSnapshot.getValue(Plant.class);
                        System.out.println(plant.getName()+" "+plantName);
                        //System.out.println(plant.getHigh_temperature()+" "+highTempText);
                        //System.out.println(plant.getLow_temperature()+" "+lowTempText);
                        if(plant.getName().equals(plantName))
                            plants.add(plant);
                        //System.out.println(plants);
                    }
                    if(plants.size()>0){
                        searchAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap image;
                image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}