package de.pfeufferweb.inbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class InboxController {
    private final FileSystemScanner fileSystemScanner;
    private final Inbox inbox;

    @Autowired
    public InboxController(FileSystemScanner fileSystemScanner, Inbox inbox) {
        this.fileSystemScanner = fileSystemScanner;
        this.inbox = inbox;
    }

    @PostMapping
    public void add(@RequestParam(name = "directory", required = true) String directory) {
        fileSystemScanner.scan(directory);
    }

    @GetMapping
    public ResponseEntity<SearchResult> search(@RequestParam(name = "query", required = true) String query) {
        return ResponseEntity.ok(inbox.search(query));
    }
}
