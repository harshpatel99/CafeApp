package com.hash.cafeapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

class ProfileRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<Profile> data;
    private Context context;

    ProfileRecyclerAdapter(ArrayList<Profile> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {

            case Profile.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_header, parent, false);
                return new ProfileRecyclerAdapter.HeaderViewHolder(view);

            case Profile.PROFILE_DATA_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_list, parent, false);
                return new ProfileRecyclerAdapter.ViewHolder(view);

            case Profile.EDIT_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_edit_data, parent, false);
                return new ProfileRecyclerAdapter.EditDataViewHolder(view);

            case Profile.LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_loading, parent, false);
                return new ProfileRecyclerAdapter.LoadingViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Profile profile = data.get(position);
        if (profile != null) {
            switch (profile.getType()) {
                case Profile.HEADER_TYPE:
                    ((HeaderViewHolder) holder).header.setText(profile.getName());

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(data.get(0).getName().substring(0,1), R.color.colorPrimary);
                    ((HeaderViewHolder) holder).profileImageView.setImageDrawable(drawable);
                    break;
                case Profile.PROFILE_DATA_TYPE:
                    ((ViewHolder) holder).title.setText(profile.getTitle());
                    ((ViewHolder) holder).profileData.setText(profile.getData());
                    if(position == 2 || position == 3){
                        ((ViewHolder) holder).edit.setVisibility(View.GONE);
                    }
                    ((ViewHolder) holder).edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            data.remove(position);
                            notifyItemRemoved(position);
                            data.add(position,new Profile(profile.getTitle(),profile.getData(),Profile.EDIT_TYPE));
                            notifyItemInserted(position);
                            //notifyItemChanged(position);
                        }
                    });
                    break;
                case Profile.EDIT_TYPE:
                    ((EditDataViewHolder) holder).editText.setHint(profile.getTitle());
                    ((EditDataViewHolder) holder).editText.setText(profile.getData());
                    ((EditDataViewHolder) holder).done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(TextUtils.isEmpty(Objects.requireNonNull(((EditDataViewHolder) holder)
                                    .editText.getText()).toString())){
                                ((EditDataViewHolder) holder).editText.setError("Required");
                                return;
                            }

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .child(position == 1 ? "name" : "compID")
                                    .setValue(Objects.requireNonNull(((EditDataViewHolder) holder)
                                            .editText.getText()).toString());

                            Profile p = data.get(position);
                            p.setType(Profile.PROFILE_DATA_TYPE);
                            p.setData(Objects.requireNonNull(((EditDataViewHolder) holder).editText.getText()).toString());
                            data.remove(position);
                            notifyItemRemoved(position);
                            data.add(position,p);
                            notifyItemInserted(position);

                            Toast.makeText(context,
                                    position == 1 ? "Success! Name Updated" : "Success! Company ID Updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case Profile.LOADING_TYPE:
                    Glide.with(Objects.requireNonNull(context)).load(R.drawable.loading_circles)
                            .placeholder(R.drawable.rounded_rectangle)
                            .into(((LoadingViewHolder) holder).imageView);
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
                return Profile.HEADER_TYPE;
            case 1:
                return Profile.PROFILE_DATA_TYPE;
            case 2:
                return Profile.EDIT_TYPE;
            case 3:
                return Profile.LOADING_TYPE;
            default:
                return -1;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        ImageView profileImageView;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.profileHeader);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView profileData;
        ImageView edit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.profileTitle);
            profileData = itemView.findViewById(R.id.profileData);
            edit = itemView.findViewById(R.id.profileEdit);
        }

    }

    class EditDataViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText editText;
        ImageView done;

        EditDataViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.profileEditText);
            done = itemView.findViewById(R.id.profileEditDone);
        }

    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.loadingIV);
        }

    }
}
