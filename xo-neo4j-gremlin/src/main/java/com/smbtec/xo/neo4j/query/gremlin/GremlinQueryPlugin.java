/*
 * eXtended Objects - Neo4j - Gremlin Query Support
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
package com.smbtec.xo.neo4j.query.gremlin;

import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.api.Neo4jDatastore;

import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.plugin.QueryLanguagePlugin;

import com.smbtec.xo.tinkerpop.blueprints.api.annotation.Gremlin;

public class GremlinQueryPlugin implements QueryLanguagePlugin<Gremlin> {

    @Override
    public Class<Gremlin> init(final Datastore datastore) {
        if (datastore instanceof Neo4jDatastore) {
            return Gremlin.class;
        } else {
            return null;
        }
    }

    @Override
    public DatastoreQuery<Gremlin> createQuery(final DatastoreSession session) {
        return new GremlinQuery((Neo4jDatastoreSession) session);
    }

}
