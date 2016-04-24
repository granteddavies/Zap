package com.zap;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Grant on 4/24/2016.
 * git test comment by Angus 4.24.16
 * second test
 * third test
 * fifth test
 */
public class Event {
    private String id;
    private String hostId;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;

    public Event(String hostFid, String title, String description, Date startTime, Date endTime) {
        this.hostId = hostFid;
        this.title = title;
        this.description = description;
        this. startTime = startTime;
        this.endTime = endTime;
    }

    public String getHostId() {
        return hostId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }


    public String getId() {
        return id;
    }
}
