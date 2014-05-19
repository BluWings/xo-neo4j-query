/*
 * #%L
 * eXtended Objects - Neo4j - Lucene Query Support
 * %%
 * Copyright (C) 2014 SMB GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

/**
 *
 * @author Lars Martin - lars.martin@smb-tec.com
 *
 */
public class LuceneQuery implements DatastoreQuery<Lucene> {

    private final GraphDatabaseService graphDb;

    protected LuceneQuery(final GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public ResultIterator<Map<String, Object>> execute(final Lucene query, final Map<String, Object> parameters) {
        return execute(query.value(), query.type(), parameters);
    }

    public ResultIterator<Map<String, Object>> execute(final String query, final Map<String, Object> parameters) {
        return execute(query, null, parameters);
    }

    private ResultIterator<Map<String, Object>> execute(final String lucene, final Class<?> type, final Map<String, Object> parameters) {
        final IndexHits<Node> hits;
        if (!graphDb.index().getNodeAutoIndexer().isEnabled() && type != null) {
            final Index<Node> nodeIndex = graphDb.index().forNodes(type.getName());
            hits = nodeIndex.query(lucene);
        } else {
            final ReadableIndex<Node> autoNodeIndex = graphDb.index().getNodeAutoIndexer().getAutoIndex();
            hits = autoNodeIndex.query(lucene);
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
}
