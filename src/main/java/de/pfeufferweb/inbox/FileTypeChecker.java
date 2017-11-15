package de.pfeufferweb.inbox;

import java.util.List;

public class FileTypeChecker {
    private final List<String> supportedTypes;

    public FileTypeChecker(List<String> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public boolean supported(String fileName) {
        return supportedTypes.stream().anyMatch(t -> fileName.toLowerCase().endsWith(t));
    }
}
