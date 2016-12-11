package com.tinkerpop.graph.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;

/**
 * Ref : https://gist.github.com/freemo/cb39a4887650064d2cb7
 * http://tinkerpop.incubator.apache.org/docs/3.0.0.M9-incubating/
 * http://s3.thinkaurelius.com/docs/titan/1.0.0/getting-started.html#
 * _loading_the_graph_of_the_gods_into_titan
 *
 */
public class SimpleTitanQuery1 {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		TitanGraph graph = null;
		GraphTraversalSource g = null;
		try {

			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.backend", "cassandra");
			conf.setProperty("storage.hostname", "localhost");
			graph = TitanFactory.open(conf);

			// GraphOfTheGodsFactory.load(graph);

			// CLEANUP
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

			// Traverse the Graph
			g = graph.traversal();

			TitanVertex marko = graph.addVertex(T.label, "user", "name", "marko", "age", 29);
			marko.properties().next().property("date", 2014);
			// marko.properties().next().element().property("creator","stephen");

			TitanVertex lop = graph.addVertex(T.label, "effect", "name", "lop", "lang", "java");
			TitanVertex josh = graph.addVertex(T.label, "user", "name", "josh", "age", 32);
			TitanVertex ripple = graph.addVertex(T.label, "effect", "name", "ripple", "lang", "java");
			TitanVertex peter = graph.addVertex(T.label, "user", "name", "peter", "age", 35);

			marko.addEdge("knows", josh, "weight", 1.0f, "date", 2012);
			marko.addEdge("created", lop, "weight", 0.4f, "date", 2014);
			josh.addEdge("created", ripple, "weight", 1.0f, "date", 2012);
			josh.addEdge("created", lop, "weight", 0.4f, "date", 2012);
			peter.addEdge("created", lop, "weight", 0.2f, "date", 2014);
			marko.addEdge("knows", peter, "weight", 0.2f, "date", 2013);

			/////////
			Vertex v_marko = g.V().has("name", "marko").next();
			System.out.println(v_marko.values().next());
			// Vertex v = g.addV("name","josh","name","josh robins").next();
			// g.V(v).properties("name").hasValue("marko").property("acl","public");

			// drop property with public acl
			// g.V(v).properties('name').has('acl','public').drop();

			// g.V(v).properties(); //
			// g.V(v).properties().properties(); // acl meta property
			// g.V(v_marko).properties().property("date",2014); // add meta
			// properties
			// g.V(v_marko).properties().property("creator","stephen");

			/////

			Collection result1 = new ArrayList<>();
			// g.V(v_marko).properties().properties().values().fill(result1);
			g.V().outE("knows", "created").has("weight", P.between(0.5, 1.5)).has("date", 2012).inV().values("name")
					.fill(result1);

			System.out.println("************************************");
			System.out.println(" Titan : Result 1 ");
			for (Object object : result1) {
				System.out.println(object);
			}
			System.out.println("************************************");

			// Traversal Execution
			// e.g. vertex 1 (Source) => out("knows") (Step-1) ... flatMap() =>
			// values("name") (Step-2) ... map() => raversal.next() (Result)

			List result2 = new ArrayList();
			g.V().has("name", "marko").out("knows").values("name").fill(result2);

			System.out.println("************************************");
			System.out.println(" Titan : Result 2 ");
			for (Object object : result2) {
				System.out.println(object);
			}
			System.out.println("************************************");

			// The objects propagating through the traversal are wrapped in a
			// Traverser<T> i.e the Vertex is wrapped up inside Traverser
			// .......
			// A traverser maintains all the metadata about the traversal —
			// e.g., how many times the traverser has gone through a loop, the
			// path history of the traverser, the current object being
			// traversed, etc.
			// So Never do the following -
			// dummyTraversal(g);

			System.out.println("************************************");
			
			g.V(v_marko).out("knows").map(t -> t.get().value("name") + " is a friend of marko.").forEachRemaining(System.out::println);;

			System.out.println("************************************");

			/**
			 ** NOTE ** Gremlin Java Pipeline is not uptodate (last updated in
			 * 2014) and not working with latest titan-code so always use the
			 * above Trversal mechanism .... GremlinPipeline<?, ?> pipeline =
			 * new GremlinPipeline(g).V();
			 * 
			 * GremlinPipeline<?, Row> list =
			 * pipeline.as("user").out("knows").as("user").select(Arrays.asList(
			 * "user"), new PipeFunction<Vertex, String>() { public String
			 * compute(Vertex vertex) { return (String)
			 * vertex.property("name").value(); } }); System.out.println(list);
			 **/

			/**
			 * 
			 * //https://github.com/tinkerpop/gremlin/wiki/Using-Gremlin-through
			 * -Java
			 * 
			 * GremlinPipeline pipe = new GremlinPipeline();
			 * pipe.start(g.getVertex(1)).out("knows").property("name");
			 * 
			 * System.out.println(pipe.next());
			 * 
			 * GremlinPipeline<?, ?> pipeline = new GremlinPipeline(g).V();
			 * 
			 * GremlinPipeline<?, Row> list =
			 * pipeline.as("user").out("knows").as("user").select(Arrays.asList(
			 * "user"), new PipeFunction<Vertex, String>() { public String
			 * compute(Vertex vertex) { return (String)
			 * vertex.getProperty("name"); } }); System.out.println(list);
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			graph.close();
		}
	}

	private static void dummyTraversal(TitanGraph g) {
		Iterator<Vertex> vertices;
		////
		vertices = g.vertices();
		while (vertices.hasNext()) {
			Vertex v = vertices.next();
			VertexProperty prop = v.property("name");

			if (prop != null && prop.isPresent()) {
				System.out.println(v.property("name").value());
			}
			////
		}
		////
	}
}
