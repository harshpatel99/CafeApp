package com.hash.cafeapp;

public class CartItem {

    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;

    private String name,url,price,category,email,from;
    private int quantity,type,status;

    public CartItem() {
    }

    public CartItem(String name, String url, String price, String category, int quantity, int type) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.type = type;
    }

    public CartItem(String name, String url, String price, String category, int quantity, int type, int status) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.type = type;
        this.status = status;
    }

    public CartItem(String name, String url, String price, String category, String email, String from, int quantity, int type, int status) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.category = category;
        this.email = email;
        this.from = from;
        this.quantity = quantity;
        this.type = type;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
