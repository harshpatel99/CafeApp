package com.hash.cafeapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hash.cafeapp.adapters.SelectImageRVAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ItemRVAdapter extends RecyclerView.Adapter {

    private ArrayList<Menu> data;
    private Context context;
    private String category;
    private String menuTime;
    private int userType;

    private Menu forExisting;
    private ArrayList<ImageData> imageData;

    ItemRVAdapter(ArrayList<Menu> data, String category,String menuTime, int userType, Context context) {
        this.data = data;
        this.context = context;
        this.category = category;
        this.userType = userType;
        this.menuTime = menuTime;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {

            case Menu.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_header, parent, false);
                return new HeaderViewHolder(view);

            case Menu.ITEM_TYPE:
                if (userType == User.STORE_TYPE) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_editable_item_type, parent, false);
                    return new ItemEditableViewHolder(view);
                } else {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_type, parent, false);
                    return new ItemViewHolder(view);
                }

            case Menu.HEADER_ADD_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_header_add_item_type, parent, false);
                return new HeaderAddItemViewHolder(view);

            case Menu.ADD_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_add_item_type, parent, false);
                return new AddItemViewHolder(view);

            case Menu.UPDATE_EXISTING_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_menu_existing_item_type, parent, false);
                return new ExistingItemViewHolder(view);

            case Menu.UNAVAILABLE:
                if (userType == User.STORE_TYPE) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_editable_item_type, parent, false);
                    return new ItemEditableViewHolder(view);
                } else {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_type_unavailable, parent, false);
                    return new UnavailableItemViewHolder(view);
                }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Menu item = data.get(position);
        if (item != null) {
            switch (item.getType()) {
                case Menu.HEADER_TYPE:
                    ((HeaderViewHolder) holder).header.setText(item.getName());
                    break;
                case Menu.ITEM_TYPE:
                    if (userType == User.STORE_TYPE) {
                        ((ItemEditableViewHolder) holder).itemName.setText(item.getName());
                        String price = "₹ " + item.getPrice();
                        ((ItemEditableViewHolder) holder).itemPrice.setText(price);
                        Picasso.with(context).load(item.getUrl()).fit()
                                .centerCrop().placeholder(R.drawable.rounded_rectangle)
                                .error(R.drawable.ic_outline_error_outline_24px)
                                .into(((ItemEditableViewHolder) holder).itemImage);

                        ((ItemEditableViewHolder) holder).deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
                                builder.setTitle("Confirm Delete");
                                builder.setMessage("Are you sure deleting this item ? This can't be undone.");

                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        reference.keepSynced(true);
                                        reference.child("menu").child(menuTime).child(category).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Menu menuFromDB = snapshot.getValue(Menu.class);
                                                    assert menuFromDB != null;
                                                    String fromDB = menuFromDB.getName();
                                                    if (fromDB.equals(data.get(position).getName())) {
                                                        data.remove(position);
                                                        notifyItemRemoved(position);
                                                        snapshot.getRef().removeValue();
                                                        Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                }
                                                dialogInterface.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
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

                    } else {
                        ((ItemViewHolder) holder).itemName.setText(item.getName());
                        String price = "₹ " + item.getPrice();
                        ((ItemViewHolder) holder).itemPrice.setText(price);
                        Picasso.with(context).load(item.getUrl()).fit()
                                .centerCrop().placeholder(R.drawable.rounded_rectangle)
                                .error(R.drawable.ic_outline_error_outline_24px)
                                .into(((ItemViewHolder) holder).itemImage);
                    }
                    break;
                case Menu.UPDATE_EXISTING_ITEM_TYPE:
                    ((ExistingItemViewHolder) holder).itemNameEditText.setText(item.getName());
                    ((ExistingItemViewHolder) holder).itemPriceEditText.setText(item.getPrice());
                    break;
                case Menu.ADD_ITEM_TYPE:
                    ((AddItemViewHolder) holder).addItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final String item = Objects.requireNonNull(((AddItemViewHolder) holder).itemNameEditText.getText()).toString();
                            final String price = Objects.requireNonNull(((AddItemViewHolder) holder).itemPriceEditText.getText()).toString();

                            if (TextUtils.isEmpty(item)) {
                                ((AddItemViewHolder) holder).itemNameEditText.setError("Required");
                                return;
                            }
                            if (TextUtils.isEmpty(price)) {
                                ((AddItemViewHolder) holder).itemPriceEditText.setError("Required");
                                return;
                            }
                            if (((AddItemViewHolder) holder).itemImage.getVisibility() == View.GONE) {
                                Toast.makeText(context, "Item Image Required", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            Menu menu = new Menu(item, ((AddItemViewHolder) holder).itemImage.getTag().toString(), price, category,
                                    true, Menu.ITEM_TYPE);

                            reference.child("menu").child(menuTime).child(category).push().setValue(menu);

                            Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show();

                            data.remove(0);
                            data.add(0, new Menu("", "", "", true,
                                    Menu.HEADER_ADD_ITEM_TYPE));
                            notifyItemChanged(0);
                        }
                    });
                    break;
                case Menu.UNAVAILABLE:
                    if (userType == User.STORE_TYPE) {
                        ((ItemEditableViewHolder) holder).itemName.setText(item.getName());
                        String price = "₹ " + item.getPrice();
                        ((ItemEditableViewHolder) holder).itemPrice.setText(price);
                        ((ItemEditableViewHolder) holder).cardView.setCardBackgroundColor(context
                                .getResources().getColor(R.color.unavailableColor));
                        Picasso.with(context).load(item.getUrl()).fit()
                                .centerCrop().placeholder(R.drawable.rounded_rectangle)
                                .error(R.drawable.ic_outline_error_outline_24px)
                                .into(((ItemEditableViewHolder) holder).itemImage);

                    } else {
                        ((UnavailableItemViewHolder) holder).itemName.setText(item.getName());
                        String price = "₹ " + item.getPrice();
                        ((UnavailableItemViewHolder) holder).itemPrice.setText(price);
                        Picasso.with(context).load(item.getUrl()).fit()
                                .centerCrop().placeholder(R.drawable.rounded_rectangle)
                                .error(R.drawable.ic_outline_error_outline_24px)
                                .into(((UnavailableItemViewHolder) holder).itemImage);
                    }
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
                return Menu.HEADER_TYPE;
            case 1:
                return Menu.ITEM_TYPE;
            case 2:
                return Menu.HEADER_ADD_ITEM_TYPE;
            case 3:
                return Menu.ADD_ITEM_TYPE;
            case 4:
                return Menu.UPDATE_EXISTING_ITEM_TYPE;
            case 5:
                return Menu.UNAVAILABLE;
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

    class UnavailableItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice;
        ImageView itemImage;

        UnavailableItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.categoryItemImage);
            itemName = itemView.findViewById(R.id.categoryItemTitle);
            itemPrice = itemView.findViewById(R.id.categoryItemPrice);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice, minusTextView, countTextView, addTextView;
        LinearLayout layout;
        ImageView itemImage;
        Boolean count = false;
        int value = 1;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.categoryItemImage);
            itemName = itemView.findViewById(R.id.categoryItemTitle);
            itemPrice = itemView.findViewById(R.id.categoryItemPrice);
            layout = itemView.findViewById(R.id.categoryItemLinearLayout);

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

                            CartItem cartItem = new CartItem();
                            cartItem.setCategory(data.get(getLayoutPosition()).getCategory());
                            cartItem.setName(data.get(getLayoutPosition()).getName());
                            cartItem.setPrice(data.get(getLayoutPosition()).getPrice());
                            cartItem.setUrl(data.get(getLayoutPosition()).getUrl());
                            cartItem.setQuantity(1);
                            cartItem.setType(CartItem.ITEM_TYPE);

                            String itemID = category + data.get(getLayoutPosition()).getName();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("cart")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .child(itemID).setValue(cartItem);

                            LinearLayout.LayoutParams layoutParams =
                                    (LinearLayout.LayoutParams) layout.getLayoutParams();
                            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                            layout.setLayoutParams(layoutParams);
                        }
                    }
                }
            });

            addTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (value < 5) {
                        countTextView.setText(String.valueOf(++value));

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child("cart")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String itemID = category + data.get(getLayoutPosition()).getName();
                                        if (dataSnapshot.hasChild(itemID)) {
                                            Integer intCount = dataSnapshot.child(itemID).child("quantity")
                                                    .getValue(Integer.class);
                                            assert intCount != null;
                                            int itemQuantity = ++intCount;

                                            dataSnapshot.getRef().child(itemID).child("quantity")
                                                    .setValue(itemQuantity);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    } else
                        Toast.makeText(context, "Only 5 items at a time", Toast.LENGTH_SHORT).show();
                }
            });

            minusTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (value > 0) {
                        if ((value - 1) != 0)
                            countTextView.setText(String.valueOf(--value));
                        else {
                            minusTextView.setVisibility(View.GONE);
                            addTextView.setVisibility(View.GONE);
                            countTextView.setText("+");
                            LinearLayout.LayoutParams layoutParams =
                                    (LinearLayout.LayoutParams) layout.getLayoutParams();
                            layoutParams.gravity = Gravity.START;
                            layout.setLayoutParams(layoutParams);
                        }

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child("cart")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String itemID = category + data.get(getLayoutPosition()).getName();
                                        if (dataSnapshot.hasChild(itemID)) {
                                            Integer intCount = dataSnapshot.child(itemID).child("quantity")
                                                    .getValue(Integer.class);
                                            assert intCount != null;
                                            int itemCount = --intCount;

                                            if (itemCount == 0)
                                                dataSnapshot.getRef().child(itemID).removeValue();
                                            else
                                                dataSnapshot.getRef().child(itemID).child("quantity")
                                                        .setValue(itemCount);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    } /*else {
                        minusTextView.setVisibility(View.GONE);
                        addTextView.setVisibility(View.GONE);
                        countTextView.setText("+");
                        LinearLayout.LayoutParams layoutParams =
                                (LinearLayout.LayoutParams) layout.getLayoutParams();
                        layoutParams.gravity = Gravity.START;
                        layout.setLayoutParams(layoutParams);
                    }*/
                }
            });
        }
    }

    class ItemEditableViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemPrice;
        MaterialCardView cardView;
        LinearLayout layout;
        ImageView itemImage, editButton, deleteButton, availabilityToggle;

        ItemEditableViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.categoryItemImage);
            itemName = itemView.findViewById(R.id.categoryItemTitle);
            itemPrice = itemView.findViewById(R.id.categoryItemPrice);
            layout = itemView.findViewById(R.id.categoryItemLinearLayout);
            cardView = itemView.findViewById(R.id.cardEditableItem);

            editButton = itemView.findViewById(R.id.itemEditTextView);
            deleteButton = itemView.findViewById(R.id.itemDeleteTextView);
            availabilityToggle = itemView.findViewById(R.id.itemAvailabilityTextView);

            availabilityToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
                    int cardColor = cardView.getCardBackgroundColor().getDefaultColor();
                    if (cardColor == context.getResources().getColor(R.color.unavailableColor)) {
                        builder.setTitle("Make it Available ? ");
                        builder.setMessage("Customers will be able to buy this item now.");
                    } else {
                        builder.setTitle("Make it Unavailable ? ");
                        builder.setMessage("Customers no longer be able to buy this item.");
                    }

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("menu").child(menuTime).child(category).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Menu menuFromDB = snapshot.getValue(Menu.class);
                                        assert menuFromDB != null;
                                        String fromDB = menuFromDB.getName();
                                        menuFromDB.setAvailability(!menuFromDB.getAvailability());
                                        menuFromDB.setType(menuFromDB.getAvailability() ? 1 : 5);
                                        if (fromDB.equals(data.get(getLayoutPosition()).getName())) {
                                            if (menuFromDB.getAvailability())
                                                cardView.setCardBackgroundColor(context.getResources()
                                                        .getColor(android.R.color.white));
                                            else
                                                cardView.setCardBackgroundColor(context.getResources()
                                                        .getColor(android.R.color.darker_gray));
                                            Toast.makeText(context, "Now item is not available to customers", Toast.LENGTH_SHORT).show();
                                            snapshot.getRef().setValue(menuFromDB);
                                            break;
                                        }
                                    }
                                    dialogInterface.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    forExisting = data.get(getLayoutPosition());
                    data.remove(getLayoutPosition());
                    data.add(getLayoutPosition(),
                            new Menu("", "", "", category,
                                    true, Menu.UPDATE_EXISTING_ITEM_TYPE));
                    notifyItemChanged(getLayoutPosition());
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("Confirm Delete");
                    builder.setMessage("Are you sure deleting this item ? This can't be undone.");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.keepSynced(true);
                            reference.child("menu").child(menuTime).child(category).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Menu menuFromDB = snapshot.getValue(Menu.class);
                                        assert menuFromDB != null;
                                        String fromDB = menuFromDB.getName();
                                        if (fromDB.equals(data.get(getLayoutPosition()).getName())) {
                                            data.remove(getLayoutPosition());
                                            notifyItemRemoved(getLayoutPosition());
                                            snapshot.getRef().removeValue();
                                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                    dialogInterface.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
    }

    class HeaderAddItemViewHolder extends RecyclerView.ViewHolder {

        TextView addItem;

        HeaderAddItemViewHolder(@NonNull View itemView) {
            super(itemView);
            addItem = itemView.findViewById(R.id.headerAddItemTextView);

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.remove(0);
                    data.add(0,
                            new Menu( category,menuTime,
                                    true, Menu.ADD_ITEM_TYPE));
                    notifyItemChanged(0);
                }
            });
        }
    }

    class AddItemViewHolder extends RecyclerView.ViewHolder {

        MaterialButton addItem, cancel, browseImage;
        TextInputEditText itemNameEditText, itemPriceEditText;
        ImageView itemImage;

        AddItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameEditText = itemView.findViewById(R.id.itemNameEditText);
            itemPriceEditText = itemView.findViewById(R.id.itemPriceEditText);
            addItem = itemView.findViewById(R.id.updateMenuListAddItemButton);
            cancel = itemView.findViewById(R.id.updateMenuListCancelButton);
            browseImage = itemView.findViewById(R.id.itemImageBrowseImage);
            itemImage = itemView.findViewById(R.id.itemImageSelectIV);

            browseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_select_image);

                    Chip uploadChip = dialog.findViewById(R.id.uploadFoodImageChip);
                    uploadChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            ((UpdateMenuActivity) context).startActivityForResult(Intent.createChooser(
                                    intent, "Select Picture"), UpdateMenuActivity.PICK_IMAGE_REQUEST);
                        }
                    });

                    imageData = new ArrayList<>();

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

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.remove(0);
                    data.add(0,
                            new Menu("", "", "", category,
                                    true, Menu.HEADER_ADD_ITEM_TYPE));
                    notifyItemChanged(0);
                }
            });

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
                    if (itemImage.getVisibility() == View.GONE) {
                        Toast.makeText(context, "Item Image Required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Menu menu = new Menu(item, itemImage.getTag().toString(), price, category,
                            true, Menu.ITEM_TYPE);
                    reference.child("menu").child(menuTime).child(category).push().setValue(menu);

                    Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show();

                    data.remove(0);
                    data.add(0, new Menu("", "", "", true,
                            Menu.HEADER_ADD_ITEM_TYPE));
                    notifyItemChanged(0);

                    itemNameEditText.getText().clear();
                    itemPriceEditText.getText().clear();
                    itemImage.setVisibility(View.GONE);
                }
            });
        }
    }

    class ExistingItemViewHolder extends RecyclerView.ViewHolder {

        MaterialButton addItem, cancel, browseImage;
        TextInputEditText itemNameEditText, itemPriceEditText;
        ImageView itemImage;

        ExistingItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameEditText = itemView.findViewById(R.id.existingItemNameEditText);
            itemPriceEditText = itemView.findViewById(R.id.existingItemPriceEditText);
            addItem = itemView.findViewById(R.id.updateExistingItemButton);
            cancel = itemView.findViewById(R.id.updateExistingCancelButton);
            browseImage = itemView.findViewById(R.id.itemImageBrowseImage);
            itemImage = itemView.findViewById(R.id.itemImageSelectIV);

            browseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_select_image);

                    imageData = new ArrayList<>();

                    Chip uploadChip = dialog.findViewById(R.id.uploadFoodImageChip);
                    uploadChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            ((UpdateMenuActivity) context).startActivityForResult(Intent.createChooser(
                                    intent, "Select Picture"), UpdateMenuActivity.PICK_IMAGE_REQUEST);
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
                    if (itemImage.getVisibility() == View.GONE) {
                        Toast.makeText(context, "Item Image Required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final Menu menu = new Menu(item, itemImage.getTag().toString(), price, category,
                            true, Menu.ITEM_TYPE);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child("menu").child(menuTime).child(category).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Menu menuFromDB = snapshot.getValue(Menu.class);
                                assert menuFromDB != null;
                                String fromDB = menuFromDB.getName();
                                if (fromDB.equals(forExisting.getName())) {
                                    snapshot.getRef().setValue(menu);
                                    Toast.makeText(context, "Item Updated!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    data.remove(getLayoutPosition());
                    data.add(getLayoutPosition(), menu);
                    notifyItemChanged(getLayoutPosition());

                    itemNameEditText.getText().clear();
                    itemPriceEditText.getText().clear();
                    itemImage.setVisibility(View.GONE);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    data.remove(getLayoutPosition());
                    data.add(getLayoutPosition(), forExisting);
                    notifyItemChanged(getLayoutPosition());
                }
            });
        }
    }


}
