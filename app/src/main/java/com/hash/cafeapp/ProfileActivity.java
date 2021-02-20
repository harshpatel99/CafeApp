package com.hash.cafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<Profile> data;
    Toolbar toolbar;
    DatabaseReference databaseReference;
    ProfileRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        data = new ArrayList<>();

        data.add(new Profile(Profile.LOADING_TYPE));

        getData();

        adapter = new ProfileRecyclerAdapter(data,this);
        RecyclerView recyclerView = findViewById(R.id.profileRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getData() {

        databaseReference = FirebaseDatabase.getInstance().getReference();

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
