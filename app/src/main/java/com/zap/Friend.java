package com.zap;

import com.orm.SugarRecord;

/**
 * Created by Grant on 4/14/2016.
 */
public class Friend extends SugarRecord {
    private String name;

    public Friend(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
