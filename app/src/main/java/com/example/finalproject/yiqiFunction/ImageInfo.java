package com.example.finalproject.yiqiFunction;

import android.graphics.Bitmap;

public class ImageInfo {

    private String lat;
    private String lon;
    private String dateInfo;
    private long id;
    private String idInfo;
    private String sourceInfo;
    private String serviceVersionInfo;
    private Bitmap pic;
    private String imageUrl;

    public ImageInfo(String lat, String lon, String dateInfo, String idInfo, String sourceInfo, String serviceVersionInfo, String imageUrl, Bitmap pic){
        this.dateInfo = dateInfo;
        this.idInfo = idInfo;
        this.sourceInfo = sourceInfo;
        this.serviceVersionInfo = serviceVersionInfo;
        this.pic = pic;
        this.id = 0;
        this.imageUrl = imageUrl;
        this.lon = lon;
        this.lat = lat;

    }
    public ImageInfo(String lat, String lon, String dateInfo, String idInfo, String sourceInfo, String serviceVersionInfo, String imageUrl, Bitmap pic, long id){
        this.dateInfo = dateInfo;
        this.idInfo = idInfo;
        this.sourceInfo = sourceInfo;
        this.serviceVersionInfo = serviceVersionInfo;
        this.pic = pic;
        this.id = id;
        this.imageUrl = imageUrl;
        this.lon = lon;
        this.lat = lat;

    }

    public String getDateInfo(){
        return this.dateInfo;
    }

    public String getIdInfo(){
        return this.idInfo;
    }

    public String getSourceInfo(){
        return this.sourceInfo;
    }

    public  String getServiceVersionInfo(){
        return this.serviceVersionInfo;
    }

    public Bitmap getPic(){
        return this.pic;
    }

    public long getId(){
        return this.id;
    }

    public String getLat() {return  this.lat;}

    public String getLon() {return  this.lon;}

    public  String getImageUrl() {return  this.imageUrl;}

    public void setLat(String lat){this.lat = lat;}
    public void setLon(String lon) {this.lon = lon;}
    public void setDateInfo(String dateInfo) {this.dateInfo = dateInfo;}
    public void setId(long id) {this.id = id;}
    public void setIdInfo(String idInfo) {this.idInfo = idInfo;}
    public void setSourceInfo(String sourceInfo) {this.sourceInfo = sourceInfo;}
    public void setServiceVersionInfo(String serviceVersionInfo) {this.serviceVersionInfo = serviceVersionInfo;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public void setPic(Bitmap pic) {this.pic = pic;}

    public String toString(){
        return     "lat: "+ lat +"\nlon: "+ lon +"\nimageId: " + idInfo + "\nimageDate: " + dateInfo +
                "\nimageResource: " + sourceInfo + "\nimageServiceVersion: " + serviceVersionInfo + "\nimageUrl: "+ imageUrl;
    }
}
