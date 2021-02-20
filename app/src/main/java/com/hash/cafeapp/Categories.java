package com.hash.cafeapp;

public class Categories {

    public static final int HEADER_TYPE = 0;
    public static final int CATEGORY_TYPE = 1;
    public static final int ITEM_TYPE = 2;
    public static final int EMPTY_TYPE = 3;

    private String name,url,price;
    private int type;

    public Categories(String name, String url,String price, int type) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
