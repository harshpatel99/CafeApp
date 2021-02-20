package com.hash.cafeapp;

public class History {

    public static final int HEADER_TYPE = 0;
    public static final int CARD_TYPE = 1;
    public static final int CAFE_TYPE = 2;

    private String amount,date,items,email,from;
    private int type,status;
    private long dateTime;

    public History(String amount, String date, String items, int type) {
        this.amount = amount;
        this.date = date;
        this.items = items;
        this.type = type;
    }

    public History(String amount, String date, String items, String email, int type) {
        this.amount = amount;
        this.date = date;
        this.items = items;
        this.email = email;
        this.type = type;
    }

    public History(String amount, String date, String items, int status, int type) {
        this.amount = amount;
        this.date = date;
        this.items = items;
        this.type = type;
        this.status = status;
    }


    public History(String amount, String date, String items, String email, int status, int type) {
        this.amount = amount;
        this.date = date;
        this.items = items;
        this.email = email;
        this.type = type;
        this.status = status;
    }

    public History(String amount, String date, String items, String email, int status, long dateTime,int type) {
        this.amount = amount;
        this.date = date;
        this.items = items;
        this.email = email;
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
    }

    public History(String amount, String date, String items, String email, int status, long dateTime,String from,int type) {
        this.amount = amount;
        this.date = date;
        this.items = items;
        this.email = email;
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
