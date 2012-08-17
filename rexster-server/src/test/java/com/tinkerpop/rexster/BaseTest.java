package com.tinkerpop.rexster;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.sail.SailGraph;
import com.tinkerpop.blueprints.impls.sail.SailGraphFactory;
import com.tinkerpop.blueprints.impls.sail.impls.MemoryStoreSailGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.rexster.server.DefaultRexsterApplication;
import com.tinkerpop.rexster.server.RexsterApplication;
import com.tinkerpop.rexster.util.StatisticsHelper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public abstract class BaseTest {

    private static Logger logger = Logger.getLogger(BaseTest.class.getName());
    protected static final String graphName = "graph";

    protected Mockery mockery = new JUnit4Mockery();
    protected StatisticsHelper sh = new StatisticsHelper();

    protected Graph toyGraph;
    protected Graph emptyGraph;
    protected RexsterApplication raToyGraph;
    protected RexsterApplication raEmptyGraph;

    @Before
    public void init() {
        this.mockery = new JUnit4Mockery();
        this.createDefaultGraphs(TinkerGraphFactory.createTinkerGraph(), new TinkerGraph());
    }

    @After
    public void tearDown() {
        this.toyGraph.shutdown();
        this.emptyGraph.shutdown();
    }

    public static void printPerformance(String name, Integer events, String eventName, double timeInMilliseconds) {
        if (null != events)
            logger.info(name + ": " + events + " " + eventName + " in " + timeInMilliseconds + "ms");
        else
            logger.info(name + ": " + eventName + " in " + timeInMilliseconds + "ms");
    }

    protected ResourceHolder<PrefixResource> constructPrefixResource() {
        final SailGraph sg = new MemoryStoreSailGraph();
        SailGraphFactory.createTinkerGraph(sg);

        // have to reset with a sail graph for prefixes to work. empty graph is not used
        // in these tests so no need to reset.
        this.createDefaultGraphs(sg, this.emptyGraph);

        final UriInfo uri = this.mockery.mock(UriInfo.class);
        final URI requestUriPath = URI.create("http://localhost/graphs/graph/prefixes");
        final HttpServletRequest httpServletRequest = this.mockery.mock(HttpServletRequest.class);

        this.mockery.checking(new Expectations() {{
            allowing(httpServletRequest).getParameterMap();
            will(returnValue(new HashMap<String, String>()));
            allowing(uri).getAbsolutePath();
            will(returnValue(requestUriPath));
        }});

        final PrefixResource resource = new PrefixResource(uri, httpServletRequest, this.raToyGraph);
        return new ResourceHolder<PrefixResource>(resource, null);
    }

    protected ResourceHolder<VertexResource> constructVertexResourceWithToyGraph() {
        return this.constructVertexResource(true, new HashMap<String, Object>(), MediaType.APPLICATION_JSON_TYPE);
    }

    protected ResourceHolder<VertexResource> constructVertexResourceWithEmptyGraph() {
        return this.constructVertexResource(false, new HashMap<String, Object>(), MediaType.APPLICATION_JSON_TYPE);
    }

    protected ResourceHolder<VertexResource> constructVertexResource(final boolean useToyGraph,
                                                                     final HashMap<String, Object> parameters){
        return this.constructVertexResource(useToyGraph, parameters, MediaType.APPLICATION_JSON_TYPE);
    }

    protected ResourceHolder<VertexResource> constructVertexResource(final boolean useToyGraph,
                                                                     final HashMap<String, Object> parameters,
                                                                     final MediaType mediaType) {
        final UriInfo uri = this.mockery.mock(UriInfo.class);
        final HttpServletRequest httpServletRequest = this.mockery.mock(HttpServletRequest.class);

        final Request request = this.mockery.mock(Request.class);
        final Variant variantJson = new Variant(mediaType, null, null);
        final URI requestUriPath = URI.create("http://localhost/graphs/graph/vertices");

        this.mockery.checking(new Expectations() {{
            allowing(httpServletRequest).getParameterMap();
            will(returnValue(parameters));
            allowing(request).selectVariant(with(any(List.class)));
            will(returnValue(variantJson));
            allowing(uri).getAbsolutePath();
            will(returnValue(requestUriPath));
        }});

        final VertexResource resource = useToyGraph ? new VertexResource(uri, httpServletRequest, this.raToyGraph)
                : new VertexResource(uri, httpServletRequest, this.raEmptyGraph);
        return new ResourceHolder<VertexResource>(resource, request);
    }

    protected void assertFoundElementsInResults(final JSONArray jsonResultArray, final String elementType,
                                                final String... expectedIds) {
        Assert.assertNotNull(jsonResultArray);
        Assert.assertEquals(expectedIds.length, jsonResultArray.length());

        final List<String> foundIds = new ArrayList<String>();
        for (int ix = 0; ix < jsonResultArray.length(); ix++) {
            final JSONObject jsonResult = jsonResultArray.optJSONObject(ix);
            Assert.assertNotNull(jsonResult);
            Assert.assertEquals(elementType, jsonResult.optString(Tokens._TYPE));
            Assert.assertTrue(jsonResult.has(Tokens._ID));
            foundIds.add(jsonResult.optString(Tokens._ID));
        }

        for (String expectedId : expectedIds) {
            Assert.assertTrue(foundIds.contains(expectedId));
        }
    }

    private void createDefaultGraphs(final Graph toy, final Graph empty) {
        this.toyGraph = toy;
        this.emptyGraph = empty;

        this.raToyGraph = new DefaultRexsterApplication(graphName, toy);
        this.raEmptyGraph = new DefaultRexsterApplication(graphName, empty);

        final List<String> namespaces = new ArrayList<String>();
        namespaces.add("*:*");
        this.raToyGraph.getApplicationGraph(graphName).loadAllowableExtensions(namespaces);
        this.raEmptyGraph.getApplicationGraph(graphName).loadAllowableExtensions(namespaces);
    }
}
