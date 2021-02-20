package com.hash.cafeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.hash.cafeapp.activity.TransactionStatusActivity;
import com.hash.cafeapp.models.Checksum;
import com.hash.cafeapp.models.Paytm;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity {

    private ArrayList<CartItem> data;
    private CartRecyclerAdapter adapter;
    private TextView totalValueTextView;

    LoadingDialog loadingDialog;
    Paytm paytm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.cartToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        MaterialButton paymentButton;

        totalValueTextView = findViewById(R.id.cartTotalValue);
        paymentButton = findViewById(R.id.cartPaymentButton);

        data = new ArrayList<>();

        getData();

        adapter = new CartRecyclerAdapter(data, this);
        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CartActivity.this);
                builder.setTitle("Confirm Purchase");
                builder.setMessage("Proceed to pay " + totalValueTextView.getText().toString() + " for your items.");

                builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        if (data.size() != 0) {

                            long date = new Date().getTime();

                            if (ContextCompat.checkSelfPermission(CartActivity.this,
                                    Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(CartActivity.this,
                                        new String[]{Manifest.permission.READ_SMS,
                                                Manifest.permission.RECEIVE_SMS}, 101);
                            } else {
                                loadingDialog = new LoadingDialog(CartActivity.this);
                                loadingDialog.showDialog();
                                processPaymentByPaytm(String.valueOf(date));
                            }


                            /*data.get(0).setStatus(1);
                            data.get(0).setEmail(Objects.requireNonNull(FirebaseAuth.getInstance()
                                    .getCurrentUser()).getEmail());
                            data.get(0).setFrom(Objects.requireNonNull(FirebaseAuth.getInstance()
                                    .getCurrentUser()).getUid());

                            /****
                             FirebaseDatabase.getInstance().getReference("users/" +
                             Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                             + "/transactions").child(String.valueOf(date)).setValue(data);

                             FirebaseDatabase.getInstance().getReference("storeTransactions")
                             .child(String.valueOf(date)).setValue(data);

                             FirebaseDatabase.getInstance().getReference("cart")
                             .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                             .removeValue();

                             if (ContextCompat.checkSelfPermission(CartActivity.this,
                             Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                             ActivityCompat.requestPermissions(CartActivity.this,
                             new String[]{Manifest.permission.READ_SMS,
                             Manifest.permission.RECEIVE_SMS}, 101);
                             }

                             /*Toast.makeText(CartActivity.this, "Payment Successful",
                             Toast.LENGTH_SHORT).show();*//****

                             finish();*/
                        }
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

    private void getData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cart");
        reference.keepSynced(true);
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                        data.add(cartItem);
                        adapter.notifyDataSetChanged();

                        assert cartItem != null;
                        int totalPrice = Integer.parseInt(totalValueTextView.getText().toString().substring(1))
                                + (cartItem.getQuantity() * Integer.parseInt(cartItem.getPrice()));
                        String cartValue = getResources().getString(R.string.rupee_symbol)
                                + totalPrice;
                        totalValueTextView.setText(cartValue);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
    }

    public void processPaymentByPaytm(String orderID) {

        paytm = new Paytm(
                "rxazcv89315285244163",
                orderID,
                FirebaseAuth.getInstance().getUid(),
                "WAP",
                totalValueTextView.getText().toString().substring(1).trim(),
                "APPSTAGING",
                "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderID,
                "Retail"
        );

        WebServiceCaller.getClient().getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId())
                .enqueue(new Callback<Checksum>() {
                    @Override
                    public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                        if (response.isSuccessful()) {
                            processToPay(response.body().getChecksumHash(), paytm);
                        }
                    }

                    @Override
                    public void onFailure(Call<Checksum> call, Throwable t) {
                        Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("ErrorPaytm", t.getMessage());
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseDatabase.getInstance().getReference("cart")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .removeValue();

    }

    private void processToPay(final String checksumHash, Paytm paytm) {
        PaytmPGService Service = PaytmPGService.getStagingService();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", paytm.getmId());
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        //paramMap.put("EMAIL", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        //paramMap.put("MOBILE_NO", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);

        loadingDialog.hideDialog();

        Service.startPaymentTransaction(this, true,
                true, new PaytmPaymentTransactionCallback() {
                    public void someUIErrorOccurred(String inErrorMessage) {
                        loadingDialog.hideDialog();
                        Toast.makeText(CartActivity.this, "Some UI Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    public void onTransactionResponse(Bundle inResponse) {
                        loadingDialog.hideDialog();
                        verifyTransactionStatus(inResponse, checksumHash);

                    }

                    public void networkNotAvailable() {
                        loadingDialog.hideDialog();
                        Toast.makeText(CartActivity.this, "Network Not Available", Toast.LENGTH_SHORT).show();
                    }

                    public void clientAuthenticationFailed(String inErrorMessage) {
                        loadingDialog.hideDialog();
                        Toast.makeText(CartActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }

                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        loadingDialog.hideDialog();
                        Toast.makeText(CartActivity.this, "Error Loading Payment Interface", Toast.LENGTH_SHORT).show();
                    }

                    public void onBackPressedCancelTransaction() {
                        loadingDialog.hideDialog();
                        Toast.makeText(CartActivity.this, "Order Canceled", Toast.LENGTH_SHORT).show();
                    }

                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        loadingDialog.hideDialog();
                        Toast.makeText(CartActivity.this, "Transaction Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void verifyTransactionStatus(Bundle inResponse, String checksum) {

        String status = inResponse.getString("STATUS");
        String amt = inResponse.getString("TXNAMOUNT");

        /*String orderID = inResponse.getString("ORDERID");
        String checksumHash = inResponse.getString("CHECKSUMHASH");
        String mid = inResponse.getString("MID");
        String responseMsg = inResponse.getString("RESPMSG");

        if (orderID.equalsIgnoreCase(paytm.getOrderId()) &&
                checksumHash.equalsIgnoreCase(checksum) &&
                mid.equalsIgnoreCase(paytm.getmId())/* &&
                amt.equalsIgnoreCase(paytm.getTxnAmount().concat(".00")*//*) {*/
        assert status != null;
        if (status.equalsIgnoreCase("TXN_SUCCESS")) {
            Intent intent = new Intent(this, TransactionStatusActivity.class);
            intent.putExtra("status", 1);
            intent.putExtra("statusText", "Transaction Successful");
            intent.putExtra("statusSub", "Your order of "
                    + getResources().getString(R.string.rupee_symbol) + amt
                    + " was successfully completed");
            startActivity(intent);

            data.get(0).setStatus(1);
            data.get(0).setEmail(Objects.requireNonNull(FirebaseAuth.getInstance()
                    .getCurrentUser()).getEmail());
            data.get(0).setFrom(Objects.requireNonNull(FirebaseAuth.getInstance()
                    .getCurrentUser()).getUid());

            FirebaseDatabase.getInstance().getReference("users/" +
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                    + "/transactions").child(paytm.getOrderId()).setValue(data);

            FirebaseDatabase.getInstance().getReference("storeTransactions")
                    .child(paytm.getOrderId()).setValue(data);

            FirebaseDatabase.getInstance().getReference("cart")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .removeValue();

            finish();
        } else {
            Intent intent = new Intent(this, TransactionStatusActivity.class);
            intent.putExtra("status", 2);
            intent.putExtra("statusText", "Transaction Unsuccessful");
            intent.putExtra("statusSub", "An error occurred while completing transaction.");
            startActivity(intent);
            finish();
        }

    }

}
