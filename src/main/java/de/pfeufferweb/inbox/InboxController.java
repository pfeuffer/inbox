package de.pfeufferweb.inbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class InboxController {
    private final Inbox inbox;

    @Autowired
    public InboxController(Inbox inbox) {
        this.inbox = inbox;
    }

    @GetMapping(value = "/search", produces = {"application/json", "application/xml"})
    @ResponseBody
    public SearchResult search(@RequestParam(name = "query", required = true) String query) {
        return inbox.search(query);
    }

    @GetMapping(value = "/search", produces = {"text/html"})
    public String search(@RequestParam(name = "query", required = true) String query, Model model) {
        SearchResult result = search(query);
        model.addAttribute("items", result.getItems());
        model.addAttribute("size", result.getItems().size());
        return "get";
    }

//    @GetMapping(value = "/get", produces = {"application/json", "application/xml"})
//    @ResponseBody
//    public SearchResult get(@RequestParam(name = "location", required = true) String location) {
//        return inbox.get(location);
//    }
//
//    @GetMapping(value = "/get", produces = {"text/html"})
//    public String get(@RequestParam(name = "location", required = true) String location, Model model) {
//        SearchResult result = get(location);
//        model.addAttribute("items", result.getItems());
//        model.addAttribute("size", result.getItems().size());
//        return "get";
//    }

    @GetMapping("/read/{uuid}")
    public ResponseEntity<InputStreamResource> readFile(@PathVariable(name = "uuid") String uuid) {
        Optional<URI> searchResult = inbox.getUri(uuid);
        if (!searchResult.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = Paths.get(searchResult.get());
        long size;
        InputStream inputStream;
        try {
            size = Files.size(filePath);
            inputStream = Files.newInputStream(filePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.ok()
                .contentLength(size)
                .contentType(determineContentType(filePath.toString()))
                .body(new InputStreamResource(inputStream));

    }

    private MediaType determineContentType(String location) {
        String cleanLocation = location.toLowerCase().trim();
        if (cleanLocation.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (cleanLocation.endsWith(".doc")) {
            return MediaType.valueOf("application/msword");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
