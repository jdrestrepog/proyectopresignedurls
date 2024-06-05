package com.bancolombia.smartsupervision.model;

import java.util.List;

public class PresignedUrlsResponse {
    private List<String> urls;

    public PresignedUrlsResponse(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
