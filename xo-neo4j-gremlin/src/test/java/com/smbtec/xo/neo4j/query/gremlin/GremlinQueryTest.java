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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.api.Neo4jXOProvider;
import com.buschmais.xo.test.AbstractXOManagerTest;
import com.smbtec.xo.neo4j.query.gremlin.composite.A;
import com.smbtec.xo.tinkerpop.blueprints.api.annotation.Gremlin;

@RunWith(Parameterized.class)
public class GremlinQueryTest extends AbstractXOManagerTest {

    protected enum Neo4jDatabase implements AbstractXOManagerTest.Database {
        MEMORY("memory:///"), REST("http://localhost:7474/db/data");
        private URI uri;

        private Neo4jDatabase(final String uri) {
            try {
                this.uri = new URI(uri);
            } catch (final URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public Class<?> getProvider() {
            return Neo4jXOProvider.class;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    protected static Collection<Object[]> xoUnits(final Class<?>... types) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY), Arrays.asList(types), Collections.<Class<?>> emptyList(), ValidationMode.AUTO,
                ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    private static WrappingNeoServer server;

    @BeforeClass
    public static void startServer() {
        final GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        server = new WrappingNeoServer((GraphDatabaseAPI) graphDatabaseService);
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    @Before
    public void setup() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();

        final A a = xoManager.create(A.class);
        a.setName("foo");

        xoManager.currentTransaction().commit();
    }

    public GremlinQueryTest(final XOUnit xoUnit) {
        super(xoUnit);
    }

    @Test
    public void testGremlinQuery() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        xoManager.createQuery("g.V").using(Gremlin.class).execute();
        xoManager.currentTransaction().commit();
    }

    @Test
    public void testGremlinQuery1() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        xoManager.createQuery("g.V").using(Gremlin.class).execute();
        xoManager.currentTransaction().commit();
    }

    @Override
    protected void dropDatabase() {
        final XOManager manager = getXoManager();
        manager.currentTransaction().begin();
        manager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        manager.createQuery("MATCH (n) DELETE n").execute();
        manager.currentTransaction().commit();
    }

}
