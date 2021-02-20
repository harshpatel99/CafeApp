package com.hash.cafeapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hash.cafeapp.CartActivity;
import com.hash.cafeapp.LoadingDialog;
import com.hash.cafeapp.R;
import com.hash.cafeapp.models.ContactUsForm;

import java.util.Date;
import java.util.Objects;

public class ContactUsActivity extends AppCompatActivity {

    MaterialButton nextButton;
    TextInputEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Toolbar toolbar = findViewById(R.id.contactUsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        editText = findViewById(R.id.descProblemEditText);
        nextButton = findViewById(R.id.contactUsNextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(Objects.requireNonNull(editText.getText()).toString())){
                    editText.setError(getResources().getString(R.string.contact_us_text));
                    return;
                }

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ContactUsActivity.this);
                builder.setTitle("Submit Message");

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        ContactUsForm form = new ContactUsForm(Objects.requireNonNull(FirebaseAuth
                                .getInstance().getCurrentUser()).getEmail(),
                                editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("contact")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .child(String.valueOf(new Date().getTime()))
                                .setValue(form);

                        editText.getText().clear();
                        Snackbar.make(nextButton,"Message Submitted Successfully",Snackbar.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
