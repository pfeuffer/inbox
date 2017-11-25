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
public class InboxController {
    private final Inbox inbox;

    @Autowired
    public InboxController(Inbox inbox) {
        this.inbox = inbox;
    }

    @GetMapping(value = "/search", produces = {"application/json", "application/xml"})
    @ResponseBody
    public SearchResult search(@RequestParam("query") String query) {
        return inbox.search(query);
    }

    @GetMapping(value = "/search", produces = {"text/html"})
    public String search(@RequestParam("query") String query, Model model) {
        SearchResult result = search(query);
        model.addAttribute("items", result.getItems());
        model.addAttribute("size", result.getItems().size());
        return "get";
    }

    @GetMapping("/read/{uuid}")
    public ResponseEntity<InputStreamResource> readFile(@PathVariable(name = "uuid") String uuid) {
        Optional<SearchResult.SearchItem> searchResult = inbox.getByUuid(uuid);
        if (!searchResult.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = Paths.get(URI.create(searchResult.get().getLocation()));
        long size;
        InputStream inputStream;
        try {
            size = Files.size(filePath);
            inputStream = Files.newInputStream(filePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        String type = searchResult.get().getType();
        return ResponseEntity.ok()
                .contentLength(size)
                .contentType(determineContentType(type))
                .header("Content-Disposition", "attachment; filename=" + uuid + "." + type)
                .body(new InputStreamResource(inputStream));

    }

    private MediaType determineContentType(String type) {
        switch (type.toLowerCase()) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "doc":
                return MediaType.valueOf("application/msword");
            case "docx":
                return MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "xls":
                return MediaType.valueOf("application/vnd.ms-excel");
            case "xlsx":
                return MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "odt":
                return MediaType.valueOf("application/vnd.oasis.opendocument.text");
            case "ods":
                return MediaType.valueOf("application/vnd.oasis.opendocument.spreadsheet");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
