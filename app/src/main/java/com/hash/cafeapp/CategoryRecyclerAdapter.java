package com.hash.cafeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<Categories> data;
    private Context context;

    CategoryRecyclerAdapter(ArrayList<Categories> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {

            case Categories.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_header, parent, false);
                return new HeaderViewHolder(view);

            case Categories.CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list, parent, false);
                return new CategoryViewHolder(view);

            case Categories.ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_item, parent, false);
                return new CategoryItemViewHolder(view);

            case Categories.EMPTY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_empty, parent, false);
                return new CategoryEmptyViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Categories category = data.get(position);
        if (category != null) {
            switch (category.getType()) {
                case Categories.HEADER_TYPE:
                    ((HeaderViewHolder) holder).header.setText(category.getName());
                    break;
                case Categories.CATEGORY_TYPE:
                    ((CategoryViewHolder) holder).name.setText(category.getName());
                    break;
                case Categories.ITEM_TYPE:
                    ((CategoryItemViewHolder) holder).itemName.setText(category.getName());
                    ((CategoryItemViewHolder) holder).itemPrice.setText(category.getPrice());
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
                return Categories.HEADER_TYPE;
            case 1:
                return Categories.CATEGORY_TYPE;
            case 2:
                return Categories.ITEM_TYPE;
            case 3:
                return Categories.EMPTY_TYPE;
            default:
                return -1;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView header;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.categoryHeaderTitle);

        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView image;
        RelativeLayout layout;
        CardView cardView;

        Boolean expanded = true;
        int i = 2;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            //image = itemView.findViewById(R.id.categoryImage);
            name = itemView.findViewById(R.id.categoryTitle);
            layout = itemView.findViewById(R.id.categoryRelativeLayout);
            //cardView = itemView.findViewById(R.id.categoryCardView);

        }

        @Override
        public void onClick(View view) {

            expanded = !expanded;
            /*TransitionManager.beginDelayedTransition(cardView, new TransitionSet()
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeImageTransform()));*/

            ViewGroup.LayoutParams params = image.getLayoutParams();
            if (!expanded) {
                /*params.height = (int) convertDpToPx(context,50);
                params.width = (int) convertDpToPx(context,50);
                image.setLayoutParams(params);*/

                Categories categories = new Categories("Grilled Sandwich", "","$80", Categories.ITEM_TYPE);
                data.add(i, categories);
                notifyItemInserted(i++);
                categories = new Categories("Vegetable Sandwich", "","$60", Categories.ITEM_TYPE);
                data.add(i, categories);
                notifyItemInserted(i++);
                categories = new Categories("Cheese Sandwich", "","$90", Categories.ITEM_TYPE);
                data.add(i, categories);
                notifyItemInserted(i);


            } else {
                /*params.height = (int) convertDpToPx(context,120);
                params.width = (int) convertDpToPx(context,120);
                image.setLayoutParams(params);*/

                data.remove(i);
                notifyItemRemoved(i--);
                data.remove(i);
                notifyItemRemoved(i--);
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    class CategoryItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice, minusTextView, countTextView, addTextView;
        ImageView itemImage;
        Boolean count = false;
        int value = 1;

        CategoryItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.categoryItemImage);
            itemName = itemView.findViewById(R.id.categoryItemTitle);
            itemPrice = itemView.findViewById(R.id.categoryItemPrice);

            minusTextView = itemView.findViewById(R.id.categoryItemMinus);
            countTextView = itemView.findViewById(R.id.categoryItemCount);
            addTextView = itemView.findViewById(R.id.categoryItemAdd);

            countTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!count) {
                        if (minusTextView.getVisibility() == View.GONE || addTextView.getVisibility() == View.GONE) {
                            minusTextView.setVisibility(View.VISIBLE);
                            addTextView.setVisibility(View.VISIBLE);
                            countTextView.setText("1");
                        }
                    }
                }
            });

            addTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (value < 5)
                        countTextView.setText(String.valueOf(++value));
                    else
                        Toast.makeText(context, "Only 5 items at a time", Toast.LENGTH_SHORT).show();
                }
            });

            minusTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (value > 1)
                        countTextView.setText(String.valueOf(--value));
                    else {
                        minusTextView.setVisibility(View.GONE);
                        addTextView.setVisibility(View.GONE);
                        countTextView.setText("+");
                    }
                }
            });

        }
    }

    class CategoryEmptyViewHolder extends RecyclerView.ViewHolder {

        CategoryEmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
