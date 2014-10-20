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

import java.util.HashMap;
import java.util.Map;

import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.smbtec.xo.tinkerpop.blueprints.api.annotation.Gremlin;
import com.smbtec.xo.tinkerpop.blueprints.impl.GremlinQuery;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Edge;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Vertex;

/**
 *
 * @author Lars Martin - lars.martin@smb-tec.com
 *
 */
public class Neo4jGremlinQuery extends GremlinQuery implements DatastoreQuery<Gremlin> {

    public Neo4jGremlinQuery(Neo4jDatastoreSession session) {
        super(new Neo4j2Graph(session.getGraphDatabaseService()));
    }

    @Override
    public Map<String, Object> entityRepresentation(Object entity) {
        Map<String, Object> result = new HashMap<>();
        if (entity instanceof Neo4j2Vertex) {
            result.put(NODE_COLUMN_NAME, ((Neo4j2Vertex) entity).getRawVertex());
            return result;
        } else if (entity instanceof Neo4j2Edge) {
            result.put(EDGE_COLUMN_NAME, ((Neo4j2Edge) entity).getRawEdge());
            return result;
        } else if (entity instanceof Neo4j2Graph) {
            result.put(GRAPH_COLUMN_NAME, ((Neo4j2Graph) entity).getRawGraph().toString());
            return result;
        } else {
            return super.entityRepresentation(entity);
        }
    }

}
