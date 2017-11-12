package de.pfeufferweb.inbox;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InboxTest {

    @Test
    public void shouldFindNothingIfNoMatchExists() {
        Inbox inbox = new Inbox();
        inbox.register(mockDocument("This is a simple document", "in/some/path"));

        SearchResult result = inbox.search("complex");

        assertThat(result.getItems().size(), is(0));
    }

    @Test
    public void shouldFindExistingItem() {
        Inbox inbox = new Inbox();
        inbox.register(mockDocument("This is a simple document", "in/some/path"));

        SearchResult result = inbox.search("document");

        assertThat(result.getItems().size(), is(1));
    }

    @Test
    public void shouldFindCorrectItemWithConjunction() {
        Inbox inbox = new Inbox();
        inbox.register(mockDocument("This is a rather small document", "in/other/path"));
        inbox.register(mockDocument("This is a simple document", "in/some/path"));

        SearchResult result = inbox.search("document AND simple");

        assertThat(result.getItems().size(), is(1));
    }

    @Test
    public void shouldFindCorrectItemsWithDisjunction() {
        Inbox inbox = new Inbox();
        inbox.register(mockDocument("This is a rather small document", "in/other/path"));
        inbox.register(mockDocument("This is a simple document", "in/some/path"));
        inbox.register(mockDocument("Neither small nor simple", "in/some/path"));
        inbox.register(mockDocument("And something really big", "in/some/path"));

        SearchResult result = inbox.search("small OR simple");

        assertThat(result.getItems().size(), is(3));
    }

    @Test
    public void shouldUseImplicitDisjunction() {
        Inbox inbox = new Inbox();
        inbox.register(mockDocument("This is a rather small document", "in/other/path"));
        inbox.register(mockDocument("This is a simple document", "in/some/path"));
        inbox.register(mockDocument("Neither small nor simple", "in/some/path"));
        inbox.register(mockDocument("And something really big", "in/some/path"));

        SearchResult result = inbox.search("small simple");

        assertThat(result.getItems().size(), is(3));
    }

    private Document mockDocument(String content, String path) {
        Document mock = mock(Document.class);
        when(mock.getContent()).thenReturn(content);
        Location location = mock(Location.class);
        when(mock.getLocation()).thenReturn(location);
        when(location.getLocation()).thenReturn(path);
        return mock;
    }
}