package org.network.model;

public class ChatRoom {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + " (" + id + ")";
    }
}