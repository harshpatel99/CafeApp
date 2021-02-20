package com.hash.cafeapp;

import java.util.ArrayList;

public class CategoryData {

    public static final int HEADER_TYPE = 0;
    public static final int CATEGORY_TYPE = 1;
    public static final int MENU_TIME_TYPE = 6;
    public static final int ITEM_TYPE = 2;
    public static final int HEADER_ADD_CATEGORY_TYPE = 3;
    public static final int ADD_CATEGORY_TYPE = 4;
    public static final int EMPTY_TYPE = 5;


    private ArrayList<Menu> items;
    private String categoryName;
    private int type;

    public CategoryData() {
    }

    public CategoryData(ArrayList<Menu> items) {
        this.items = items;
    }

    public CategoryData(ArrayList<Menu> items, int type) {
        this.items = items;
        this.type = type;
    }

    public CategoryData(String categoryName, int type) {
        this.items = items;
        this.categoryName = categoryName;
        this.type = type;
    }

    public CategoryData(String categoryName, ArrayList<Menu> items, int type) {
        this.items = items;
        this.categoryName = categoryName;
        this.type = type;
    }

    public ArrayList<Menu> getItems() {
        return items;
    }

    public void setItems(ArrayList<Menu> menus) {
        this.items = menus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
