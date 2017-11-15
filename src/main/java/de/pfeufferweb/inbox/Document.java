package de.pfeufferweb.inbox;

public class Document {

    private final Location location;
    private final String content;
    private final long lastModified;

    Document(Location location, String content, long lastModified) {
        this.location = location;
        this.content = content;
        this.lastModified = lastModified;
    }

    public Location getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

    public long getLastModified() {
        return lastModified;
    }
}
