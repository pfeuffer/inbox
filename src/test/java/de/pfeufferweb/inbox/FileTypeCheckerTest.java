package de.pfeufferweb.inbox;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FileTypeCheckerTest {

    @Test
    public void shouldAcceptSupportedType() {
        FileTypeChecker fileTypeChecker = new FileTypeChecker(Arrays.asList("pdf", "doc"));
        assertThat(fileTypeChecker.supported("in/some/path/there.is.a.file.pdf"), is(true));
    }

    @Test
    public void shouldRejectUnsupportedType() {
        FileTypeChecker fileTypeChecker = new FileTypeChecker(Arrays.asList("pdf", "doc"));
        assertThat(fileTypeChecker.supported("in/some/path/there.is.a.file.docx"), is(false));
    }
}