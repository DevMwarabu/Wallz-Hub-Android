package com.e.wallzhub.Constants.Models;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImageModel {
    private String photographer,photographer_url,id;
    private JSONObject src,user;
    private JSONArray video_files,video_pictures;
    private int type;

    public ImageModel(JSONObject user, JSONArray video_files,JSONArray video_pictures, String id,int type) {
        this.user = user;
        this.video_files = video_files;
        this.type = type;
        this.id = id;
        this.video_pictures = video_pictures;
    }

    public ImageModel(String photographer, String photographer_url, String id, JSONObject src,int type) {
        this.photographer = photographer;
        this.photographer_url = photographer_url;
        this.id = id;
        this.src = src;
        this.type = type;
    }

    public JSONArray getVideo_pictures() {
        return video_pictures;
    }

    public void setVideo_pictures(JSONArray video_pictures) {
        this.video_pictures = video_pictures;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }

    public JSONArray getVideo_files() {
        return video_files;
    }

    public void setVideo_files(JSONArray video_files) {
        this.video_files = video_files;
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
