package com.hash.cafeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hash.cafeapp.R;
import com.hash.cafeapp.User;

import java.util.ArrayList;

public class PrivilegedUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<User> data;
    private Context context;

    public PrivilegedUserAdapter(ArrayList<User> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_privileged_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = data.get(position);
        ((ViewHolder) holder).name.setText(user.getName());
        ((ViewHolder) holder).email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.privilegedNameTV);
            email = itemView.findViewById(R.id.privilegedEmailTV);

        }
    }
}
