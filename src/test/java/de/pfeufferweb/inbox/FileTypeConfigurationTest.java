package de.pfeufferweb.inbox;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FileTypeConfigurationTest {

    @Test
    public void shouldAcceptSingleType() {
        FileTypeChecker checker = new FileTypeConfiguration().supportedFileTypes("type");
        assertThat(checker.supported(".type"), is(true));
    }

    @Test
    public void shouldAcceptMultipleTypesWithWihespaces() {
        FileTypeChecker checker = new FileTypeConfiguration().supportedFileTypes("type1 , type2 ");
        assertThat(checker.supported(".type1"), is(true));
        assertThat(checker.supported(".type2"), is(true));
        assertThat(checker.supported(".type3"), is(false));
    }

    @Test
    public void shouldTransformToLowerCase() {
        FileTypeChecker checker = new FileTypeConfiguration().supportedFileTypes("TYPE");
        assertThat(checker.supported(".type"), is(true));
        assertThat(checker.supported(".TYPE"), is(true));
        assertThat(checker.supported(".TypE"), is(true));
    }
}