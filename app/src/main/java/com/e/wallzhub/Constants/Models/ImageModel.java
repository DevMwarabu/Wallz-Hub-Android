package com.e.wallzhub.Constants.Models;

import org.json.JSONObject;

public class ImageModel {
    private String photographer,photographer_url,id;
    private JSONObject src;


    public ImageModel(String photographer, String photographer_url, String id, JSONObject src) {
        this.photographer = photographer;
        this.photographer_url = photographer_url;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getSrc() {
        return src;
    }

    public void setSrc(JSONObject src) {
        this.src = src;
    }
}
