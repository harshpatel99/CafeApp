package com.hash.cafeapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hash.cafeapp.activity.TransactionStatusActivity;
import com.hash.cafeapp.models.Checksum;
import com.hash.cafeapp.models.Paytm;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<History> data;
    private Context context;
    private LoadingDialog loadingDialog;
    private Paytm paytm;

    public HistoryRecyclerAdapter(ArrayList<History> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {

            case History.CARD_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_list_item, parent, false);
                return new ViewHolder(view);

            case History.CAFE_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_list_item_cafe, parent, false);
                return new CafeViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final History history = data.get(position);
        if (history != null) {
            switch (history.getType()) {
                case History.CARD_TYPE:
                    String amount = context.getResources().getString(R.string.rupee_symbol) + history.getAmount();
                    ((ViewHolder) holder).amountTextView.setText(amount);
                    ((ViewHolder) holder).itemsTextView.setText(history.getItems());
                    ((ViewHolder) holder).dateTextView.setText(history.getDate());

                    if (history.getStatus() == 1) {
                        ((ViewHolder) holder).statusTextView
                                .setText(context.getResources().getString(R.string.customer_order_placed));
                    } else if (history.getStatus() == 2) {
                        ((ViewHolder) holder).statusTextView
                                .setText(context.getResources().getString(R.string.customer_order_confirm));
                    } else if (history.getStatus() == 3) {
                        ((ViewHolder) holder).statusTextView
                                .setText(context.getResources().getString(R.string.customer_order_ready));
                    } else if (history.getStatus() == 4) {
                        ((ViewHolder) holder).statusTextView
                                .setText(context.getResources().getString(R.string.customer_order_successful));
                    }

                    ((ViewHolder) holder).repeatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle("Do you want to reorder?");
                            alertBuilder.setMessage(history.getItems());
                            alertBuilder.setPositiveButton(R.string.order, new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                public void onClick(DialogInterface dialog, int which) {

                                    FirebaseDatabase.getInstance().getReference("users/" +
                                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                                            + "/transactions").child(String.valueOf(history.getDateTime()))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    ArrayList<CartItem> data = new ArrayList<>();

                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        data.add(snapshot.getValue(CartItem.class));
                                                    }

                                                    int total = 0;
                                                    for (int i = 0; i < data.size(); i++) {
                                                        total += data.get(i).getQuantity() * Integer.parseInt(data.get(i).getPrice());
                                                    }
                                                    data.get(0).setStatus(1);

                                                    long date = new Date().getTime();

                                                    if (ContextCompat.checkSelfPermission(context,
                                                            Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions((MainActivity) context,
                                                                new String[]{Manifest.permission.READ_SMS,
                                                                        Manifest.permission.RECEIVE_SMS}, 101);
                                                    } else {
                                                        loadingDialog = new LoadingDialog((MainActivity) context);
                                                        loadingDialog.showDialog();
                                                        processPaymentByPaytm(String.valueOf(date),total,data);
                                                    }

                                                    /*long date = new Date().getTime();
                                                    FirebaseDatabase.getInstance().getReference("users/" +
                                                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                                                            + "/transactions").child(String.valueOf(date)).setValue(data);

                                                    FirebaseDatabase.getInstance().getReference("storeTransactions")
                                                            .child(String.valueOf(date)).setValue(data);

                                                    Toast.makeText(context, "Your order is placed!", Toast.LENGTH_SHORT).show();*/
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                }
                            });
                            alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        }
                    });

                    break;
                case History.CAFE_TYPE:
                    amount = context.getResources().getString(R.string.rupee_symbol) + history.getAmount();
                    ((CafeViewHolder) holder).amountTextView.setText(amount);
                    ((CafeViewHolder) holder).itemsTextView.setText(history.getItems());
                    ((CafeViewHolder) holder).dateTextView.setText(history.getDate());
                    ((CafeViewHolder) holder).orderedByTextView.setText(history.getEmail());

                    if (history.getStatus() == 1) {
                        ((CafeViewHolder) holder).orderStatusButton.setText(context.getResources().getString(R.string.confirm_order));
                    } else if (history.getStatus() == 2) {
                        ((CafeViewHolder) holder).orderStatusButton.setText(context.getResources().getString(R.string.order_ready));
                    } else if (history.getStatus() == 3) {
                        ((CafeViewHolder) holder).orderStatusButton.setText(context.getResources().getString(R.string.order_complete));
                    } else if (history.getStatus() == 4) {
                        ((CafeViewHolder) holder).orderStatusButton.setVisibility(View.GONE);
                    }

                    ((CafeViewHolder) holder).orderStatusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (history.getStatus() == 1) {
                                FirebaseDatabase.getInstance().getReference("storeTransactions")
                                        .child(String.valueOf(history.getDateTime()))
                                        .child("0").child("status").setValue(2);

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(history.getFrom())
                                        .child("transactions")
                                        .child(String.valueOf(history.getDateTime()))
                                        .child("0").child("status").setValue(2);

                                history.setStatus(2);
                                ((CafeViewHolder) holder).orderStatusButton
                                        .setText(context.getResources().getString(R.string.order_ready));
                            } else if (history.getStatus() == 2) {
                                FirebaseDatabase.getInstance().getReference("storeTransactions")
                                        .child(String.valueOf(history.getDateTime()))
                                        .child("0").child("status").setValue(3);

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(history.getFrom())
                                        .child("transactions")
                                        .child(String.valueOf(history.getDateTime()))
                                        .child("0").child("status").setValue(3);

                                history.setStatus(3);
                                ((CafeViewHolder) holder).orderStatusButton
                                        .setText(context.getResources().getString(R.string.order_complete));
                            } else if (history.getStatus() == 3) {
                                FirebaseDatabase.getInstance().getReference("storeTransactions")
                                        .child(String.valueOf(history.getDateTime()))
                                        .child("0").child("status").setValue(4);

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(history.getFrom())
                                        .child("transactions")
                                        .child(String.valueOf(history.getDateTime()))
                                        .child("0").child("status").setValue(4);

                                history.setStatus(4);
                                ((CafeViewHolder) holder).orderStatusButton.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (data.get(position).getType()) {
            case 0:
                return History.HEADER_TYPE;
            case 1:
                return History.CARD_TYPE;
            case 2:
                return History.CAFE_TYPE;
            default:
                return -1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView amountTextView;
        TextView dateTextView;
        TextView itemsTextView;
        TextView statusTextView;
        MaterialButton repeatButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.historyCardAmount);
            dateTextView = itemView.findViewById(R.id.historyCardDate);
            itemsTextView = itemView.findViewById(R.id.historyCardItems);
            repeatButton = itemView.findViewById(R.id.historyCardRepeatButton);
            statusTextView = itemView.findViewById(R.id.historyCardStatus);

        }

    }

    class CafeViewHolder extends RecyclerView.ViewHolder {

        TextView amountTextView;
        TextView dateTextView;
        TextView itemsTextView;
        TextView orderedByTextView;
        MaterialButton orderStatusButton;

        CafeViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.historyCardAmount);
            dateTextView = itemView.findViewById(R.id.historyCardDate);
            itemsTextView = itemView.findViewById(R.id.historyCardItems);
            orderedByTextView = itemView.findViewById(R.id.historyCardOrderedBy);
            orderStatusButton = itemView.findViewById(R.id.historyCardOrderStatusButton);
        }

    }

    public void processPaymentByPaytm(String orderID, int total, final ArrayList<CartItem> cartData) {

        paytm = new Paytm(
                "rxazcv89315285244163",
                orderID,
                FirebaseAuth.getInstance().getUid(),
                "WAP",
                String.valueOf(total),
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
                            processToPay(response.body().getChecksumHash(), paytm,cartData);
                        }
                    }

                    @Override
                    public void onFailure(Call<Checksum> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("ErrorPaytm", t.getMessage());
                    }
                });

    }

    private void processToPay(final String checksumHash, Paytm paytm,final ArrayList<CartItem> cartData) {
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

        Service.startPaymentTransaction(context, true,
                true, new PaytmPaymentTransactionCallback() {
                    public void someUIErrorOccurred(String inErrorMessage) {
                        loadingDialog.hideDialog();
                        Toast.makeText(context, "Some UI Error Occurred", Toast.LENGTH_SHORT).show();
                    }

                    public void onTransactionResponse(Bundle inResponse) {
                        loadingDialog.hideDialog();
                        verifyTransactionStatus(inResponse, checksumHash,cartData);

                    }

                    public void networkNotAvailable() {
                        loadingDialog.hideDialog();
                        Toast.makeText(context, "Network Not Available", Toast.LENGTH_SHORT).show();
                    }

                    public void clientAuthenticationFailed(String inErrorMessage) {
                        loadingDialog.hideDialog();
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }

                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        loadingDialog.hideDialog();
                        Toast.makeText(context, "Error Loading Payment Interface", Toast.LENGTH_SHORT).show();
                    }

                    public void onBackPressedCancelTransaction() {
                        loadingDialog.hideDialog();
                        Toast.makeText(context, "Order Canceled", Toast.LENGTH_SHORT).show();
                    }

                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        loadingDialog.hideDialog();
                        Toast.makeText(context, "Transaction Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void verifyTransactionStatus(Bundle inResponse, String checksum,ArrayList<CartItem> cartData) {

        String status = inResponse.getString("STATUS");
        String amt = inResponse.getString("TXNAMOUNT");

        assert status != null;
        if (status.equalsIgnoreCase("TXN_SUCCESS")) {
            Intent intent = new Intent(context, TransactionStatusActivity.class);
            intent.putExtra("status", 1);
            intent.putExtra("statusText", "Transaction Successful");
            intent.putExtra("statusSub", "Your order of "
                    + context.getResources().getString(R.string.rupee_symbol) + amt
                    + " was successfully completed");
            context.startActivity(intent);

            long date = new Date().getTime();
            FirebaseDatabase.getInstance().getReference("users/" +
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                    + "/transactions").child(String.valueOf(date)).setValue(cartData);

            FirebaseDatabase.getInstance().getReference("storeTransactions")
                    .child(String.valueOf(date)).setValue(cartData);

            Toast.makeText(context, "Your order is placed!", Toast.LENGTH_SHORT).show();

        } else {
            Intent intent = new Intent(context, TransactionStatusActivity.class);
            intent.putExtra("status", 2);
            intent.putExtra("statusText", "Transaction Unsuccessful");
            intent.putExtra("statusSub", "An error occurred while completing transaction.");
            context.startActivity(intent);
        }

    }

}
