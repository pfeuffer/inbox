package de.pfeufferweb.inbox;

public class FileTypeExtractor {

    public String extractFileType(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0) {
            return "";
        } else {
            return fileName.substring(lastDot + 1);
        }
    }
}
