/*
 * #%L
 * eXtended Objects - Neo4j - Gremlin Query Support
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
package com.smbtec.xo.neo4j.query.gremlin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.smbtec.xo.tinkerpop.blueprints.api.annotation.Gremlin;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Edge;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Vertex;
import com.tinkerpop.pipes.util.structures.Table;

public class GremlinQuery implements DatastoreQuery<Gremlin> {

    private static final String g = "g";
    private static final String GREMLIN_GROOVY = "gremlin-groovy";

    private Neo4j2Graph tinkerPopGraph;
    private GraphDatabaseService graphDatabaseService;

    private ScriptEngine engine;

    public GremlinQuery(Neo4jDatastoreSession session) {
        graphDatabaseService = session.getGraphDatabaseService();
        tinkerPopGraph = new Neo4j2Graph(graphDatabaseService);
        engine = new ScriptEngineManager().getEngineByName(GREMLIN_GROOVY);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(final String script, final Map<String, Object> parameters) {
        try {
            final Bindings bindings = createBindings(parameters, tinkerPopGraph);
            final Object result = engine.eval(script, bindings);

            if (result instanceof Table) {
                System.out.println("Table");
            } else if (result instanceof Iterable) {
                return convertIterator(((Iterable<?>) result).iterator());
            } else if (result instanceof Iterator) {
                return convertIterator(((Iterator<?>) result));
            } else if (result instanceof Map) {
                System.out.println("Map");
            }
            return convertSingleObject(result);

        } catch (Exception e) {
            throw new XOException(e.getMessage(), e);
        }
    }

    private ResultIterator<Map<String, Object>> convertSingleObject(final Object data) {
        return new ResultIterator<Map<String, Object>>() {

            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Map<String, Object> next() {
                Map<String, Object> result = new HashMap<>();
                if (data instanceof Neo4j2Vertex) {
                    result.put("node", ((Neo4j2Vertex) data).getRawVertex());
                } else if (data instanceof Neo4j2Edge) {
                    result.put("relationship", ((Neo4j2Edge) data).getRawEdge());
                } else if (data instanceof Neo4j2Graph) {
                    result.put("graph", ((Neo4j2Graph) result).getRawGraph().toString());
                }
                hasNext = false;
                return result;
            }

            @Override
            public void remove() {
                throw new XOException("Remove operation is not supported for query results.");
            }

            @Override
            public void close() {
            }
        };
    }

    private ResultIterator<Map<String, Object>> convertIterator(final Iterator<?> iterator) {
        return new ResultIterator<Map<String, Object>>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                Object data = iterator.next();
                Map<String, Object> result = new HashMap<>();
                if (data instanceof Neo4j2Vertex) {
                    result.put("node", ((Neo4j2Vertex) data).getRawVertex());
                } else if (data instanceof Neo4j2Edge) {
                    result.put("relationship", ((Neo4j2Edge) data).getRawEdge());
                } else if (data instanceof Neo4j2Graph) {
                    result.put("graph", ((Neo4j2Graph) result).getRawGraph().toString());
                }
                return result;
            }

            @Override
            public void remove() {
                throw new XOException("Remove operation is not supported for query results.");
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(final Gremlin query, final Map<String, Object> parameters) {
        return execute(query.value(), parameters);
    }

    private Bindings createBindings(Map params, Neo4j2Graph neo4jGraph) {
        final Bindings bindings = createInitialBinding(neo4jGraph);
        if (params != null) {
            bindings.putAll(params);
        }
        return bindings;
    }

    private Bindings createInitialBinding(Neo4j2Graph neo4jGraph) {
        final Bindings bindings = new SimpleBindings();
        bindings.put(g, neo4jGraph);
        return bindings;
    }

}
