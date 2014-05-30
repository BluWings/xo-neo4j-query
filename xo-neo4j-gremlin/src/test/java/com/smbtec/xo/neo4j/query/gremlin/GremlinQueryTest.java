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

import static org.hamcrest.CoreMatchers.is;
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

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.Query.Result.CompositeRowObject;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.smbtec.xo.neo4j.query.gremlin.composite.A;
import com.smbtec.xo.tinkerpop.blueprints.api.annotation.Gremlin;

@RunWith(Parameterized.class)
public class GremlinQueryTest extends AbstractNeo4jXOManagerTest {

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
        Result<CompositeRowObject> result = xoManager.createQuery("g.V").using(Gremlin.class).execute();
        assertThat(result.hasResult(), is(true));
        A match = result.getSingleResult().get("node", A.class);
        assertThat(match, not(nullValue()));
        assertThat(match.getName(), equalTo("foo"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void testGremlinQuerySingleResult() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A match = xoManager.createQuery("g.v(0)", A.class).using(Gremlin.class).execute().getSingleResult();
        assertThat(match, not(nullValue()));
        assertThat(match.getName(), equalTo("foo"));
        xoManager.currentTransaction().commit();
    }

}
