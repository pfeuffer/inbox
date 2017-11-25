package de.pfeufferweb.inbox;

import java.util.UUID;

public class Document {

    private final String uuid;
    private final Location location;
    private final String content;
    private final String fileType;
    private final long lastModified;

    private Document(String uuid, Location location, String content, String fileType, long lastModified) {
        this.uuid = uuid;
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

    public static class Builder {
        private String uuid = UUID.randomUUID().toString();
        private Location location;
        private String content;
        private String fileType;
        private long lastModified;

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder lastModified(long lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Document build() {
            return new Document(uuid, location, content, fileType, lastModified);
        }
    }
}
