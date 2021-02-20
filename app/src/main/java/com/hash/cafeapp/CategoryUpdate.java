package com.hash.cafeapp;

import java.util.ArrayList;

public class CategoryUpdate {

    public static final int HEADER_TYPE = 0;
    public static final int ADD_CATEGORY_TYPE = 1;
    public static final int CATEGORY_TYPE = 2;
    //public static final int ADD_ITEM_TYPE_HEADER = 3;
    public static final int ADD_ITEM_TYPE = 4;
    //public static final int ITEM_TYPE = 5;
    public static final int EMPTY_TYPE = 6;
    public static final int UPDATE_EXISTING_ITEM_TYPE = 7;
    public static final int LOADING_TYPE = 8;
    public static final int NO_DATA_TYPE = 9;
    public static final int ITEM_LIST_TYPE = 3;

    private ArrayList<Menu> items;
    private String categoryName;
    private int type;

    public CategoryUpdate() {
    }

    public CategoryUpdate(int type) {
        this.type = type;
    }

    public CategoryUpdate(String categoryName, ArrayList<Menu> items, int type) {
        this.items = items;
        this.categoryName = categoryName;
        this.type = type;
    }

    public ArrayList<Menu> getItems() {
        return items;
    }

    public void setItems(ArrayList<Menu> items) {
        this.items = items;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
