package com.example.plantio;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private Context context;
    ArrayList<Plant> plants;
    String light,highTemp,lowTemp;

    public SearchAdapter(Context context, ArrayList<Plant> plants) {
        this.context=context;
        this.plants=plants;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Plant plant=plants.get(position);
            holder.name.setText(plant.getName());
            holder.description.setText(plant.getShort_description());

            Glide.with(holder.img.getContext())
                    .load(plant.getImage())
                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                    .circleCrop()
                    .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark)
                    .into(holder.img);
    }

    @Override
    public int getItemCount(){
        return plants.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name,description;
        Button more;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            context = itemView.getContext();
            img = (CircleImageView)itemView.findViewById(R.id.img1);
            name = (TextView)itemView.findViewById(R.id.nametext);
            description = (TextView)itemView.findViewById(R.id.shortdescriptiontext);
            more = (Button)itemView.findViewById(R.id.morebutton);
            more.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    openDetailsActivity();
                }
            });

        }

        public void openDetailsActivity(){
            Intent intent = new Intent(context,Details.class);
            int position = getAdapterPosition();
            Plant plantdata = new Plant();
            plantdata = plants.get(position);
            intent.putExtra("planttag",plantdata);
            context.startActivity(intent);
        }

    }
}