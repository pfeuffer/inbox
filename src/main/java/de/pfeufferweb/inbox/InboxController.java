package de.pfeufferweb.inbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping(value = "/get", produces = {"application/json", "application/xml"})
    @ResponseBody
    public SearchResult get(@RequestParam(name = "location", required = true) String location) {
        return inbox.get(location);
    }

    @GetMapping(value = "/get", produces = {"text/html"})
    public String get(@RequestParam(name = "location", required = true) String location, Model model) {
        SearchResult result = get(location);
        model.addAttribute("items", result.getItems());
        model.addAttribute("size", result.getItems().size());
        return "get";
    }
}
