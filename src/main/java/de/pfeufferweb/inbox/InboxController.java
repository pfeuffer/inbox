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
    private final FileScanner fileScanner;
    private final Inbox inbox;

    @Autowired
    public InboxController(FileScanner fileScanner, Inbox inbox) {
        this.fileScanner = fileScanner;
        this.inbox = inbox;
    }

    @PostMapping
    public void add(@RequestParam(name = "directory", required = true) String directory) {
        fileScanner.scan(directory);
    }

    @GetMapping
    public ResponseEntity<SearchResult> search(@RequestParam(name = "query", required = true) String query) {
        return ResponseEntity.ok(inbox.search(query));
    }
}
