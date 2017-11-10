package de.pfeufferweb.inbox;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DocumentScanner {

    public Document scan(Path file) {
        try {
            String content = new Tika().parseToString(file.toFile());
            return new Document(new Location(file.toAbsolutePath().toUri()), content);
        } catch (Exception e) {
            throw new RuntimeException("could not parse file " + file.toAbsolutePath(), e);
        }
    }
}
