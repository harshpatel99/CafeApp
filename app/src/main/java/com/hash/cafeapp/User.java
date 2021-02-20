package com.hash.cafeapp;

public class User {

    public static final int CUSTOMER_TYPE = 0;
    public static final int STORE_TYPE = 1;
    public static final int ADMIN_TYPE = 2;

    private String name;
    private String email;
    private String phone;
    private String compID;
    private int userType;
    private Long date;

    public User() {
    }

    public User(String name, String email, String phone, String compID, int userType, Long date) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.compID = compID;
        this.userType = userType;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompID() {
        return compID;
    }

    public void setCompID(String compID) {
        this.compID = compID;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
