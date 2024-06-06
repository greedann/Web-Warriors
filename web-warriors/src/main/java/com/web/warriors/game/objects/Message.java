package com.web.warriors.game.objects;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private String type;
    private String message;
    private String sender;
    private List<Integer> receives;

    public Message(String type, String message, String sender) {
        this.type = type;
        this.message = message;
        this.sender = sender;
        this.receives = new ArrayList<>();
    }

    public Message() {
        this.type = "null";
        this.message = "null";
        this.sender = "null";
        this.receives = new ArrayList<>();
    }

    public String getType() {
        return type;
    }


    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<Integer> getReceives() {
        return receives;
    }

    public void setReceives(List<Integer> receives) {
        this.receives = receives;
    }

    public void addReceive(Integer receive) {
        this.receives.add(receive);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
