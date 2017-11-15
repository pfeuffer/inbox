package de.pfeufferweb.inbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final String directory;

    @Autowired
    public FileSystemScanner(Inbox inbox, DocumentScanner documentScanner, FileTypeChecker fileTypeChecker, @Value("${directory}") String directory) {
        this.inbox = inbox;
        this.documentScanner = documentScanner;
        this.fileTypeChecker = fileTypeChecker;
        this.directory = directory;
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void scan() {
        LOG.info("start scan of " + directory);
        Path path = Paths.get(directory);
        try {
            scan(path);
            LOG.info("scan finished");
        } catch (Exception e) {
            LOG.error("error scanning directory", e);
        }
    }

    private void scan(Path path) {
        LOG.info("scanning " + path);
        list(path)
                .filter(Files::isRegularFile)
                .filter(this::supportedFileType)
                .map(documentScanner::read)
                .forEach(inbox::register);
        list(path)
                .filter(Files::isDirectory)
                .forEach(this::scan);
    }

    private boolean supportedFileType(Path path) {
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
