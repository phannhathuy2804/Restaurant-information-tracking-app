package com.example.cmpt276project.ui.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.model.Inspection;

import java.util.List;

//This adapter handles the setup of the RestaurantDetailsActivity recyclerView and handles formating for the date in the recyclerView
public class RestaurantDetailsAdapter extends RecyclerView.Adapter<RestaurantDetailsAdapter.ViewHolder> {

    private static final String TAG = "RestaurantDetailsAdapter";

    private List<Inspection> listInspection;
    Context context;

    public InspectionAdapterItemClickListener inspectionAdapterItemClickListener;

    public RestaurantDetailsAdapter(Context context, List<Inspection> listInspection, InspectionAdapterItemClickListener inspectionAdapterItemClickListener) {
        this.listInspection = listInspection;
        this.context = context;
        this.inspectionAdapterItemClickListener = inspectionAdapterItemClickListener;
    }

    @NonNull
    @Override
    public RestaurantDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_inspections, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailsAdapter.ViewHolder holder, final int position) {

        switch (listInspection.get(position).getHazardLevel()) {

            case LOW:
                holder.itemView.setBackgroundResource(R.drawable.list_bg_green);
                holder.image.setImageResource(R.drawable.led_green);
                break;

            case MODERATE:
                holder.itemView.setBackgroundResource(R.drawable.list_bg_yellow);
                holder.image.setImageResource(R.drawable.led_yellow);
                break;

            case HIGH:
                holder.itemView.setBackgroundResource(R.drawable.list_bg_red);
                holder.image.setImageResource(R.drawable.led_red);
                break;
        }

        holder.CritIssuesText.setText("" + listInspection.get(position).getNumOfCritical());
        holder.NonCritIssuesText.setText("" + listInspection.get(position).getNumOfNonCritical());
        holder.DateText.setText(listInspection.get(position).getInspectionDate().formatDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inspectionAdapterItemClickListener.onItemClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listInspection.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView CritIssuesText;
        TextView NonCritIssuesText;
        TextView DateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.hazardImageView);
            CritIssuesText = (TextView) itemView.findViewById(R.id.critIssuesText);
            NonCritIssuesText = (TextView) itemView.findViewById(R.id.nonCritIssuesText);
            DateText = (TextView) itemView.findViewById(R.id.inspectionDateText);
        }
    }
}