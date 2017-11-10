package de.pfeufferweb.inbox;

import java.net.URI;

public class Location {

    private URI uri;

    public Location(URI uri) {
        this.uri = uri;
    }

    public String getLocation() {
        return uri.getRawPath();
    }
}
