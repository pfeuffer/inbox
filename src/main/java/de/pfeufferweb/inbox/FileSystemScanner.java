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
public class FileSystemScanner {

    private static Log LOG = LogFactory.getLog(FileSystemScanner.class);

    private final Inbox inbox;
    private final DocumentScanner documentScanner;
    private final FileTypeChecker fileTypeChecker;

    @Autowired
    public FileSystemScanner(Inbox inbox, DocumentScanner documentScanner, FileTypeChecker fileTypeChecker) {
        this.inbox = inbox;
        this.documentScanner = documentScanner;
        this.fileTypeChecker = fileTypeChecker;
    }

    public void scan(String directory) {
        Path path = Paths.get(directory);
        scan(path);
    }

    private void scan(Path path) {
        LOG.info("scanning " + path);
        list(path).filter(Files::isRegularFile).filter(this::supportedFiletype).forEach(f -> inbox.register(documentScanner.read(f)));
        list(path).filter(Files::isDirectory).forEach(this::scan);
    }

    private boolean supportedFiletype(Path path) {
        return fileTypeChecker.supported(path.getFileName().toString());
    }

    private Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new RuntimeException("could not list directory " + path, e);
        }
    }
}
