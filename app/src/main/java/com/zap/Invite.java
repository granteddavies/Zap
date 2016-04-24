package com.zap;

/**
 * Created by Grant on 4/24/2016.
 */
public class Invite {
    private String id;
    private String eventid;
    private String recipientid;
    private String status;

    public Invite(String eventId, String recipientId) {
        this.eventid = eventId;
        this.recipientid = recipientId;
        status = EventData.STATUS_PENDING;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getId() {
        return id;
    }

    public String getEventid() {
        return eventid;
    }

    public String getRecipientid() {
        return recipientid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
