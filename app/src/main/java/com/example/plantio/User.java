package com.example.plantio;

import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * The User class represents each user of the system.
 */
public class User {
    String pattern = "dd-MM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    public String mail;
    public String password;
    public HashMap<String, Date> plantswater = new HashMap<String, Date>();

    // Constructors
    public User(String pattern, String mail, String password, HashMap<String, Date> plantswater) {
        this.pattern = pattern;
        this.mail = mail;
        this.password = password;
        this.plantswater = plantswater;
    }

    // Setters and Getters
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<String, Date> getPlantswater() {
        return plantswater;
    }

    public void setPlantswater(HashMap<String, Date> plantswater) {
        this.plantswater = plantswater;
    }
}



