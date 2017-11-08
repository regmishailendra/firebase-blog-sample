package com.example.shailendra.appfire;

/**
 * Created by Shailendra on 10/30/2016.
 */
public class Blog {
    private String title,description,image,Username;

    public Blog(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.Username=Username;
    }
    public Blog()
    {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }}