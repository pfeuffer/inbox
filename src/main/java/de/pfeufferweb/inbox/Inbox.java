package de.pfeufferweb.inbox;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
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

import javax.print.Doc;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Inbox {

    public static final String FIELD_NAME_UUID = "uuid";
    public static final String FIELD_NAME_CONTENT = "content";
    public static final String FIELD_NAME_LOCATION = "location";
    public static final String FIELD_NAME_TYPE = "type";
    public static final String FIELD_NAME_MODIFIED = "modified";
    public static final String FIELD_NAME_MODIFIED_INDEX = "modifiedIndex";
    private final StandardAnalyzer analyzer = new StandardAnalyzer();
    private final Directory index;

    private final Set<Location> knownDocuments = new HashSet<>();

    @Autowired
    public Inbox(Directory index) {
        this.index = index;
    }

    public void register(Document document) {
        if (contains(document.getLocation())) {
            return;
        }
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(index, config);
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            doc.add(new StringField(FIELD_NAME_UUID, document.getUUID(), Store.YES));
            doc.add(new StringField(FIELD_NAME_LOCATION, document.getLocation().getLocationString(), Store.YES));
            doc.add(new StringField(FIELD_NAME_TYPE, document.getFileType(), Store.YES));
            doc.add(new LongPoint(FIELD_NAME_MODIFIED_INDEX, document.getLastModified()));
            doc.add(new StoredField(FIELD_NAME_MODIFIED, document.getLastModified()));
            doc.add(new TextField(FIELD_NAME_CONTENT, document.getContent(), Store.YES));
            writer.updateDocument(new Term(FIELD_NAME_LOCATION, document.getLocation().getLocationString()), doc);
            writer.commit();
            writer.close();
            knownDocuments.add(document.getLocation());
        } catch (Exception e) {
            throw new RuntimeException("could not index document", e);
        }
    }

    public boolean contains(Location location) {
        if (knownDocuments.contains(location)) {
            return true;
        } else {
            Optional<URI> uri = getByLocation(location.getLocationString());
            if (uri.isPresent()) {
                knownDocuments.add(new Location(uri.get()));
                return true;
            } else {
                return false;
            }
        }
    }

    public Optional<URI> getByLocation(String location) {
        try {
            Query query = new TermQuery(new Term(FIELD_NAME_LOCATION, location));
            return executeQuery(query)
                    .getDocuments()
                    .stream()
                    .findFirst()
                    .map(Document::getLocation)
                    .map(Location::getLocationString)
                    .map(URI::create);
        } catch (IndexNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("error looking up location " + location, e);
        }
    }

    public Optional<Document> getByUuid(String uuid) {
        try {
            Query query = new TermQuery(new Term(FIELD_NAME_UUID, uuid));
            return executeQuery(query)
                    .getDocuments()
                    .stream()
                    .findFirst();
        } catch (IndexNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("error looking up by uuid " + uuid, e);
        }
    }

    public SearchResult search(String search) {
        try {
            Query query = new QueryParser(FIELD_NAME_CONTENT, analyzer).parse(search);
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
        List<Document> items =
                Arrays.stream(scoreDocs)
                        .map(d -> createSearchItem(indexSearcher, d))
                        .collect(Collectors.toList());
        reader.close();
        return new SearchResult(items);
    }

    private Document createSearchItem(IndexSearcher indexSearcher, ScoreDoc d) {
        return new Document.Builder()
                .uuid(readField(indexSearcher, d, FIELD_NAME_UUID))
                .content(readField(indexSearcher, d, FIELD_NAME_CONTENT))
                .location(new Location(URI.create(readField(indexSearcher, d, FIELD_NAME_LOCATION))))
                .fileType(readField(indexSearcher, d, FIELD_NAME_TYPE))
                .lastModified(Long.parseLong(readField(indexSearcher, d, FIELD_NAME_MODIFIED)))
                .build();
    }

    private String readField(IndexSearcher indexSearcher, ScoreDoc doc, String field) {
        try {
            return indexSearcher.doc(doc.doc).get(field);
        } catch (IOException e) {
            throw new RuntimeException("error reading document " + doc.doc);
        }
    }
}
