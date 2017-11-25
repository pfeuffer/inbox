package de.pfeufferweb.inbox;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class FileTypeExtractorTest {

    @Test
    public void noFileTypeIsEmpty() {
        String type = new FileTypeExtractor().extractFileType("noTypeThere");
        assertThat(type, is(equalTo("")));
    }

    @Test
    public void findSimpleFileType() {
        String type = new FileTypeExtractor().extractFileType("andTheTypeIs.pdf");
        assertThat(type, is(equalTo("pdf")));
    }

    @Test
    public void noTypeIfDotIsAtLastPosition() {
        String type = new FileTypeExtractor().extractFileType("andTheTypeIsEmpty.");
        assertThat(type, is(equalTo("")));
    }
}