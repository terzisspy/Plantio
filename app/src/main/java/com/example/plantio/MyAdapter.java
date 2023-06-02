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

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends FirebaseRecyclerAdapter<Plant,MyAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public MyAdapter(@NonNull FirebaseRecyclerOptions<Plant> options) {
        super(options);
    }

    Plant plantdata = new Plant();

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Plant plant) {
        //if(plant.getLow_temperature()<=10&&plant.getHigh_temperature()<=20) {
            holder.name.setText(plant.getName());
            holder.description.setText(plant.getShort_description());
            plantdata = plant;

            Glide.with(holder.img.getContext())
                    .load(plant.getImage())
                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                    .circleCrop()
                    .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark)
                    .into(holder.img);
        //}
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);

        return new myViewHolder(view);
    }
    private Context context;



    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name,description;
        Button more;

        public myViewHolder(@NonNull View itemView){
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
            intent.putExtra("planttag",plantdata);
            context.startActivity(intent);
        }

    }
}