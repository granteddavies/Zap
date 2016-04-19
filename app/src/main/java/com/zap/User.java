package com.zap;

import com.orm.SugarRecord;

/**
 * Created by Grant on 4/14/2016.
 */
public class User extends SugarRecord {
    private String name;
    private String fid;
    private boolean available;
    private String activity;

    public User() {
    }

    public User(String name, String fid) {
        this.name = name;
        this.fid = fid;
        available = false;
        activity = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFid() {
        return fid;
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
