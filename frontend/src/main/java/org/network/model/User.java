package org.network.model;

public class User {

    private final String id;
    private final String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User(String name, String id) {
        this.id = id;
        this.name = name;
    }
}