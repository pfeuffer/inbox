package de.pfeufferweb.inbox;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Inbox {

    private final StandardAnalyzer analyzer = new StandardAnalyzer();
    private final Directory index;

    @Autowired
    public Inbox(Directory index) {
        this.index = index;
    }

    public void register(Document document) {
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(index, config);
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            doc.add(new StringField("location", document.getLocation().getLocation(), Field.Store.YES));
            doc.add(new LongPoint("modified", document.getLastModified()));
            doc.add(new TextField("content", document.getContent(), Field.Store.YES));
            writer.updateDocument(new Term("location", document.getLocation().getLocation()), doc);
            writer.commit();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("could not index document", e);
        }
    }

    public SearchResult get(String location) {
        try {
            Query query = new TermQuery(new Term("location", location));
            return executeQuery(query);
        } catch (Exception e) {
            throw new RuntimeException("error loading location " + location, e);
        }
    }

    public SearchResult search(String search) {
        try {
            Query query = new QueryParser("content", analyzer).parse(search);
            return executeQuery(query);
        } catch (Exception e) {
            throw new RuntimeException("error searching for " + search, e);
        }
    }

    private SearchResult executeQuery(Query query) throws IOException {
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(100);
        indexSearcher.search(query, collector);
        ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
        List<SearchResult.SearchItem> items =
                Arrays.stream(scoreDocs)
                        .map(d -> createSearchItem(indexSearcher, d))
                        .collect(Collectors.toList());
        return new SearchResult(items);
    }

    private SearchResult.SearchItem createSearchItem(IndexSearcher indexSearcher, ScoreDoc d) {
        return new SearchResult.SearchItem(readField(indexSearcher, d, "content"), readField(indexSearcher, d, "location"));
    }

    private String readField(IndexSearcher indexSearcher, ScoreDoc doc, String field) {
        try {
            return indexSearcher.doc(doc.doc).get(field);
        } catch (IOException e) {
            throw new RuntimeException("error reading document " + doc.doc);
        }
    }
}
