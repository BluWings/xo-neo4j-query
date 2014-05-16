package com.smbtec.xo.neo4j.query.lucene;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastoreQuery;

public class LuceneQuery implements DatastoreQuery<Lucene> {

    private final GraphDatabaseService graphDb;

    protected LuceneQuery(final GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public ResultIterator<Map<String, Object>> execute(final Lucene query, final Map<String, Object> parameters) {
        final String luceneQuery = query.value();
        final Class<?> type = query.type();
        final IndexHits<Node> hits;
        if (!graphDb.index().getNodeAutoIndexer().isEnabled()) {
            final Index<Node> nodeIndex = graphDb.index().forNodes(type.getName());
            hits = nodeIndex.query(luceneQuery);
        } else {
            final ReadableIndex<Node> autoNodeIndex = graphDb.index().getNodeAutoIndexer().getAutoIndex();
            hits = autoNodeIndex.query(luceneQuery);
        }
        return new ResultIterator<Map<String, Object>>() {

            public boolean hasNext() {
                return hits.hasNext();
            }

            public Map<String, Object> next() {
                final Map<String, Object> result = new HashMap<String, Object>();
                result.put("result", hits.next());
                return result;
            }

            public void remove() {
                throw new XOException("Remove operation is not supported for query results.");
            }

            public void close() {
                hits.close();
            }
        };

    }

    public ResultIterator<Map<String, Object>> execute(final String query, final Map<String, Object> parameters) {
        throw new XOException("");
    }

}
