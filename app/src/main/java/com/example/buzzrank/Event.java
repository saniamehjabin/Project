package com.example.buzzrank;

public class Event {
    private String eventName;
    private String eventId;

    // Default constructor
    public Event() {}

    // Constructor with parameters
    public Event(String name, String id) {
        this.eventName = name;
        this.eventId = id;
    }

    // Getter for event name
    public String getEventName() {
        return eventName;
    }

    // Getter for event ID
    public String getId() {
        return eventId;
    }

    // Setter for event ID
    public void setId(String id) {
        this.eventId = id;
    }
}
