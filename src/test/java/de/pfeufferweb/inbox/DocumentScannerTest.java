package de.pfeufferweb.inbox;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DocumentScannerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldParseWordFile() throws IOException {
        Path wordFile = copy("Simple_word_file.docx");

        Document wordDocument = new DocumentScanner().read(wordFile).get();

        assertThat(cleanedContent(wordDocument), is(equalTo("Simple word file")));
        assertThat(wordDocument.getFileType(), is(equalTo("docx")));
        assertThat(wordDocument.getLocation().getLocationString()
                .endsWith("Simple_word_file.docx"), is(true));
    }

    @Test
    public void shouldParsePdfFile() throws IOException {
        Path pdfFile = copy("Simple_pdf_file.pdf");

        Document wordDocument = new DocumentScanner().read(pdfFile).get();

        assertThat(cleanedContent(wordDocument), is(equalTo("Simple pdf file")));
        assertThat(wordDocument.getFileType(), is(equalTo("pdf")));
        assertThat(wordDocument.getLocation().getLocationString()
                .endsWith("Simple_pdf_file.pdf"), is(true));
    }

    @Test
    public void shouldNotFailForIllegalFile() throws IOException {
        Path pdfFile = copy("illegal.pdf");

        Optional<Document> illegal = new DocumentScanner().read(pdfFile);

        assertThat(illegal.isPresent(), is(false));
    }

    private Path copy(String fileName) throws IOException {
        File tempDir = temporaryFolder.getRoot();
        Path newFile = new File(tempDir, fileName).toPath();
        Files.copy(DocumentScannerTest.class.getResourceAsStream("/files/" +
                fileName), newFile);
        return newFile;
    }

    private String cleanedContent(Document wordDocument) {
        return wordDocument.getContent().replaceAll("\\n+", "").trim();
    }
}