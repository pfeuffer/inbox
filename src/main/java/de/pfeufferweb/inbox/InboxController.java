package de.pfeufferweb.inbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class InboxController {
    private final Inbox inbox;

    @Autowired
    public InboxController(Inbox inbox) {
        this.inbox = inbox;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResult> search(@RequestParam(name = "query", required = true) String query) {
        return ResponseEntity.ok(inbox.search(query));
    }

    @GetMapping("/get")
    public ResponseEntity<SearchResult> get(@RequestParam(name = "location", required = true) String location) {
        return ResponseEntity.ok(inbox.get(location));
    }
}
