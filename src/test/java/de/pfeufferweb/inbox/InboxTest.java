package de.pfeufferweb.inbox;

import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InboxTest {

    private Inbox inbox = new Inbox(new RAMDirectory());

    @Test
    public void shouldFindNothingIfNoMatchExists() {
        inbox.register(mockDocument("This is a simple document", "in/some/path"));

        SearchResult result = inbox.search("complex");

        assertThat(result.getItems().size(), is(0));
    }

    @Test
    public void shouldFindExistingItem() {
        inbox.register(mockDocument("This is a simple document", "in/some/path"));

        SearchResult result = inbox.search("document");

        assertThat(result.getItems().size(), is(1));
    }

    @Test
    public void shouldFindCorrectItemWithConjunction() {
        inbox.register(mockDocument("This is a rather small document", "in/other/path"));
        inbox.register(mockDocument("This is a simple document", "in/some/path"));

        SearchResult result = inbox.search("document AND simple");

        assertThat(result.getItems().size(), is(1));
    }

    @Test
    public void shouldFindCorrectItemsWithDisjunction() {
        inbox.register(mockDocument("This is a rather small document", "in/other/path"));
        inbox.register(mockDocument("This is a simple document", "in/some/path"));
        inbox.register(mockDocument("Neither small nor simple", "in/further/path"));
        inbox.register(mockDocument("And something really big", "in/more/path"));

        SearchResult result = inbox.search("small OR simple");

        assertThat(result.getItems().size(), is(3));
    }

    @Test
    public void shouldUseImplicitDisjunction() {
        inbox.register(mockDocument("This is a rather small document", "in/other/path"));
        inbox.register(mockDocument("This is a simple document", "in/some/path"));
        inbox.register(mockDocument("Neither small nor simple", "in/further/path"));
        inbox.register(mockDocument("And something really big", "in/more/path"));

        SearchResult result = inbox.search("small simple");

        assertThat(result.getItems().size(), is(3));
    }

    @Test
    public void shouldGetExistingItem() {
        inbox.register(mockDocument("This is a simple document", "/in/some/path"));

        Optional<URI> result = inbox.getUri("/in/some/path");

        assertThat(result.get(), is(equalTo(URI.create("/in/some/path"))));
    }

    @Test
    public void shouldNotFindNotExistingItem() {
        inbox.register(mockDocument("This is a simple document", "/in/some/path"));

        Optional<URI> result = inbox.getUri("/in/some/other");

        assertThat(result.isPresent(), is(false));
    }

    private Document mockDocument(String content, String path) {
        Document mock = mock(Document.class);
        when(mock.getUUID()).thenReturn(path);
        when(mock.getContent()).thenReturn(content);
        Location location = mock(Location.class);
        when(mock.getLocation()).thenReturn(location);
        when(location.getLocationString()).thenReturn(path);
        return mock;
    }
}