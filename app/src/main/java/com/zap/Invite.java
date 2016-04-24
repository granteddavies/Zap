package com.zap;

/**
 * Created by Grant on 4/24/2016.
 */
public class Invite {
    private String id;
    private String eventId;
    private String recipientId;
    private String status;

    public Invite(String eventId, String recipientId) {
        this.eventId = eventId;
        this.recipientId = recipientId;
        status = EventData.STATUS_PENDING;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
