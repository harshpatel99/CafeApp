package com.hash.cafeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<CartItem> data;
    private Context context;

    CartRecyclerAdapter(ArrayList<CartItem> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {

            case Cart.ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_list, parent, false);
                return new ViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItem cart = data.get(position);
        if (cart != null) {
            switch (cart.getType()) {
                case Cart.ITEM_TYPE:
                    String amount = context.getResources().getString(R.string.rupee_symbol)
                            + cart.getPrice();
                    String quantity = "x " + cart.getQuantity();
                    ((ViewHolder) holder).amountTextView.setText(amount);
                    ((ViewHolder) holder).nameTextView.setText(cart.getName());
                    ((ViewHolder) holder).quantityTextView.setText(quantity);
                    Picasso.with(context).load(cart.getUrl()).fit()
                            .centerCrop().placeholder(R.drawable.rounded_rectangle)
                            .error(R.drawable.ic_outline_error_outline_24px)
                            .into(((ViewHolder) holder).itemImage);
                    //((ViewHolder) holder).itemImage.setText(cart.getDate());
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
                return Cart.HEADER_TYPE;
            case 1:
                return Cart.ITEM_TYPE;
            case 2:
                return Cart.TOTAL_TYPE;
            default:
                return -1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView amountTextView;
        TextView nameTextView;
        TextView quantityTextView;
        ImageView itemImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.cartItemPrice);
            nameTextView = itemView.findViewById(R.id.cartItemName);
            quantityTextView = itemView.findViewById(R.id.cartItemQuantity);
            itemImage = itemView.findViewById(R.id.cartItemImage);
        }

    }
}
