package org.network.model;

public class MessageRequest {
    public String roomId;
    public String text;
    public MessageRequest(String roomId, String text) {
        this.roomId = roomId;
        this.text = text;
    }
}