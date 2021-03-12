package com.e.wallzhub.Constants.Models;

/**
 * This project file is owned by DevMwarabu, johnmwarabuchone@gmail.com.
 * Created on 3/11/21. Copyright (c) 2021 DevMwarabu
 */
public class Advert {
    private String title,redirect_link,imageUrl;

    public Advert(String title, String redirect_link, String imageUrl) {
        this.title = title;
        this.redirect_link = redirect_link;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRedirect_link() {
        return redirect_link;
    }

    public void setRedirect_link(String redirect_link) {
        this.redirect_link = redirect_link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
