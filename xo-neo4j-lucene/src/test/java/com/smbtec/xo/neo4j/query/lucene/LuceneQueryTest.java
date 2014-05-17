package com.smbtec.xo.neo4j.query.lucene;

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

import com.smbtec.xo.neo4j.query.lucene.composite.A;

/**
 *
 * @author Lars Martin - lars.martin@smb-tec.com
 *
 */
@RunWith(Parameterized.class)
public class LuceneQueryTest extends AbstractXOManagerTest {

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

        public URI getUri() {
            return uri;
        }

        public Class<?> getProvider() {
            return Neo4jXOProvider.class;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    protected static Collection<Object[]> xoUnits(final Class<?>... types) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY, Neo4jDatabase.REST), Arrays.asList(types), Collections.<Class<?>> emptyList(), ValidationMode.AUTO,
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
    public void testLuceneAnnotatedQuery() {
        final XOManager xoManager = getXoManager();
        final Lucene lucene = new Lucene() {

            public Class<? extends Annotation> annotationType() {
                return null;
            }

            public String value() {
                return "name:fo*";
            }

            public Class<?> type() {
                return A.class;
            }
        };
        xoManager.currentTransaction().begin();

        final LuceneQuery luceneQuery = getLuceneQuery();
        final ResultIterator<Map<String, Object>> result = luceneQuery.execute(lucene, Collections.<String, Object> emptyMap());
        assertThat(result.hasNext(), is(true));

        xoManager.currentTransaction().commit();
    }

    @Test
    public void testLuceneQuery() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();

        final LuceneQuery luceneQuery = getLuceneQuery();
        final ResultIterator<Map<String, Object>> result = luceneQuery.execute("name:fo*", Collections.<String, Object> emptyMap());
        assertThat(result.hasNext(), is(true));

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

    private LuceneQuery getLuceneQuery() {
        // plugin registry not yet implemented, manually create lucene query object
        final Neo4jDatastoreSession datastoreSession = getXoManager().getDatastoreSession(Neo4jDatastoreSession.class);
        final GraphDatabaseService graphDatabaseService = datastoreSession.getGraphDatabaseService();
        return new LuceneQuery(graphDatabaseService);
    }

}
