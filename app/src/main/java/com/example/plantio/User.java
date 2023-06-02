package com.example.plantio;

import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;

public class User {
    String pattern = "dd-MM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    public String username;
    public String password;
    public ArrayList<Plant> plants;
    public ArrayList<SimpleDateFormat> dates;

    public String image;

    public User() {
    }

    public User(String username, String password, ArrayList<Plant> plants, ArrayList<SimpleDateFormat> dates) {
        this.username = username;
        this.password = password;
        this.plants = plants;
        this.dates = dates;
        this.image = image;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Plant> getPlants() {
        return plants;
    }

    public ArrayList<SimpleDateFormat> getDates() {
        return dates;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlants(ArrayList<Plant> plants) {
        this.plants = plants;
    }

    public void setDates(ArrayList<SimpleDateFormat> dates) {
        this.dates = dates;
    }
}
