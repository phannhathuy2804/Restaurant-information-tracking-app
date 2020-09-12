package com.example.cmpt276project.ui.restaurantlist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.databinding.FragmentRestaurantListBinding;
import com.example.cmpt276project.model.DBAdapter;
import com.example.cmpt276project.model.Restaurant;

import java.util.List;

/**
 *  RestaurantListFragment
 *  Displays the restaurant brief info in recyclerview
 */
public class RestaurantListFragment extends Fragment {

    private static final String TAG = RestaurantListFragment.class.getSimpleName();

    private FragmentRestaurantListBinding binding;
    private RestaurantListAdapter adapter;
    DBAdapter myDb;

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    /**
     *  Return RestaurantListFragment
     *  Use this to create an instance of RestaurantListFragment
     */
    public static RestaurantListFragment newInstance() {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DBAdapter(getContext());
        myDb.open();
        Log.w("TAG","DATABASE OPENED");

    }

    @Override
    public void onStop() {
        super.onStop();
        myDb.close();
        Log.w("TAG","DATABASE CLOSED");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpRecyclerView();
    }


    /**
     *  Set up RecyclerView
     */
    private void setUpRecyclerView(){
        List<Restaurant> filteredRestaurantList = ((RestaurantsListActivity)getActivity()).getFilteredRestaurantList();
        Log.d(TAG, "setUpRecyclerView size == " + filteredRestaurantList.size());
        adapter = new RestaurantListAdapter(filteredRestaurantList);
        adapter.setListener(listener);

//        SnappingLinearLayoutManager linearLayoutManager = new SnappingLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycleview_divier));

        binding.listRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listRecyclerView.setAdapter(adapter);
    }

    /**
     *  Handle user click events on each item inside RecyclerView
     *  onClick -> when a whole area of item was clicked
     */
    private RestaurantListAdapter.Listener listener = new RestaurantListAdapter.Listener() {

        @Override
        //start RestaurantDetailsActivity based on the position of item clicked
        public void onClick(int position) {
            binding.listRecyclerView.smoothScrollToPosition(position);
            ((RestaurantsListActivity)getActivity()).moveToRestaurantDetailActivity(position);
        }

        @Override
        public void onClickInsert(String resId, String name, String recent, String hazardLevel) {
            ((RestaurantsListActivity)getActivity()).insertDatabase(resId, name, recent, hazardLevel);
        }

        @Override
        public void onClickDelete(String trackingNumber) {
            ((RestaurantsListActivity)getActivity()).deleteFavouriteRestaurant(trackingNumber);
        }
    };
}

