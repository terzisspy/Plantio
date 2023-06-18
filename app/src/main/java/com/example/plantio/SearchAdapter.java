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

    /**
     * Constructor for the SearchAdapter class.
     *
     * @param context The context of the activity or fragment.
     * @param plants  The list of plants to be displayed.
     */
    public SearchAdapter(Context context, ArrayList<Plant> plants) {
        this.context=context;
        this.plants=plants;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
        return new MyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Plant plant=plants.get(position);
            holder.name.setText(plant.getName());
            holder.description.setText(plant.getShort_description());

            Glide.with(holder.img.getContext())
                    .load(plant.getImage())
                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                    .circleCrop()
                    .error(R.drawable.flower)
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
        /**
         * Constructor for the MyViewHolder class.
         *
         * @param itemView The item view containing the views to be held by the ViewHolder.
         */
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
        // Opens the Details activity and passes the plant data to it.
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