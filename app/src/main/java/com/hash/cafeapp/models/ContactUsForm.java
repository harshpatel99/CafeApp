package com.hash.cafeapp.models;

public class ContactUsForm {

    String email,descrition;

    public ContactUsForm() {
    }

    public ContactUsForm(String email, String descrition) {
        this.email = email;
        this.descrition = descrition;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescrition() {
        return descrition;
    }

    public void setDescrition(String descrition) {
        this.descrition = descrition;
    }
}
