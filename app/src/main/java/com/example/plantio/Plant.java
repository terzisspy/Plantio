package com.example.plantio;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class Plant implements Parcelable {
    public String name;
    public String light;
    public String short_description;
    public String full_description;
    public Double high_temperature;
    public Double low_temperature;

    public String image;

    public Plant(){
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

