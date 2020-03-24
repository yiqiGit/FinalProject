package com.example.finalproject.yiqiFunction;

import android.graphics.Bitmap;

public class ImageInfo {

    private String dateInfo;
    private long id;
    private String idInfo;
    private String sourceInfo;
    private String serviceVersionInfo;
    private Bitmap pic;

    public ImageInfo(String dateInfo, String idInfo, String sourceInfo, String serviceVersionInfo, Bitmap pic){
        this.dateInfo = dateInfo;
        this.idInfo = idInfo;
        this.sourceInfo = sourceInfo;
        this.serviceVersionInfo = serviceVersionInfo;
        this.pic = pic;
        this.id = 0;

    }
    public ImageInfo(String dateInfo, String idInfo, String sourceInfo, String serviceVersionInfo, Bitmap pic, long id){
        this.dateInfo = dateInfo;
        this.idInfo = idInfo;
        this.sourceInfo = sourceInfo;
        this.serviceVersionInfo = serviceVersionInfo;
        this.pic = pic;
        this.id = id;

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
}
