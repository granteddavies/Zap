package com.zap;

/**
 * Created by Grant on 4/24/2016.
 */
public class Invite {
    public enum Status {
        YES, NO, MAYBE, PENDING
    }

    private String id;
    private String eventId;
    private String recipientId;
    private Status status;

    public Invite(String id, String eventId, String recipientId, Status status) {
        this.id = id;
        this.eventId = eventId;
        this.recipientId = recipientId;
        status = Status.PENDING;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
