package de.pfeufferweb.inbox;

import java.util.UUID;

public class Document {

    private final String uuid;
    private final Location location;
    private final String content;
    private final String fileType;
    private final long lastModified;

    Document(Location location, String content, String fileType, long lastModified) {
        this.uuid = UUID.randomUUID().toString();
        this.location = location;
        this.content = content;
        this.fileType = fileType;
        this.lastModified = lastModified;
    }

    public Location getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

    public String getFileType() {
        return fileType;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getUUID() {
        return uuid;
    }
}
