package com.digitalbuddha.daggerdemo.models;

/**
 * Created by MikeN on 8/16/14.
 */
public class Repo {

    private String clone_url;
    private String description;

    public String getClone_url() {
        return clone_url;
    }

    public void setClone_url(String clone_url) {
        this.clone_url = clone_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}