package com.hash.cafeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ArrayList<Profile> data;
    private ProfileRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        data = new ArrayList<>();

        data.add(new Profile(Profile.LOADING_TYPE));

        getData();

        adapter = new ProfileRecyclerAdapter(data,getContext());
        RecyclerView recyclerView = view.findViewById(R.id.profileRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getData() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        data.add(new Profile(dataSnapshot.child("name")
                                .getValue(String.class),Profile.HEADER_TYPE));
                        data.add(new Profile("Name",dataSnapshot.child("name")
                                .getValue(String.class),Profile.PROFILE_DATA_TYPE));
                        data.add(new Profile("Email",dataSnapshot.child("email")
                                .getValue(String.class),Profile.PROFILE_DATA_TYPE));
                        data.add(new Profile("Mobile",dataSnapshot.child("phone")
                                .getValue(String.class),Profile.PROFILE_DATA_TYPE));
                        data.add(new Profile("Company ID",dataSnapshot.child("compID")
                                .getValue(String.class),Profile.PROFILE_DATA_TYPE));

                        data.remove(0);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
