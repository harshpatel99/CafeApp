package com.hash.cafeapp;

public class Menu {

    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;
    public static final int HEADER_ADD_ITEM_TYPE = 2;
    public static final int ADD_ITEM_TYPE = 3;
    public static final int UPDATE_EXISTING_ITEM_TYPE = 4;
    public static final int UNAVAILABLE = 5;

    private String name,url,price,category,menuTime;
    private boolean availability;
    private int type;

    public Menu() {
    }

    public Menu(int type) {
        this.type = type;
    }

    public Menu(String menuTime, int type) {
        this.menuTime = menuTime;
        this.type = type;
    }

    public Menu(String name, String price, String category, boolean availability) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.availability = availability;
    }

    public Menu(String category, String menuTime, boolean availability, int type) {
        this.category = category;
        this.menuTime = menuTime;
        this.availability = availability;
        this.type = type;
    }

    public Menu(String name, String price, String category, boolean availability, int type) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.availability = availability;
        this.type = type;
    }

    public Menu(String name, String url, String price, String category,boolean availability, int type) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.category = category;
        this.availability = availability;
        this.type = type;
    }

    public String getMenuTime() {
        return menuTime;
    }

    public void setMenuTime(String menuTime) {
        this.menuTime = menuTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
