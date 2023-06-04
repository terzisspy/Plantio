package com.example.plantio;

import static androidx.core.content.ContextCompat.startActivity;

import static java.lang.Math.abs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.isradeleon.notify.Notify;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    ArrayList<Plant> plants;
    String light,highTemp,lowTemp;
    public static int notifsent=0;

    public MyAdapter(Context context, ArrayList<Plant> plants) {
        this.context=context;
        this.plants=plants;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acccard_item,parent,false);
        return new MyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Plant plant=plants.get(position);
        holder.name.setText(plant.getName());
        holder.date.setText(plant.getShort_description());
        int whenToWater = plant.getFrequency()-plant.daysInBetween();
        String waterWarning = new String();
        if(whenToWater==-1){
            waterWarning = "You should have watered it yesterday";
            holder.again.setTypeface(null, Typeface.BOLD);
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




    class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name,date,again;
        Button more;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            context = itemView.getContext();
            img = (CircleImageView)itemView.findViewById(R.id.img1);
            name = (TextView)itemView.findViewById(R.id.nametext);
            date = (TextView)itemView.findViewById(R.id.dateWater);
            again = (TextView)itemView.findViewById(R.id.shouldbeWatered);
        }

    }
}