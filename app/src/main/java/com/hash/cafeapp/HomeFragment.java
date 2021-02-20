package com.hash.cafeapp;


import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private ArrayList<CategoryData> data;
    private CategoryRVAdapter categoryRVAdapter;
    private ExtendedFloatingActionButton floatingActionButton;
    private ImageView loadingIV;

    String timeOfMenu;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        data = new ArrayList<>();

        TypedArray styleAttributes = Objects.requireNonNull(getContext()).getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styleAttributes.getDimension(0, 0);
        styleAttributes.recycle();

        loadingIV = view.findViewById(R.id.loadingIV);
        Glide.with(Objects.requireNonNull(getContext()))
                .load(R.drawable.loading_circles)
                .placeholder(R.drawable.rounded_rectangle)
                .into(loadingIV);

        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(convertDpToPx(getContext(), 16),
                convertDpToPx(getContext(), 16),
                convertDpToPx(getContext(), 16),
                convertDpToPx(getContext(), 16) + actionBarSize);
        params.gravity = Gravity.END | Gravity.BOTTOM;

        floatingActionButton = view.findViewById(R.id.fab_home_proceed);
        floatingActionButton.setLayoutParams(params);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CartActivity.class));
                FragmentTransaction transaction = Objects.requireNonNull(getActivity())
                        .getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(R.id.frame_container, new HomeFragment());
                transaction.commit();
            }
        });

        getData();

        categoryRVAdapter = new CategoryRVAdapter(data, User.CUSTOMER_TYPE, getContext());
        RecyclerView recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryRVAdapter);

        return view;
    }

    private void getData() {

        data.add(new CategoryData("Menu",
                new ArrayList<Menu>(), CategoryData.HEADER_TYPE));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cart");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Objects
                        .requireNonNull(FirebaseAuth.getInstance().getUid()))) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("menu");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (data.size() > 1) {
                    data.subList(1, data.size()).clear();
                }

                timeOfMenu = dataSnapshot.child("active").getValue(String.class);
                if (Objects.requireNonNull(timeOfMenu).equals("dinner"))
                    timeOfMenu = "nightdinner";


                for (DataSnapshot snap : dataSnapshot.child(timeOfMenu).getChildren()) {
                    ArrayList<Menu> menus = new ArrayList<>();
                    for (DataSnapshot snapshot : snap.getChildren())
                        menus.add(snapshot.getValue(Menu.class));
                    data.add(new CategoryData(snap.getKey(), menus, CategoryData.CATEGORY_TYPE));
                    data.add(new CategoryData(snap.getKey(), menus, CategoryData.ITEM_TYPE));
                }
                data.add(new CategoryData("Empty", CategoryData.EMPTY_TYPE));
                categoryRVAdapter.notifyDataSetChanged();
                loadingIV.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference("menu");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (data.size() > 1) {
                    data.subList(1, data.size()).clear();
                }

                String menuTime = dataSnapshot.child("active").getValue(String.class);
                if (Objects.requireNonNull(menuTime).equals("dinner")) menuTime = "nightdinner";


                for (DataSnapshot snap : dataSnapshot.child(menuTime).getChildren()) {
                    ArrayList<Menu> menus = new ArrayList<>();
                    for (DataSnapshot snapshot : snap.getChildren())
                        menus.add(snapshot.getValue(Menu.class));
                    data.add(new CategoryData(snap.getKey(), menus, CategoryData.CATEGORY_TYPE));
                    data.add(new CategoryData(snap.getKey(), menus, CategoryData.ITEM_TYPE));
                }
                data.add(new CategoryData("Empty", CategoryData.EMPTY_TYPE));
                categoryRVAdapter.notifyDataSetChanged();
                loadingIV.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    private int convertDpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

}
