package de.pfeufferweb.inbox;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FileSystemScannerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private Inbox inbox;
    @Mock
    private DocumentScanner documentScanner;
    @Mock
    private FileTypeChecker checker;
    @Mock
    private Location location;

    private FileSystemScanner fileSystemScanner;

    @Test
    public void shouldHandleEmptyDirectory() throws IOException {
        fileSystemScanner.scan();

        verify(inbox, never()).register(any());
    }

    @Test
    public void shouldFindSingleFile() throws IOException {
        when(checker.supported("something.pdf")).thenReturn(true);
        folder.newFile("something.pdf");

        fileSystemScanner.scan();

        verify(inbox).register(any(Document.class));
    }

    @Test
    public void shouldTraverseInSubDirectories() throws IOException {
        when(checker.supported("something.pdf")).thenReturn(true);
        File subdir = folder.newFolder("subdir");
        new File(subdir, "something.pdf").createNewFile();

        fileSystemScanner.scan();

        verify(inbox).register(any(Document.class));
    }

    @Test
    public void shouldAddFileOnlyOnce() throws IOException {
        when(checker.supported("something.pdf")).thenReturn(true);
        File subdir = folder.newFolder("subdir");
        File file = new File(subdir, "something.pdf");
        file.createNewFile();

        when(inbox.contains(location)).thenReturn(false, true);

        fileSystemScanner.scan();
        fileSystemScanner.scan();

        verify(inbox).register(any(Document.class));
    }

    @Before
    public void init() {
        initMocks(this);
        this.fileSystemScanner = new FileSystemScanner(inbox, documentScanner, checker, folder.getRoot().getAbsolutePath());
        when(documentScanner.createLocation(any(Path.class))).thenReturn(location);
    }
}
