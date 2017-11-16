package de.pfeufferweb.inbox;

import java.net.URI;
import java.nio.file.Path;

public class Location {

    private URI uri;

    public Location(Path file) {
        this.uri = file.toAbsolutePath().toUri();
    }

    public Location(String location) {
        this.uri = URI.create(location);
    }

    public String getLocationString() {
        return uri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return uri != null ? uri.equals(location.uri) : location.uri == null;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }
}
