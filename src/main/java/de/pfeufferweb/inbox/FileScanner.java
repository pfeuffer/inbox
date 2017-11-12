package de.pfeufferweb.inbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class FileScanner {

    private static Log LOG = LogFactory.getLog(FileScanner.class);

    private final Inbox inbox;
    private final DocumentScanner documentScanner;

    @Autowired
    public FileScanner(Inbox inbox, DocumentScanner documentScanner) {
        this.inbox = inbox;
        this.documentScanner = documentScanner;
    }

    public void scan(String directory) {
        Path path = Paths.get(directory);
        scan(path);
    }

    private void scan(Path path) {
        LOG.info("scanning " + path);
        list(path).forEach(this::handle);
    }

    private Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new RuntimeException("could not list directory " + path, e);
        }
    }

    private void handle(Path path) {
        LOG.info("handling " + path);
        if (Files.isRegularFile(path)) {
            inbox.register(documentScanner.read(path));
        } else if (Files.isDirectory(path)) {
            this.scan(path);
        }
    }
}
