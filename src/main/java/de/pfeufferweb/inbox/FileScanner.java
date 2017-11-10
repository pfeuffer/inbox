package de.pfeufferweb.inbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        LOG.info("scanning " + directory);
        Path path = Paths.get(directory);
        path.forEach(this::handle);
    }

    private void handle(Path path) {
        if (Files.isRegularFile(path)) {
            inbox.register(documentScanner.scan(path));
        }
    }
}
