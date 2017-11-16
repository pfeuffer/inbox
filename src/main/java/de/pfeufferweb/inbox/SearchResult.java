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
        private final String uuid;
        private final String content;
        private final String location;

        public SearchItem(String uuid, String content, String location) {
            this.uuid = uuid;
            this.content = content;
            this.location = location;
        }

        public String getUuid() {
            return uuid;
        }

        public String getContent() {
            return content;
        }

        public String getLocation() {
            return location;
        }
    }
}
