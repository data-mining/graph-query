package com.tinkerpop.graph.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.TitanVertexProperty;

import org.apache.tinkerpop.gremlin.process.traversal.Order;

/**
 * Ref : https://gist.github.com/freemo/cb39a4887650064d2cb7
 * http://tinkerpop.incubator.apache.org/docs/3.0.0.M9-incubating/
 * http://s3.thinkaurelius.com/docs/titan/1.0.0/getting-started.html#
 * _loading_the_graph_of_the_gods_into_titan
 *
 */
public class SimpleTitanQuery2 {
	public static void main(String[] args) {
		System.out.println("Hello Universe!");
		TitanGraph graph = null;
		try {

			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.backend", "cassandra");
			conf.setProperty("storage.hostname", "localhost");
			graph = TitanFactory.open(conf);

			// CLEANUP
			if (graph != null) {
				Iterator<Vertex> vertices = graph.vertices();
				while (vertices.hasNext()) {
					Vertex v = vertices.next();
					v.remove();
				}
				Iterator<Edge> edges = graph.edges();
				while (edges.hasNext()) {
					Edge e = edges.next();
					e.remove();
				}
			}
			// Build the Graph
			generateTheCrew(graph);

			// Traverse the Graph

			GraphTraversalSource g = graph.traversal();
			System.out.println("*****************************");
			g.V().out("develops").aggregate("x").by("name").cap("x").forEachRemaining(System.out::println);
			
			System.out.println("*****************************");
			

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			graph.close();
		}
	}

	public static void generateTheCrew(final TitanGraph g) {
		
        final TitanVertex marko = g.addVertex(T.label, "person", "name", "marko");
        final TitanVertex stephen = g.addVertex( T.label, "person", "name", "stephen");
        final TitanVertex matthias = g.addVertex(T.label, "person", "name", "matthias");
        final TitanVertex daniel = g.addVertex(T.label, "person", "name", "daniel");
        final TitanVertex gremlin = g.addVertex(T.label, "software", "name", "gremlin");
        final TitanVertex tinkergraph = g.addVertex(T.label, "software", "name", "tinkergraph");
        
        //PropertyKey locationKey = g.makePropertyKey("location").dataType(String.class).make();

        TitanVertexProperty<String> locationProp = marko.property("location", "san diego");
        locationProp.property("location", "san diego");
        locationProp.property("creationtime", System.currentTimeMillis());
        locationProp.property("dead", false);
        marko.property("startTime", 1997);
        marko.property("endTime", 2001);

        ///////
        marko.property("location", "santa cruz", "startTime", 2001, "endTime", 2004);
        marko.property("location", "brussels", "startTime", 2004, "endTime", 2005);
        marko.property("location", "santa fe", "startTime", 2005);

        stephen.property("location", "centreville", "startTime", 1990, "endTime", 2000);
        stephen.property("location", "dulles", "startTime", 2000, "endTime", 2006);
        stephen.property("location", "purcellville", "startTime", 2006);

        matthias.property("location", "bremen", "startTime", 2004, "endTime", 2007);
        matthias.property("location", "baltimore", "startTime", 2007, "endTime", 2011);
        matthias.property("location", "oakland", "startTime", 2011, "endTime", 2014);
        matthias.property("location", "seattle", "startTime", 2014);

        daniel.property("location", "spremberg", "startTime", 1982, "endTime", 2005);
        daniel.property("location", "kaiserslautern", "startTime", 2005, "endTime", 2009);
        daniel.property("location", "aachen", "startTime", 2009);

        marko.addEdge("develops", gremlin,  "since", 2009);
        marko.addEdge("develops", tinkergraph,  "since", 2010);
        marko.addEdge("uses", gremlin,  "skill", 4);
        marko.addEdge("uses", tinkergraph,  "skill", 5);

        stephen.addEdge("develops", gremlin, "since", 2010);
        stephen.addEdge("develops", tinkergraph,  "since", 2011);
        stephen.addEdge("uses", gremlin, "skill", 5);
        stephen.addEdge("uses", tinkergraph,  "skill", 4);

        matthias.addEdge("develops", gremlin,  "since", 2012);
        matthias.addEdge("uses", gremlin,  "skill", 3);
        matthias.addEdge("uses", tinkergraph,  "skill", 3);

        //daniel.addEdge("uses", gremlin,  "skill", 5);
        daniel.addEdge("uses", tinkergraph,  "skill", 3);

        gremlin.addEdge("traverses", tinkergraph);

        g.variables().set("creator", "marko");
        g.variables().set("lastModified", 2014);
        g.variables().set("comment", "this graph was created to provide examples and test coverage for tinkerpop3 api advances");
    }
}
