package com.zap;

import com.orm.SugarRecord;

/**
 * Created by Grant on 4/14/2016.
 */
public class Friend extends SugarRecord {
    private String name;
    private String fid;

    public Friend(String name, String fid) {
        this.name = name;
        this.fid = fid;
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
}
