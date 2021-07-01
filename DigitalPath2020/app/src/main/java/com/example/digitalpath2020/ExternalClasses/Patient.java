package com.example.digitalpath2020.ExternalClasses;

public class Patient {
    private String slide;
    private String cancer;
    private String slideID;
    private String username;

    public Patient(String slide, String cancer, String slideID, String username) {
        this.slide = slide;
        this.cancer = cancer;
        this.slideID = slideID;
        this.username = username;
    }

    public Patient() {

    }

    public String getSlide() {
        return slide;
    }

    public void setSlide(String slide) {
        this.slide = slide;
    }

    public String getCancer() {
        return cancer;
    }

    public void setCancer(String cancer) {
        this.cancer = cancer;
    }

    public String getSlideID() {
        return slideID;
    }

    public void setSlideID(String slideID) {
        this.slideID = slideID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
