package com.example.ishmeetsingh.rideshine;

/**
 * Created by ishmeet.singh on 10-08-2016.
 */
public class ResultOfQuery {

    public String resId;
    public String resPlaceId;
    public String resName;
    public String resReference;
    public String resIcon;
    public String resVicinity;
    public double lati;
    public double longi;


    public void setLati(double lati) {
        this.lati = lati;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

    public ResultOfQuery() {
        this.resName = null;
        this.resVicinity = null;
    }

    public String getResId() {
        return resId;
    }

    public String getResPlaceId() {
        return resPlaceId;
    }

    public String getResName() {
        return resName;
    }

    public String getResReference() {
        return resReference;
    }

    public String getResIcon() {
        return resIcon;
    }

    public String getResVicinity() {
        return resVicinity;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public void setResPlaceId(String resPlaceId) {
        this.resPlaceId = resPlaceId;
    }

    public void setResReference(String resReference) {
        this.resReference = resReference;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public void setResIcon(String resIcon) {
        this.resIcon = resIcon;
    }

    public void setResVicinity(String resVicinity) {
        this.resVicinity = resVicinity;
    }
}
