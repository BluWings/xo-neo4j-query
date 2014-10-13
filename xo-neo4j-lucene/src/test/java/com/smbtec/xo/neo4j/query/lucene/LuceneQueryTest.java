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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.AutoIndexer;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.smbtec.xo.neo4j.query.lucene.composite.A;

/**
 *
 * @author Lars Martin - lars.martin@smb-tec.com
 *
 */
@RunWith(Parameterized.class)
public class LuceneQueryTest extends AbstractNeo4jXOManagerTest {

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    protected static Collection<Object[]> xoUnits(final Class<?>... types) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY), Arrays.asList(types), Collections.<Class<?>> emptyList(), ValidationMode.AUTO,
                ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    @Before
    public void setup() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();

        // enable legacy auto indexing
        final Neo4jDatastoreSession datastoreSession = xoManager.getDatastoreSession(Neo4jDatastoreSession.class);
        final GraphDatabaseService graphDatabaseService = datastoreSession.getGraphDatabaseService();
        final AutoIndexer<Node> nodeAutoIndexer = graphDatabaseService.index().getNodeAutoIndexer();
        nodeAutoIndexer.startAutoIndexingProperty("name");
        nodeAutoIndexer.setEnabled(true);

        final A a = xoManager.create(A.class);
        a.setName("foo");

        xoManager.currentTransaction().commit();
    }

    public LuceneQueryTest(final XOUnit xoUnit) {
        super(xoUnit);
    }

    @Test
    public void testLuceneQuery() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();

        A match = getXoManager().createQuery("name:fo*", A.class).using(Lucene.class).execute().getSingleResult();

        assertThat(match, not(nullValue()));
        assertThat(match.getName(), equalTo("foo"));

        xoManager.currentTransaction().commit();
    }

}
