package com.hash.cafeapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hash.cafeapp.R;
import com.hash.cafeapp.User;
import com.hash.cafeapp.adapters.PrivilegedUserAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class UserPrivilegeActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextInputEditText emailEditText;
    MaterialButton button;
    PrivilegedUserAdapter adapter;

    ArrayList<User> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_privilege);

        toolbar = findViewById(R.id.userPrivilegeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        emailEditText = findViewById(R.id.privilegeEmailEditText);
        button = findViewById(R.id.privilegeButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = Objects.requireNonNull(emailEditText.getText()).toString();
                if(TextUtils.isEmpty(email)){
                    emailEditText.setError("Enter E-mail ID");
                    return;
                }

                FirebaseDatabase.getInstance().getReference("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isAvailable = false;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String userEmail = snapshot.child("email").getValue(String.class);
                            Integer userType = snapshot.child("userType").getValue(Integer.class);
                            assert userEmail != null;
                            assert userType != null;

                            if(userEmail.equals(email)){
                                isAvailable = true;
                                if(userType == User.STORE_TYPE){
                                    Toast.makeText(UserPrivilegeActivity.this, email +
                                            " already have privileges", Toast.LENGTH_SHORT).show();
                                }else {
                                    snapshot.getRef().child("userType").setValue(User.STORE_TYPE);
                                    Toast.makeText(UserPrivilegeActivity.this,
                                            "User Type Successfully Changed", Toast.LENGTH_SHORT).show();
                                    emailEditText.getText().clear();
                                }
                            }
                        }
                        if(!isAvailable){
                            Toast.makeText(UserPrivilegeActivity.this,
                                    "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        data = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                if(user.getUserType() == User.STORE_TYPE){
                    data.add(user);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                if(user.getUserType() == User.STORE_TYPE){
                    data.add(user);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new PrivilegedUserAdapter(data,this);
        RecyclerView recyclerView = findViewById(R.id.privilegeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
