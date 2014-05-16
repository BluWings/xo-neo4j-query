package com.smbtec.xo.neo4j.query.lucene;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.neo4j.impl.datastore.AbstractNeo4jDatastore;
import com.buschmais.xo.neo4j.impl.datastore.AbstractNeo4jDatastoreSession;

import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.plugin.QueryPlugin;

public class LuceneQueryPlugin implements QueryPlugin<AbstractNeo4jDatastore, Lucene> {

    public Class<Lucene> init(final Datastore datastore) {
        if (datastore instanceof AbstractNeo4jDatastore) {
            return Lucene.class;
        } else {
            return null;
        }
    }

    public DatastoreQuery<Lucene> createQuery(final DatastoreSession session) {
        return new LuceneQuery(((AbstractNeo4jDatastoreSession<GraphDatabaseService>) session).getGraphDatabaseService());
    }

}
