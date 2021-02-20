package com.hash.cafeapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.hash.cafeapp.adapters.SelectImageRVAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryRVAdapter extends RecyclerView.Adapter {

    private ArrayList<CategoryData> data;
    private Context context;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int userType;
    private Boolean addCategoryExpanded = true;
    private ItemRVAdapter horizontalAdapter;
    String menuTime;

    CategoryRVAdapter(ArrayList<CategoryData> data, int userType, Context context) {
        this.data = data;
        this.context = context;
        this.userType = userType;

        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case CategoryData.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_header, parent, false);
                return new HeaderViewHolder(view);

            case CategoryData.CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list, parent, false);
                return new CategoryViewHolder(view);

            case CategoryData.ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_category_layout, parent, false);
                return new ItemViewHolder(view);

            case CategoryData.EMPTY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_empty, parent, false);
                return new CategoryEmptyViewHolder(view);

            case CategoryData.HEADER_ADD_CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_list_header, parent, false);
                return new HeaderAddCategoryViewHolder(view);

            case CategoryData.ADD_CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_list_add_category, parent, false);
                return new AddCategoryViewHolder(view);

            case CategoryData.MENU_TIME_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_menu_time, parent, false);
                return new MenuTimeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryData category = data.get(position);
        if (category != null) {
            switch (category.getType()) {
                case CategoryData.HEADER_TYPE:
                    ((HeaderViewHolder) holder).header.setText(category.getCategoryName());
                    break;
                case CategoryData.CATEGORY_TYPE:
                    ((CategoryViewHolder) holder).name.setText(category.getCategoryName());
                    break;

                case CategoryData.ITEM_TYPE:
                    String menuTime = category.getItems().get(0).getMenuTime();
                    if (userType == User.STORE_TYPE) {
                        horizontalAdapter = new ItemRVAdapter(category.getItems(),
                                category.getCategoryName(),menuTime, User.STORE_TYPE, context);
                    } else
                        horizontalAdapter = new ItemRVAdapter(category.getItems(),
                                category.getCategoryName(),menuTime, User.CUSTOMER_TYPE, context);
                    ((ItemViewHolder) holder).recyclerViewHorizontal.setAdapter(horizontalAdapter);
                    ((ItemViewHolder) holder).recyclerViewHorizontal.setRecycledViewPool(recycledViewPool);
                    break;
                case CategoryData.MENU_TIME_TYPE:
                    ((MenuTimeViewHolder) holder).header.setText(category.getCategoryName());
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
                return CategoryData.HEADER_TYPE;
            case 1:
                return CategoryData.CATEGORY_TYPE;
            case 2:
                return CategoryData.ITEM_TYPE;
            case 3:
                return CategoryData.HEADER_ADD_CATEGORY_TYPE;
            case 4:
                return CategoryData.ADD_CATEGORY_TYPE;
            case 5:
                return CategoryData.EMPTY_TYPE;
            case 6:
                return CategoryData.MENU_TIME_TYPE;
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

    class MenuTimeViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        MenuTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.menuTimeHeaderTitle);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerViewHorizontal;

        LinearLayoutManager horizontalManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        ItemViewHolder(View itemView) {
            super(itemView);

            recyclerViewHorizontal = itemView.findViewById(R.id.rv_category_item);
            recyclerViewHorizontal.setNestedScrollingEnabled(false);
            recyclerViewHorizontal.setLayoutManager(horizontalManager);
            recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());

        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RelativeLayout layout;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.categoryTitle);
            layout = itemView.findViewById(R.id.categoryRelativeLayout);

        }
    }

    class HeaderAddCategoryViewHolder extends RecyclerView.ViewHolder {

        Chip addCategory;

        HeaderAddCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            addCategory = itemView.findViewById(R.id.updateMenuListHeaderAddCategoryButton);

            addCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCategoryExpanded = !addCategoryExpanded;
                    if (!addCategoryExpanded) {
                        //addCategory.setText(context.getResources().getString(R.string.cancel));
                        data.add(getLayoutPosition() + 1,
                                new CategoryData("", new ArrayList<Menu>(),
                                        CategoryData.ADD_CATEGORY_TYPE));
                        notifyItemInserted(getLayoutPosition() + 1);
                    } else {
                        //addCategory.setText(context.getResources().getString(R.string.add_category));
                        data.remove(getLayoutPosition() + 1);
                        notifyItemRemoved(getLayoutPosition() + 1);
                    }
                }
            });
        }
    }

    class AddCategoryViewHolder extends RecyclerView.ViewHolder {

        MaterialButton addCategory, browseImage;
        TextInputEditText categoryNameEditText;
        TextInputEditText itemPriceEditText;
        TextInputEditText itemNameEditText;
        ImageView itemImage;

        ChipGroup chipGroup;
        Chip morningChip,noonChip,eveningChip;

        boolean chipSelected = false;

        AddCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameEditText = itemView.findViewById(R.id.addCategoryEditText);
            itemNameEditText = itemView.findViewById(R.id.addItemEditText);
            itemPriceEditText = itemView.findViewById(R.id.addPriceEditText);
            addCategory = itemView.findViewById(R.id.updateMenuListAddCategoryButton);
            browseImage = itemView.findViewById(R.id.itemImageBrowseImage);
            itemImage = itemView.findViewById(R.id.itemImageSelectIV);

            chipGroup = itemView.findViewById(R.id.selectTimeChipGroup);
            morningChip = itemView.findViewById(R.id.morningChip);
            noonChip = itemView.findViewById(R.id.noonChip);
            eveningChip = itemView.findViewById(R.id.eveningChip);

            chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, int checkedId) {
                    Chip chip = group.findViewById(checkedId);
                    if(chip != null){
                        chipSelected = true;
                    }else{
                        chipSelected = false;
                    }
                }
            });


            browseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_select_image);

                    final ArrayList<ImageData> imageData = new ArrayList<>();

                    Chip uploadChip = dialog.findViewById(R.id.uploadFoodImageChip);
                    uploadChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            ((UpdateMenuActivity) context).startActivityForResult(Intent.createChooser(
                                    intent, "Select Picture"),UpdateMenuActivity.PICK_IMAGE_REQUEST);
                        }
                    });

                    final SelectImageRVAdapter adapter = new SelectImageRVAdapter(imageData, dialog, itemImage, context);
                    RecyclerView recyclerView = dialog.findViewById(R.id.selectImageRecyclerView);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
                    recyclerView.setAdapter(adapter);

                    FirebaseDatabase.getInstance().getReference("images").child("food")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    imageData.add(dataSnapshot.getValue(ImageData.class));
                                    adapter.notifyDataSetChanged();
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

                    dialog.show();
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
                    if (itemImage.getVisibility() == View.GONE) {
                        Toast.makeText(context, "Item Image Required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!chipSelected){
                        Toast.makeText(context, "Select time for item to be served!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(morningChip.isChecked()) menuTime = "breakfast";
                    else if(noonChip.isChecked()) menuTime = "lunch";
                    else if(eveningChip.isChecked()) menuTime = "nightdinner";

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child("menu").child(menuTime).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(category).exists()) {
                                categoryNameEditText.setError("Already exists");
                                Toast.makeText(context, "Category : " + category
                                        + " already exists", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Menu menu = new Menu(item, itemImage.getTag().toString(), price,
                                    category, true, Menu.ITEM_TYPE);

                            reference.child("menu").child(menuTime).child(category).push().setValue(menu);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show();
                    data.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());

                    categoryNameEditText.getText().clear();
                    itemNameEditText.getText().clear();
                    itemPriceEditText.getText().clear();
                    chipGroup.clearCheck();
                    itemImage.setVisibility(View.GONE);

                    addCategoryExpanded = !addCategoryExpanded;
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
