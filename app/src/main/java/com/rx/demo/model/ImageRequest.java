package com.rx.demo.model;


public class ImageRequest {
    private final String searchTerm;
    private final String offset;

    public ImageRequest(String searchTerm, String offset) {
        this.searchTerm = searchTerm;
        this.offset = offset;
    }

    public ImageRequest(String searchTerm) {
        this.searchTerm = searchTerm;
        offset = null;
    }


    public String getSearchTerm() {
        return searchTerm;
    }

    public String getOffset() {
        return offset;
    }
}
