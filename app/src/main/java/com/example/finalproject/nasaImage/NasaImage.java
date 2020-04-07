package com.example.finalproject.nasaImage;

public class NasaImage implements Comparable<NasaImage>{

    private long id;
    private String date;
    private String description;
    private String title;
    private String fileName;
    private String imageUrl;
    private String hdImageUrl;

    public NasaImage() {  }

    public NasaImage(long id, String date, String description, String title, String fileName, String imageUrl, String hdImageUrl) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.title = title;
        this.fileName = fileName;
        this.imageUrl = imageUrl;
        this.hdImageUrl = hdImageUrl;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHdImageUrl() {
        return hdImageUrl;
    }

    public void setHdImageUrl(String hdImageUrl) {
        this.hdImageUrl = hdImageUrl;
    }


    @Override
    public int compareTo(NasaImage o) {
        return o.getDate().compareTo(this.getDate());
    }
}
