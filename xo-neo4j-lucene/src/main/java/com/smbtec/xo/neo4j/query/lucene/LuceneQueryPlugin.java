/*
 * eXtended Objects - Neo4j - Lucene Query Support
 *
 * Copyright (C) 2014 SMB GmbH
 *
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
 *
 */
package com.smbtec.xo.neo4j.query.lucene;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.neo4j.impl.datastore.Neo4jDatastore;
import com.buschmais.xo.neo4j.impl.datastore.AbstractNeo4jDatastoreSession;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.plugin.QueryLanguagePlugin;

/**
 *
 * @author Lars Martin - lars.martin@smb-tec.com
 *
 */
public class LuceneQueryPlugin implements QueryLanguagePlugin<Lucene> {

    @Override
    public Class<Lucene> init(final Datastore datastore) {
        if (datastore instanceof Neo4jDatastore) {
            return Lucene.class;
        } else {
            return null;
        }
    }

    @Override
    public DatastoreQuery<Lucene> createQuery(final DatastoreSession session) {
        return new LuceneQuery(((AbstractNeo4jDatastoreSession<GraphDatabaseService>) session).getGraphDatabaseService());
    }

}
