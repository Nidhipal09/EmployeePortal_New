package com.employeeportal.dto.onboarding;

import javax.persistence.*;

public class UploadedFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @JsonIgnore
    private Long Id;
    private String url;

    public UploadedFiles() {
    }

    public UploadedFiles(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

}
