package com.hash.cafeapp;

public class Profile {

    static final int HEADER_TYPE = 0;
    static final int PROFILE_DATA_TYPE = 1;
    static final int EDIT_TYPE = 2;
    static final int LOADING_TYPE = 3;

    private String title;
    private String data;
    private String name;
    private int type;

    public Profile(int type) {
        this.type = type;
    }

    public Profile(String title, String data, int type) {
        this.title = title;
        this.data = data;
        this.type = type;
    }

    public Profile(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
