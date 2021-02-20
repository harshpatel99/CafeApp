package com.hash.cafeapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UpdateMenuRecyclerAdapter extends RecyclerView.Adapter {

    //private ArrayList<Menu> data;
    //private Context context;
    //

    private ArrayList<CategoryUpdate> data;
    private ArrayList<Menu> menus;
    private Context context;
    private RecyclerView.RecycledViewPool recycledViewPool;

    UpdateMenuRecyclerAdapter(ArrayList<CategoryUpdate> data, Context context) {
        this.data = data;
        this.context = context;

        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    /*UpdateMenuRecyclerAdapter(ArrayList<Menu> data, Context context) {
        this.data = data;
        this.context = context;
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {

            case CategoryUpdate.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_list_header, parent, false);
                return new UpdateMenuHeaderViewHolder(view);

            case CategoryUpdate.ADD_CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_list_add_category, parent, false);
                return new UpdateMenuAddCategoryViewHolder(view);

            case CategoryUpdate.CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list, parent, false);
                return new UpdateMenuCategoryViewHolder(view);

            case CategoryUpdate.ITEM_LIST_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_category_layout, parent, false);
                return new ItemViewHolder(view);

            /*case CategoryUpdate.ADD_ITEM_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_add_item_header, parent, false);
                return new UpdateMenuAddItemHeaderViewHolder(view);

            case CategoryUpdate.ADD_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_add_item_type, parent, false);
                return new UpdateMenuAddItemViewHolder(view);

            case CategoryUpdate.ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_item, parent, false);
                return new UpdateMenuItemTypeViewHolder(view);

            case CategoryUpdate.EMPTY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_empty, parent, false);
                return new UpdateMenuEmptyViewHolder(view);

            case CategoryUpdate.UPDATE_EXISTING_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_existing_item_type, parent, false);
                return new UpdateMenuExistingItemViewHolder(view);*/

            case CategoryUpdate.LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_loading, parent, false);
                return new LoadingViewHolder(view);

            case CategoryUpdate.NO_DATA_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_no_data, parent, false);
                return new NoDataViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryUpdate category = data.get(position);
        if (category != null) {
            switch (category.getType()) {

                case CategoryUpdate.ADD_CATEGORY_TYPE:
                    break;
                case CategoryUpdate.CATEGORY_TYPE:
                    ((UpdateMenuCategoryViewHolder) holder).name.setText(category.getCategoryName());
                    break;
                case CategoryUpdate.ITEM_LIST_TYPE:
                    /*ItemRVAdapter horizontalAdapter = new ItemRVAdapter(data.get(position)
                            .getItems(), context);
                    ((UpdateMenuRecyclerAdapter.ItemViewHolder) holder).recyclerViewHorizontal
                            .setAdapter(horizontalAdapter);
                    ((UpdateMenuRecyclerAdapter.ItemViewHolder) holder).recyclerViewHorizontal
                            .setRecycledViewPool(recycledViewPool);*/
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
                return CategoryUpdate.HEADER_TYPE;
            case 1:
                return CategoryUpdate.ADD_CATEGORY_TYPE;
            case 2:
                return CategoryUpdate.CATEGORY_TYPE;
            case 3:
                return CategoryUpdate.ITEM_LIST_TYPE;
            /*case 3:
                return CategoryUpdate.ADD_ITEM_TYPE_HEADER;
            case 4:
                return CategoryUpdate.ADD_ITEM_TYPE;
            case 5:
                return CategoryUpdate.ITEM_TYPE;
            case 6:
                return CategoryUpdate.EMPTY_TYPE;
            case 7:
                return CategoryUpdate.UPDATE_EXISTING_ITEM_TYPE;*/
            case 8:
                return CategoryUpdate.LOADING_TYPE;
            case 9:
                return CategoryUpdate.NO_DATA_TYPE;
            default:
                return -1;
        }
    }

    private Boolean addCategoryExpanded = true, addItemExpanded = true;

    class UpdateMenuHeaderViewHolder extends RecyclerView.ViewHolder {

        Chip addCategory;

        UpdateMenuHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            addCategory = itemView.findViewById(R.id.updateMenuListHeaderAddCategoryButton);

            addCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCategoryExpanded = !addCategoryExpanded;
                    if (!addCategoryExpanded) {
                        data.add(getLayoutPosition() + 1,
                                new CategoryUpdate("", new ArrayList<Menu>(),
                                        CategoryUpdate.ADD_CATEGORY_TYPE));
                        //new Menu("", "", "", "", true, Menu.ADD_CATEGORY_TYPE));
                        notifyItemInserted(getLayoutPosition() + 1);
                    } else {
                        data.remove(getLayoutPosition() + 1);
                        notifyItemRemoved(getLayoutPosition() + 1);
                    }
                }
            });
        }
    }

    class UpdateMenuCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView image;
        RelativeLayout layout;
        CardView cardView;

        Boolean expanded = true;

        UpdateMenuCategoryViewHolder(@NonNull View itemView) {
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

            if (!expanded) {

                /*if (!addItemExpanded)
                    addItemExpanded = true;*/

                menus = new ArrayList<>();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("menu");
                reference.keepSynced(true);
                reference.child(data.get(getLayoutPosition()).getCategoryName())
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Menu menu = dataSnapshot.getValue(Menu.class);
                                assert menu != null;
                                menu.setType(CategoryUpdate.ITEM_LIST_TYPE);
                                menus.add(menu);
                                notifyDataSetChanged();
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

                //menus.add(new Menu("Veg", "40", "", true, Menu.ITEM_TYPE));
                //menus.add(new Menu("Grilled", "50", "", true, Menu.ITEM_TYPE));
                //menus.add(new Menu("Veg Grilled", "60", "", true, Menu.ITEM_TYPE));
                //menus.add(new Menu("Veg American", "70", "", true, Menu.ITEM_TYPE));

                CategoryUpdate categories = new CategoryUpdate("",
                        menus, CategoryUpdate.ITEM_LIST_TYPE);
                data.add(getLayoutPosition() + 1, categories);
                notifyItemInserted(getLayoutPosition() + 1);


                /*Menu menu = new Menu("", "", "", true, Menu.ADD_ITEM_TYPE_HEADER);
                data.add(getLayoutPosition() + 1, menu);

                reference = FirebaseDatabase.getInstance().getReference();
                reference.child("menu").child(data.get(getLayoutPosition()).getCategory())
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                //data.remove(getAdapterPosition());
                                Menu menu = dataSnapshot.getValue(Menu.class);
                                data.add(getLayoutPosition()+2,menu);

                                notifyDataSetChanged();
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
            } else {

                int size = data.size();

                for (int j = getLayoutPosition()+1; j<size ; j++) {
                    Menu menu = data.get(j);
                    if (menu.getType() == Menu.CATEGORY_TYPE) {
                        break;
                    } else {
                        size--;
                        data.remove(menu);
                    }
                }
                data.remove(getLayoutPosition()+1);
                notifyDataSetChanged();

            }*/
            }

        }
    }

    class UpdateMenuAddCategoryViewHolder extends RecyclerView.ViewHolder {

        MaterialButton addCategory;
        TextInputEditText categoryNameEditText;
        TextInputEditText itemPriceEditText;
        TextInputEditText itemNameEditText;

        ChipGroup chipGroup;
        Chip morningChip, noonChip, eveningChip;

        boolean chipSelected = false;

        UpdateMenuAddCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameEditText = itemView.findViewById(R.id.addCategoryEditText);
            itemNameEditText = itemView.findViewById(R.id.addItemEditText);
            itemPriceEditText = itemView.findViewById(R.id.addPriceEditText);
            addCategory = itemView.findViewById(R.id.updateMenuListAddCategoryButton);

            chipGroup = itemView.findViewById(R.id.selectTimeChipGroup);
            morningChip = itemView.findViewById(R.id.morningChip);
            noonChip = itemView.findViewById(R.id.noonChip);
            eveningChip = itemView.findViewById(R.id.eveningChip);

            chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, int checkedId) {
                    Chip chip = group.findViewById(checkedId);
                    if (chip != null) {
                        for (int i = 0; i < group.getChildCount(); ++i) {
                            group.getChildAt(i).setClickable(true);
                        }
                        chip.setClickable(false);
                        chipSelected = true;
                        Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();
                    } else {
                        chipSelected = false;
                        Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            addCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String category = Objects.requireNonNull(categoryNameEditText.getText()).toString();
                    final String item = Objects.requireNonNull(itemNameEditText.getText()).toString();
                    final String price = Objects.requireNonNull(itemPriceEditText.getText()).toString();

                    if (TextUtils.isEmpty(category)) {
                        categoryNameEditText.setError("Required");
                        return;
                    }
                    if (TextUtils.isEmpty(item)) {
                        itemNameEditText.setError("Required");
                        return;
                    }
                    if (TextUtils.isEmpty(price)) {
                        itemPriceEditText.setError("Required");
                        return;
                    }
                    if (chipGroup.getCheckedChipId() != morningChip.getId() || chipGroup.getCheckedChipId() != noonChip.getId() ||
                            chipGroup.getCheckedChipId() != eveningChip.getId()) {
                        Toast.makeText(context, "Select time for item to be served!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.keepSynced(true);
                    reference.child("menu").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(category).exists()) {
                                categoryNameEditText.setError("");
                                Toast.makeText(context, "Category : " + category
                                        + " already exists", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Menu menu = new Menu(item, price, category, true, Menu.ITEM_TYPE);

                            String menuTime = "";
                            if(morningChip.isSelected()) menuTime = "morning";
                            else if(noonChip.isSelected()) menuTime = "noon";
                            else if(eveningChip.isSelected()) menuTime = "evening";

                            reference.child("menu").child(menuTime).child(category).push().setValue(menu);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show();
                    data.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    data.add(data.size(),
                            new CategoryUpdate(Objects.requireNonNull(categoryNameEditText.getText()).toString(),
                                    new ArrayList<Menu>(), CategoryUpdate.CATEGORY_TYPE));
                    notifyItemInserted(data.size());
                    addCategoryExpanded = !addCategoryExpanded;
                }
            });
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerViewHorizontal;

        LinearLayoutManager horizontalManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        public ItemViewHolder(View itemView) {
            super(itemView);

            recyclerViewHorizontal = itemView.findViewById(R.id.rv_category_item);
            recyclerViewHorizontal.setHasFixedSize(true);
            recyclerViewHorizontal.setNestedScrollingEnabled(false);
            recyclerViewHorizontal.setLayoutManager(horizontalManager);
            recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());

        }
    }

    /*class UpdateMenuAddItemHeaderViewHolder extends RecyclerView.ViewHolder {

        Chip addItem;

        UpdateMenuAddItemHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            addItem = itemView.findViewById(R.id.listAddItemAddChip);

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addItemExpanded = !addItemExpanded;
                    if (!addItemExpanded) {
                        data.add(getLayoutPosition() + 1,
                                new Menu("", "", "", data.get(getLayoutPosition() - 1).getCategory(), true, Menu.ADD_ITEM_TYPE));
                        notifyItemInserted(getLayoutPosition() + 1);
                    } else {
                        data.remove(getLayoutPosition() + 1);
                        notifyItemRemoved(getLayoutPosition() + 1);
                    }
                }
            });
        }
    }

    class UpdateMenuAddItemViewHolder extends RecyclerView.ViewHolder {

        MaterialButton addItem;
        TextInputEditText itemNameEditText, itemPriceEditText;

        UpdateMenuAddItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameEditText = itemView.findViewById(R.id.itemNameEditText);
            itemPriceEditText = itemView.findViewById(R.id.itemPriceEditText);
            addItem = itemView.findViewById(R.id.updateMenuListAddItemButton);

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String item = Objects.requireNonNull(itemNameEditText.getText()).toString();
                    final String price = Objects.requireNonNull(itemPriceEditText.getText()).toString();

                    if (TextUtils.isEmpty(item)) {
                        itemNameEditText.setError("Required");
                        return;
                    }
                    if (TextUtils.isEmpty(price)) {
                        itemPriceEditText.setError("Required");
                        return;
                    }

                    data.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Menu menu = new Menu(item, price, data.get(getLayoutPosition()).getCategory(), true, Menu.ITEM_TYPE);
                    reference.child("menu").child(data.get(getLayoutPosition()).getCategory()).push().setValue(menu);

                    Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show();
                    addItemExpanded = !addItemExpanded;
                }
            });
        }
    }

    class UpdateMenuItemTypeViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice;
        ImageView itemImage;

        UpdateMenuItemTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.categoryItemImage);
            itemName = itemView.findViewById(R.id.categoryItemTitle);
            itemPrice = itemView.findViewById(R.id.categoryItemPrice);
        }
    }

    class UpdateMenuExistingItemViewHolder extends RecyclerView.ViewHolder {

        MaterialButton addItem;
        TextInputEditText itemNameEditText, itemPriceEditText;

        UpdateMenuExistingItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameEditText = itemView.findViewById(R.id.existingItemNameEditText);
            itemPriceEditText = itemView.findViewById(R.id.existingItemPriceEditText);
            addItem = itemView.findViewById(R.id.updateExistingItemButton);

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Item Name : "
                                    + Objects.requireNonNull(itemNameEditText.getText()).toString()
                                    + "\nItem Price : "
                                    + Objects.requireNonNull(itemPriceEditText.getText()).toString()
                            , Toast.LENGTH_SHORT).show();
                    data.remove(getLayoutPosition());
                    data.add(getLayoutPosition(),
                            new Menu(itemNameEditText.getText().toString(), "",
                                    "$" + itemPriceEditText.getText().toString(),
                                    "", true, Menu.ITEM_TYPE));
                    notifyItemChanged(getLayoutPosition() + 1);
                    addItemExpanded = !addItemExpanded;
                }
            });
        }
    }

    class UpdateMenuEmptyViewHolder extends RecyclerView.ViewHolder {

        UpdateMenuEmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }*/

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class NoDataViewHolder extends RecyclerView.ViewHolder {

        NoDataViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    void updateAvailability(RecyclerView.ViewHolder viewHolder, int position, int dir) {
        /*if (viewHolder instanceof UpdateMenuItemTypeViewHolder) {
            Toast.makeText(context, "Availability Updated at " + position, Toast.LENGTH_SHORT).show();
            if (dir == 1) {
                /*Menu menu = data.get(position);
                data.remove(position);
                menu.setType(Menu.UPDATE_EXISTING_ITEM_TYPE);
                data.add(position, menu);
                //notifyItemRemoved(position);
                //notifyItemInserted(position);
                notifyItemChanged(position);*/
    }

    public Context getContext() {
        return context;
    }
}
