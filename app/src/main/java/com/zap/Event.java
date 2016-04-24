package com.zap;

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
    private String hostid;
    private String hostname;
    private String title;
    private String description;
    private Date starttime;
    private Date endtime;

    public Event(String hostFid, String hostName, String title, String description, Date startTime, Date endTime) {
        this.hostid = hostFid;
        this.hostname = hostName;
        this.title = title;
        this.description = description;
        this.starttime = startTime;
        this.endtime = endTime;
    }

    public String getHostid() {
        return hostid;
    }

    public String getHostname() {
        return hostname;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getStarttime() {
        return starttime;
    }

    public Date getEndtime() {
        return endtime;
    }


    public String getId() {
        return id;
    }
}
