package com.example.finalproject.GuardianNews;

public class GuardianNews {
    protected String title;
    protected String url;
    protected String sectionName;

    public GuardianNews(){}

    public GuardianNews(String t, String u, String sn){
        setTitle(t);
        setUrl(u);
        setSectionName(sn);
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String u) {
        this.url = u;
    }

    public String getSectionName(){ return sectionName;}

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
