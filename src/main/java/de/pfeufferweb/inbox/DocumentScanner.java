package de.pfeufferweb.inbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Component
public class DocumentScanner {
    private static Log LOG = LogFactory.getLog(FileSystemScanner.class);

    public Optional<Document> read(Path file) {
        LOG.info("reading file " + file);
        try {
            String content = new Tika().parseToString(file.toFile());
            return of(
                    new Document.Builder()
                            .location(createLocation(file))
                            .content(content)
                            .fileType(getFileType(file))
                            .lastModified(Files.getLastModifiedTime(file).toMillis())
                            .build());
        } catch (Exception e) {
            LOG.info("could not parse file " + file.toAbsolutePath(), e);
            return empty();
        }
    }

    private String getFileType(Path file) {
        return new FileTypeExtractor().extractFileType(file.getFileName().toString());
    }

    public Location createLocation(Path file) {
        return new Location(file);
    }
}
