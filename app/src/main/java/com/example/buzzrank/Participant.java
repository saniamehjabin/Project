package com.example.buzzrank;

public class Participant {
    String name;
    long timestamp;

    public Participant(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
