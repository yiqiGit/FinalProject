package com.example.finalproject.BBCNews;

import androidx.annotation.NonNull;

/**
 * This class includes information of news.
 *
 *@author Xiaoting Kong
 *@version 1.0
 */
public class News {
    /**
     * A long representing database ID of news.
     */
    private long id;

    /**
     * A string representing title of news.
     */
    private String title;

    /**
     * A string representing description of news.
     */
    private String description;

    /**
     * A string representing date of news.
     */
    private String date;

    /**
     * A string representing link of news.
     */
    private String link;

    /**
     * A boolean representing if the news is favourite news.
     */
    private boolean isFavourite = false;

    /**
     * Constructs an empty News.
     */
    public News() {
    }

    /**
     * Constructs a News with the specified id, title, description, date, link and isFavourite.
     *
     * @param id          a long representing database ID of news
     * @param title       a string representing title of news
     * @param description a string representing description of news
     * @param date        a string representing date of news
     * @param link        a string representing link of news
     * @param isFavourite a boolean representing if the news is favourite news
     */
    public News(long id, String title, String description, String date, String link, boolean isFavourite) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setDate(date);
        setLink(link);
        setIsFavourite(isFavourite);
    }

    /**
     * Gets database ID of news.
     *
     * @return a long representing database ID of news
     */
    public long getId() {
        return id;
    }

    /**
     * Sets database ID of news.
     *
     * @param id a long representing database ID of news
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets title of news.
     *
     * @return a string representing title of news
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of news.
     *
     * @param title a string representing title of news
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets title of description.
     *
     * @return a string representing description of news
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of news.
     *
     * @param description a string representing description of news
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets date of description.
     *
     * @return a string representing date of news
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date of news.
     *
     * @param date a string representing date of news
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets link of description.
     *
     * @return a string representing link of news
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link of news.
     *
     * @param link a string representing link of news
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets isFavourite.
     *
     * @return a boolean representing if the news is favourite news
     */
    public boolean getIsFavourite() {
        return isFavourite;
    }

    /**
     * Sets isFavourite.
     *
     * @param isFavourite a boolean representing if the news is favourite news
     */
    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    /**
     * Returns a string representation of this news.
     *
     * @return a string representation of this news
     */
    @NonNull
    @Override
    public String toString() {
        super.toString();
        String s = "Title : " + getTitle() + "\n\nDescription :" + getDescription() + "\n\nDate :" + getDate() + "\n\nLink :" + getLink() + "\n";
        return s;
    }
}

