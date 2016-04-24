package com.zap;


/**
 * Created by Grant on 4/14/2016.
 */
public class User {
    private String id;
    private String name;
    private boolean available;
    private String activity;


    public User(String name, String id) {
        this.name = name;
        this.id = id;
        available = false;
        activity = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }
}
