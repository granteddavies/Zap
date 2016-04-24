package com.zap;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Grant on 4/24/2016.
 * git test comment by Angus 4.24.16
 * second test
 */
public class Event {
    private String id;
    private String hostFid;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;

    private ArrayList<Invite> invites;

    public Event(String hostFid, String title, String description, Date startTime, Date endTime) {
        this.hostFid = hostFid;
        this.title = title;
        this.description = description;
        this. startTime = startTime;
        this.endTime = endTime;
        invites = new ArrayList<>();
    }

    public String getHostFid() {
        return hostFid;
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

    public ArrayList<Invite> getInvites() {
        return invites;
    }

    public void addInvite(Invite invite) {
        invites.add(invite);
    }
}
