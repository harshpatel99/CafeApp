package com.hash.cafeapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hash.cafeapp.R;

import java.util.Objects;

public class TransactionStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);

        Toolbar toolbar = findViewById(R.id.statusToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        int status = Objects.requireNonNull(getIntent().getExtras()).getInt("status");
        String text = Objects.requireNonNull(getIntent().getExtras()).getString("statusText");
        String subText = Objects.requireNonNull(getIntent().getExtras()).getString("statusSub");

        ImageView statusImage = findViewById(R.id.statusImageView);
        TextView statusText = findViewById(R.id.statusTextView);
        TextView statusSubText = findViewById(R.id.statusSubTextView);

        if(status == 1){
            statusImage.setImageResource(R.drawable.ic_done_outline_24px);
            statusImage.setColorFilter(ContextCompat.getColor(this, R.color.successful_color),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            statusText.setText(text);
            statusSubText.setText(subText);
        }else if(status == 2){
            statusImage.setImageResource(R.drawable.ic_cancel_24px);
            statusImage.setColorFilter(ContextCompat.getColor(this, R.color.unsuccessful_color),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            statusText.setText(text);
            statusSubText.setText(subText);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
