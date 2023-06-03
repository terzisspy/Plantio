package com.example.plantio;

import android.icu.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Plant implements Parcelable {
    public String name;
    public String light;
    public String short_description;
    public String full_description;
    public Double high_temperature;
    public Double low_temperature;

    public String image;
    public int frequency;

    public Plant(){
    }

    public Plant(String name,Object date){
        this.name = name;
        this.short_description = date.toString();
    }

    public Plant(String name,String light,String short_description,String full_description,Double high_temperature,Double low_temperature, String image){
        this.full_description = full_description;
        this.short_description = short_description;
        this.name = name;
        this.light = light;
        this.high_temperature = high_temperature;
        this.low_temperature = low_temperature;
        this.image=image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public void setFull_description(String full_description) {
        this.full_description = full_description;
    }

    public void setHigh_temperature(Double high_temperature) {
        this.high_temperature = high_temperature;
    }

    public void setLow_temperature(Double low_temperature) {
        this.low_temperature = low_temperature;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getLight() {
        return light;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getFull_description() {
        return full_description;
    }

    public Double getHigh_temperature() {
        return high_temperature;
    }

    public Double getLow_temperature() {
        return low_temperature;
    }

    public String getImage() {
        return image;
    }


    public Plant(Parcel in){
        String[] data = new String[7];
        in.readStringArray(data);
        this.name=data[0];
        this.short_description=data[1];
        this.full_description=data[2];
        this.low_temperature=Double.parseDouble(data[3]);
        this.high_temperature=Double.parseDouble(data[4]);
        this.light=data[5];
        this.image=data[6];
    }

    public int daysInBetween(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Parse the given date string to Date object
            Date givenDate = sdf.parse(short_description);

            // Get today's date
            Calendar calendarToday = Calendar.getInstance();
            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
            calendarToday.set(Calendar.MINUTE, 0);
            calendarToday.set(Calendar.SECOND, 0);
            calendarToday.set(Calendar.MILLISECOND, 0);
            Date today = calendarToday.getTime();

            // Calculate the number of days between the given date and today's date
            long diffInMillis = today.getTime()-givenDate.getTime();
            long daysBetween = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            // Return the number of days as an integer
            return (int) daysBetween;
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing error
        }

        return -1;
    }

    @Override
    public String toString(){
        String plantContext;
        return plantContext=this.name+" "+this.short_description+" "+this.full_description+" "+this.low_temperature+" "+this.high_temperature+" "+this.light;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.name,this.short_description, this.full_description, String.valueOf(this.low_temperature), String.valueOf(this.high_temperature), this.light, this.image});
    }

    public static final Parcelable.Creator<Plant> CREATOR=new Parcelable.Creator<Plant>(){
        @Override
        public Plant createFromParcel(Parcel source){
            return new Plant(source);
        }

        @Override
        public Plant[] newArray(int size){
            return new Plant[size];
        }
    };

}

