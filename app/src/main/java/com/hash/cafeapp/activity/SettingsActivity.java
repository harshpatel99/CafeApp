package com.hash.cafeapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hash.cafeapp.R;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    RadioButton breakfast, lunch, dinner, selectedMenuRB;
    RadioGroup selectMenu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        breakfast = findViewById(R.id.settingMorningMenu);
        lunch = findViewById(R.id.settingNoonMenu);
        dinner = findViewById(R.id.settingEveningMenu);
        selectMenu = findViewById(R.id.settingSelectMenu);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("menu");
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String active = dataSnapshot.child("active").getValue(String.class);
                assert active != null;

                if (active.equals("breakfast")) {
                    breakfast.setChecked(true);
                } else if (active.equals("lunch")) {
                    lunch.setChecked(true);
                } else if (active.equals("dinner")) {
                    dinner.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        selectMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectedMenuRB = findViewById(radioGroup.getCheckedRadioButtonId());
                if (selectedMenuRB.getText().toString().equals("dinner")) {
                    FirebaseDatabase.getInstance().getReference("menu").child("active")
                            .setValue("dinner");
                }else{
                    FirebaseDatabase.getInstance().getReference("menu").child("active")
                            .setValue(selectedMenuRB.getText().toString().toLowerCase());
                }
                selectedMenuRB.getText();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
