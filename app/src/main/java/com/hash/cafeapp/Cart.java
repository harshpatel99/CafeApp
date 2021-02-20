package com.hash.cafeapp;

public class Cart {

    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;
    public static final int TOTAL_TYPE = 2;

    String amount;
    String name;
    String quantity;
    String image;
    private int type;

    public Cart(String amount, String name, String quantity, String image, int type) {
        this.amount = amount;
        this.name = name;
        this.quantity = quantity;
        this.image = image;
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
