package de.pfeufferweb.inbox;

public class Document {

    private final Location location;
    private final String content;

    Document(Location location, String content) {
        this.location = location;
        this.content = content;
    }

    public Location getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }
}
