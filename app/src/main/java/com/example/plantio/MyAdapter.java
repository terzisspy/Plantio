package com.example.plantio;

import static androidx.core.content.ContextCompat.startActivity;

import static java.lang.Math.abs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.isradeleon.notify.Notify;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    ArrayList<Plant> plants;
    String light,highTemp,lowTemp;
    public static int notifsent=0;

    private FirebaseAuth mFirebaseAuth;

    DatabaseReference db;

    public MyAdapter(Context context, ArrayList<Plant> plants) {
        this.context=context;
        this.plants=plants;
    }

    @NonNull
    @Override
    // Inflate the item layout for the RecyclerView
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acccard_item,parent,false);
        return new MyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Plant plant=plants.get(position);
        holder.name.setText(plant.getName());
        holder.date.setText(plant.getShort_description());

        // Calculate when to water the plant and display a warning message

        int whenToWater = plant.getFrequency()-plant.daysInBetween();
        String waterWarning = new String();
        if(whenToWater==-1){
            waterWarning = "You should have watered it yesterday";
            holder.again.setTypeface(null, Typeface.BOLD);
            // Send a notification if not already sent
            if(notifsent!=1)
                Notify.build(context)
                    .setTitle("You forgot to water your " + plant.getName())
                    .setContent("You should have watered it yesterday")
                    .setSmallIcon(R.drawable.flower)
                    .setLargeIcon(plant.getImage())
                    .largeCircularIcon()
                    .show();
        }
        else if(whenToWater==0){
            waterWarning = "You should water it today";
            holder.again.setTypeface(null, Typeface.BOLD);
            // Send a notification if not already sent
            if(notifsent!=1)
                Notify.build(context)
                    .setTitle("Remember to water your " + plant.getName())
                    .setContent("You should water it today")
                    .setSmallIcon(R.drawable.flower)
                    .setLargeIcon(plant.getImage())
                    .largeCircularIcon()
                    .show();
        }
        else if(whenToWater==1){
            waterWarning = "You should water it tomorrow";
            holder.again.setTypeface(null, Typeface.BOLD);
            // Send a notification if not already sent
            if(notifsent!=1)
                Notify.build(context)
                    .setTitle("Remember to water your " + plant.getName())
                    .setContent("You should water it tomorrow")
                    .setSmallIcon(R.drawable.flower)
                    .setLargeIcon(plant.getImage())
                    .largeCircularIcon()
                    .show();
        }
        else if(whenToWater<0){
            waterWarning = "You should have watered it: "+abs(whenToWater)+" days ago";
            holder.again.setTypeface(null, Typeface.BOLD);
        }
        else{
            waterWarning = "You should water it in: " + abs(whenToWater) + " days";
        }
        holder.again.setText(waterWarning);
        System.out.println(plant.daysInBetween());

        Glide.with(holder.img.getContext())
                .load(plant.getImage())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(R.drawable.flower)
                .into(holder.img);
        if(notifsent!=1)
            notifsent+=5;
    }

    @Override
    public int getItemCount(){
        return plants.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView name, date, again;
        Button remove, water;

        public MyViewHolder(@NonNull View itemView) {

            // Initialize UI Components
            super(itemView);
            context = itemView.getContext();
            img = (CircleImageView) itemView.findViewById(R.id.img1);
            name = (TextView) itemView.findViewById(R.id.nametext);
            date = (TextView) itemView.findViewById(R.id.dateWater);
            again = (TextView) itemView.findViewById(R.id.shouldbeWatered);
            remove = (Button) itemView.findViewById(R.id.removeButton);
            water = (Button) itemView.findViewById(R.id.waterButton);

            // Remove button click listener
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete the plant from the database
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    String userId = mFirebaseAuth.getCurrentUser().getUid();
                    int position = getAdapterPosition();
                    db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("plants");
                    String nameToBeDeleted = plants.get(position).getName();
                    Query plantQuery = db.orderByChild("name").equalTo(nameToBeDeleted);
                    plantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                                // Delete the plant from the database
                                plantSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(context,AccountActivity.class);
                                                context.startActivity(intent);
                                                int duration = Toast.LENGTH_LONG;
                                                Toast toast = Toast.makeText(context, nameToBeDeleted+ " successfully deleted.", duration);
                                                toast.show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                int duration = Toast.LENGTH_LONG;
                                                Toast toast = Toast.makeText(context, nameToBeDeleted+ " failed to get deleted.", duration);
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

            });

            // Water button click listener
            water.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    String userId = mFirebaseAuth.getCurrentUser().getUid();
                    int position = getAdapterPosition();
                    String nameToBeUpdated = plants.get(position).getName();

                    //Updated data with the calculation of current date
                    Calendar calendarToday = Calendar.getInstance();
                    calendarToday.set(Calendar.HOUR_OF_DAY, 0);
                    calendarToday.set(Calendar.MINUTE, 0);
                    calendarToday.set(Calendar.SECOND, 0);
                    calendarToday.set(Calendar.MILLISECOND, 0);
                    Date today = calendarToday.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String newDate = sdf.format(today);
                    HashMap<String,Object> updateData = new HashMap<>();

                    db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("plants");
                    String nameToBeDeleted = plants.get(position).getName();
                    Query plantQuery = db.orderByChild("name").equalTo(nameToBeDeleted);
                    plantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                                // Update the water date of the plant from the database
                                String dateAndFreq = plantSnapshot.child("lastWatered").getValue().toString();
                                String [] splitDates = dateAndFreq.split(",");
                                updateData.put("lastWatered",newDate+","+splitDates[1]+","+splitDates[2]);
                                plantSnapshot.getRef().updateChildren(updateData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(context,AccountActivity.class);
                                                context.startActivity(intent);
                                                int duration = Toast.LENGTH_LONG;
                                                Toast toast = Toast.makeText(context, nameToBeUpdated+ " successfully watered.", duration);
                                                toast.show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                int duration = Toast.LENGTH_LONG;
                                                Toast toast = Toast.makeText(context, nameToBeUpdated+ " failed to get watered.", duration);
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
            });
        }
    }
}