package com.example.youngchae.birdwizer;

/**
 * Created by youngchae on 2016-07-16.
 */
public class MainRecyclerItem {
    int image;
    String title;
    String location;

    public MainRecyclerItem(int image, String title, String location) {
        this.image = image;
        this.title = title;
        this.location = location;
    }
    public int getImage(){
        return this.image;
    }
    public String getMainTitle(){
        return this.title;
    }
    public String getLocation(){
        return this.location;
    }
}
