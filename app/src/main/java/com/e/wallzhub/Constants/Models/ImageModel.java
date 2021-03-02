package com.e.wallzhub.Constants.Models;

import org.json.JSONObject;

public class ImageModel {
    private String photographer,photographer_url;
    private JSONObject src;

    public ImageModel(String photographer, String photographer_url, JSONObject src) {
        this.photographer = photographer;
        this.photographer_url = photographer_url;
        this.src = src;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getPhotographer_url() {
        return photographer_url;
    }

    public void setPhotographer_url(String photographer_url) {
        this.photographer_url = photographer_url;
    }

    public JSONObject getSrc() {
        return src;
    }

    public void setSrc(JSONObject src) {
        this.src = src;
    }
}
