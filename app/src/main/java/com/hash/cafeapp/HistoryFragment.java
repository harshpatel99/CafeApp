package com.hash.cafeapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class HistoryFragment extends Fragment {

    private ArrayList<History> data;
    private HistoryRecyclerAdapter adapter;
    private ImageView loadingIV;
    private View noDataView;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        data = new ArrayList<>();

        noDataView = view.findViewById(R.id.historyNoData);
        loadingIV = view.findViewById(R.id.loadingIV);
        Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.loading_circles)
                .placeholder(R.drawable.rounded_rectangle)
                .into(loadingIV);

        adapter = new HistoryRecyclerAdapter(data, getContext());
        RecyclerView recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getData();

        return view;
    }

    private void getData() {

        SharedPreferences preferences = Objects.requireNonNull(getContext())
                .getSharedPreferences("userData", Context.MODE_PRIVATE);
        int userType = preferences.getInt("userType", User.CUSTOMER_TYPE);

        if (userType == User.CUSTOMER_TYPE) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("transactions");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        noDataView.setVisibility(View.GONE);
                        data.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            StringBuilder quantityName = new StringBuilder();

                            Integer status = snapshot.child("0").child("status")
                                    .getValue(Integer.class);
                            assert status != null;

                            int total = 0;
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                CartItem item = snap.getValue(CartItem.class);
                                assert item != null;
                                quantityName.append(item.getQuantity());
                                quantityName.append(" x ");
                                quantityName.append(item.getName());
                                quantityName.append("\n");

                                total += item.getQuantity() * Integer.parseInt(item.getPrice());
                            }
                            Date dateNonFormat = new Date(Long.parseLong(Objects
                                    .requireNonNull(snapshot.getKey())));
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yy  hh:mm a",
                                    Locale.US);
                            long dateTime = Long.parseLong(Objects.requireNonNull(snapshot.getKey()));
                            String date = formatter.format(dateNonFormat);
                            data.add(new History(String.valueOf(total), date, quantityName.toString(),
                                    "", status, dateTime, History.CARD_TYPE));
                        }
                        Collections.reverse(data);
                        adapter.notifyDataSetChanged();
                    } else {
                        data.clear();
                        adapter.notifyDataSetChanged();
                        noDataView.setVisibility(View.VISIBLE);
                    }
                    loadingIV.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (userType == User.STORE_TYPE) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("storeTransactions");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        noDataView.setVisibility(View.GONE);
                        data.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String email = snapshot.child("0").child("email")
                                    .getValue(String.class);
                            String from = snapshot.child("0").child("from")
                                    .getValue(String.class);
                            Integer status = snapshot.child("0").child("status")
                                    .getValue(Integer.class);
                            assert status != null;

                            StringBuilder quantityName = new StringBuilder();
                            int total = 0;
                            for (DataSnapshot snap : snapshot.getChildren()) {

                                CartItem item = snap.getValue(CartItem.class);
                                assert item != null;
                                quantityName.append(item.getQuantity());
                                quantityName.append(" x ");
                                quantityName.append(item.getName());
                                quantityName.append("\n");

                                total += item.getQuantity() * Integer.parseInt(item.getPrice());
                            }
                            Date dateNonFormat = new Date(Long.parseLong(Objects
                                    .requireNonNull(snapshot.getKey())));
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yy  hh:mm a",
                                    Locale.US);
                            String date = formatter.format(dateNonFormat);

                            long dateTime = Long.parseLong(Objects.requireNonNull(snapshot.getKey()));
                            data.add(new History(String.valueOf(total), date, quantityName.toString(),
                                    email, status, dateTime, from, History.CAFE_TYPE));
                        }
                        Collections.reverse(data);
                        adapter.notifyDataSetChanged();
                        loadingIV.setVisibility(View.GONE);
                    } else {
                        data.clear();
                        adapter.notifyDataSetChanged();
                        noDataView.setVisibility(View.VISIBLE);
                    }
                    loadingIV.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
