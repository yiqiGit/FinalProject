package com.example.finalproject.BBCNews;

import androidx.annotation.NonNull;

public class News {
    private long id ;
    private String title;
    private String description;
    private String date;
    private String link;
    private boolean isFavourite = false;

    public News(){

    }

    public News(long i,String title, String description, String date, String link,boolean isFavourite) {
        setId(i);
        setTitle(title);
        setDescription(description);
        setDate(date);
        setLink(link);
        setIsFavourite(isFavourite);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    @NonNull
    @Override
    public String toString() {
        super.toString();
        String s ="Title : " + getTitle()+ "\n\nDescription :" + getDescription()+ "\n\nDate :" + getDate() + "\n\nLink :" + getLink()+"\n";
        return s;
    }
}

