package de.pfeufferweb.inbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SearchResult {

    private final Collection<SearchItem> items;

    public SearchResult(Collection<SearchItem> items) {
        this.items = Collections.unmodifiableCollection(new ArrayList<>(items));
    }

    public Collection<SearchItem> getItems() {
        return items;
    }

    public static class SearchItem {
        private final String content;
        private final String path;

        public SearchItem(String content, String path) {
            this.content = content;
            this.path = path;
        }

        public String getContent() {
            return content;
        }

        public String getPath() {
            return path;
        }
    }
}
