package com.tinkerpop.graph.test;

import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.example.GraphOfTheGodsFactory;
import com.tinkerpop.blueprints.Graph;

/**
 * Ref : https://gist.github.com/freemo/cb39a4887650064d2cb7
 *
 */
public class TitanQuery {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		TitanGraph graph = null;
		GraphTraversalSource g = null;
		try {

			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.backend", "cassandra");
			conf.setProperty("storage.hostname", "localhost");
			graph = TitanFactory.open(conf);
			
			//GraphOfTheGodsFactory.load(graph);
			
			g = graph.traversal();
			
			
			
			// CLEANUP
			Iterator<Vertex> vertices = g..vertices();
			while (vertices.hasNext()) {
				Vertex v = vertices.next();
				v.remove();
			}
			Iterator<Edge> edges = g.edges();
			while (edges.hasNext()) {
				Edge e = edges.next();
				e.remove();
			}
			//

			TitanVertex marko = g.addVertex(T.label, "user", "name", "marko", "age", 29);
			TitanVertex lop = g.addVertex(T.label, "effect", "name", "lop", "lang", "java");
			TitanVertex josh = g.addVertex(T.label, "user", "name", "josh", "age", 32);
			TitanVertex ripple = g.addVertex(T.label, "effect", "name", "ripple", "lang", "java");
			TitanVertex peter = g.addVertex(T.label, "user", "name", "peter", "age", 35);

			marko.addEdge("knows", josh, "weight", 1.0f);
			marko.addEdge("created", lop, "weight", 0.4f);
			josh.addEdge("created", ripple, "weight", 1.0f);
			josh.addEdge("created", lop, "weight", 0.4f);
			peter.addEdge("created", lop, "weight", 0.2f);
			
			

			//dummyTraversal(g);
			
		   //System.out.println(GraphTest.showData(g));

			
			/**
			GremlinPipeline<?, ?> pipeline = new GremlinPipeline(g).V();
			
			GremlinPipeline<?, Row> list = pipeline.as("user").out("knows").as("user").select(Arrays.asList("user"), 
					new PipeFunction<Vertex, String>() {
						public String compute(Vertex vertex) {
								return (String) vertex.property("name").value();
						}
					});
			System.out.println(list);
			**/
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
