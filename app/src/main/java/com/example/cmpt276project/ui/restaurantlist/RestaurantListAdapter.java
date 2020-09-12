package com.example.cmpt276project.ui.restaurantlist;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.databinding.ListItemRestaurantBinding;
import com.example.cmpt276project.model.DBAdapter;
import com.example.cmpt276project.model.DateAndTime;
import com.example.cmpt276project.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 *  RecyclerviewAdapter for RestaurantListFragment
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {

    private static final String TAG = "RestaurantListAdapter";

    private List<Restaurant> listRestaurant = new ArrayList<>();
    private ListItemRestaurantBinding binding;

    /**
     *  Listener for handling user clicks. Callback methods in RestaurantListFragment
     */
    private Listener listener;

    interface Listener{
        void onClick(int position);
        void onClickInsert(String resId, String name, String recent, String hazardLevel);
        void onClickDelete(String trackingNumber);
    }

    public void setListener(Listener listener){ this.listener = listener;}

    public RestaurantListAdapter(List<Restaurant> filteredRestaurantList){
//        this.myDb = myDb;
        this.listRestaurant = filteredRestaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.list_item_restaurant, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder at " + position);
        holder.itemView.setRestaurant(listRestaurant.get(position));
        holder.itemView.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });
        initialize_logo(position, holder);
        initialize_favorite(position, holder);

    }

    private void initialize_favorite(final int position, RestaurantListAdapter.ViewHolder holder) {

        final ImageButton love = (ImageButton) holder.itemView.favorite;
        final Restaurant restaurant = listRestaurant.get(position);

        Log.d(TAG, restaurant.getName() + " " + restaurant.isFavorite());


        if(restaurant.isFavorite()) {love.setImageResource(R.drawable.ic_love);}
        else {love.setImageResource(R.drawable.ic_action_name);}

        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!restaurant.isFavorite()) {

                    listRestaurant.get(position).setFavorite(true);
                    love.setImageResource(R.drawable.ic_love);
                    if(listener != null){
                        if(restaurant.getInspection().size() == 0)
                            listener.onClickInsert(restaurant.getTrackingNumber(), restaurant.getName(), "0", "");
                        else
                            listener.onClickInsert(restaurant.getTrackingNumber(), restaurant.getName(), restaurant.getTheMostRecentInspection().getInspectionDate().getDateString(DateAndTime.DATE_FORMAT_CSV_INPUT), restaurant.getHazardLevelColor());
                    }
                }
                else {

                    restaurant.setFavorite(false);
                    love.setImageResource((R.drawable.ic_action_name));
                    if(listener != null)
                        listener.onClickDelete(restaurant.getTrackingNumber());
                }

            }
        });
    }

    private void initialize_logo(int position, RestaurantListAdapter.ViewHolder holder) {
        ImageView icon = (ImageView) holder.itemView.itemImage;
        if (listRestaurant.get(position).getName().contains("7-Eleven"))
            icon.setImageResource(R.drawable.seven_eleven);
        else if (listRestaurant.get(position).getName().contains("Subway #") | listRestaurant.get(position).getName().contains("Subway ("))
            icon.setImageResource(R.drawable.subway);
        else if (listRestaurant.get(position).getName().contains("McDonald's"))
            icon.setImageResource(R.drawable.mcdonald);
        else if (listRestaurant.get(position).getName().contains("Tim Hortons"))
            icon.setImageResource(R.drawable.tim_hortons);
        else if (listRestaurant.get(position).getName().contains("Starbucks"))
            icon.setImageResource(R.drawable.starbucks);
        else if (listRestaurant.get(position).getName().contains("A&W")|listRestaurant.get(position).getName().contains("A & W"))
            icon.setImageResource(R.drawable.a_and_w);
        else if (listRestaurant.get(position).getName().contains("KFC"))
            icon.setImageResource(R.drawable.kfc);
        else if (listRestaurant.get(position).getName().contains("Dairy Queen"))
            icon.setImageResource(R.drawable.dq);
        else if (listRestaurant.get(position).getName().contains("Freshii"))
            icon.setImageResource(R.drawable.freshii);
        else if (listRestaurant.get(position).getName().contains("Wendy's"))
            icon.setImageResource(R.drawable.wendys);
        else
            icon.setImageResource(R.mipmap.restraunt_icon);

    }

    @Override
    public int getItemCount() {
        if(listRestaurant == null)
            return 0;
        return listRestaurant.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ListItemRestaurantBinding itemView;
        public ViewHolder(@NonNull ListItemRestaurantBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }

    @BindingAdapter("android:setHazardColor")
    public static void getRecentHazardLevelColor(LinearLayout linearLayout, Restaurant restaurant){
        if(restaurant.getTheMostRecentInspection() != null){
            switch (restaurant.getTheMostRecentInspection().getHazardLevel()){

                case LOW:
                    linearLayout.setBackgroundResource(R.drawable.list_bg_green);
                    break;

                case MODERATE:
                    linearLayout.setBackgroundResource(R.drawable.list_bg_yellow);
                    break;

                case HIGH:
                    linearLayout.setBackgroundResource(R.drawable.list_bg_red);
            }
        }
    }

    @BindingAdapter("android:showLastInspectionDate")
    //  Resource:  https://stackoverflow.com/questions/3838527/android-java-date-difference-in-days
    public static void setLastInspectionDate(TextView textView, String inspectionDate){
        // in this app, based on Canada time
        DateAndTime dateAndTime;

        if(inspectionDate.equals("0")){
            textView.setText(R.string.restaurant_list_activity_no_inspections_yet);
            return;
        }
        else if(inspectionDate.length() < 8){
            throw new IndexOutOfBoundsException("The inspection date format has to be yyyyMMdd");
        }
        else {
            dateAndTime = new DateAndTime(DateAndTime.DATE_FORMAT_CSV_INPUT, inspectionDate);
        }

        textView.setText(dateAndTime.formatDate());
    }
}
