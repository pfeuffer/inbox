package de.pfeufferweb.inbox;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FileScannerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private Inbox inbox;
    @Mock
    private DocumentScanner documentScanner;
    @InjectMocks
    private FileScanner fileScanner;

    @Test
    public void shouldHandleEmptyDirectory() throws IOException {
        fileScanner.scan(folder.getRoot().getAbsolutePath());

        verify(inbox, never()).register(any());
    }

    @Test
    public void shouldFindSingleFile() throws IOException {
        folder.newFile("something.pdf");

        fileScanner.scan(folder.getRoot().getAbsolutePath());

        verify(inbox).register(any(Document.class));
    }

    @Test
    public void shouldTraverseInSubDirectories() throws IOException {
        File subdir = folder.newFolder("subdir");
        new File(subdir, "something.pdf").createNewFile();

        fileScanner.scan(folder.getRoot().getAbsolutePath());

        verify(inbox).register(any(Document.class));
    }

    @Before
    public void init() {
        initMocks(this);
    }
}
