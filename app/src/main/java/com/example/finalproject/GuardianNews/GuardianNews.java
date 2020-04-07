package com.example.finalproject.GuardianNews;

/**
 * This is the class to create a GuardianNews object with title, url and section name.
 *
 *@author Pei Lun Zou
 *@version 1.0
 */

public class GuardianNews {
    /**
     * The title of the news
     */
    protected String title;

    /**
     * The url of the news
     */
    protected String url;

    /**
     * The section name of the news
     */
    protected String sectionName;

    /**
     * The constructor that accept three string parameters: title, url and section name.
     */

    public GuardianNews(String t, String u, String sn){
        setTitle(t);
        setUrl(u);
        setSectionName(sn);
    }


    /**
     * The public getter of the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * The public setter of the title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The public getter of the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * The public setter of the url.
     */
    public void setUrl(String u) {
        this.url = u;
    }

    /**
     * The public getter of the section name.
     */
    public String getSectionName(){ return sectionName;}

    /**
     * The public setter of the section name.
     */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
