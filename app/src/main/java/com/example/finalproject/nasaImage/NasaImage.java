package com.example.finalproject.nasaImage;

import java.util.List;

public class NasaImage implements Comparable<NasaImage>{
    /**
     * database id for this object
     */
    private long id;
    /**
     * Date associated with the Nasa api originated date
     */
    private String date;
    /**
     * Description of this image
     */
    private String description;
    /**
     * Title of this image
     */
    private String title;
    /**
     * File name where this image is store on disk
     */
    private String fileName;
    /**
     * Url for the image
     */
    private String imageUrl;
    /**
     * Url in high definition for this image
     */
    private String hdImageUrl;

    public NasaImage() {  }

    /**
     * Standard constructor, simply initializes the class properties
     * @param id
     * @param date
     * @param description
     * @param title
     * @param fileName
     * @param imageUrl
     * @param hdImageUrl
     */
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

    /**
     * This method is used in order to sort the objects by date
     * @param o NasaImage object to be compared with this object
     * @return integer number to be used by the {@link java.util.Collections#sort(List)} method.
     */
    @Override
    public int compareTo(NasaImage o) {
        return o.getDate().compareTo(this.getDate());
    }
}
