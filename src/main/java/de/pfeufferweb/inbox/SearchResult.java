package de.pfeufferweb.inbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SearchResult {

    private final Collection<Document> items;

    public SearchResult(Collection<Document> items) {
        this.items = Collections.unmodifiableCollection(new ArrayList<>(items));
    }

    public Collection<Document> getDocuments() {
        return items;
    }
}
