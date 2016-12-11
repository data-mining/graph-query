/**
 * 
 */
package com.tinkerpop.graph.test;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.T;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.tinkerpop.graph.loader.GodsGraphFactory;

/**
 * @author kaniska
 * Ref : https://www.youtube.com/watch?v=ALhjzlNuZdA
 * 
 */
public class AggregationQuery {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello Universe!");
		StandardTitanGraph graph = null;
		try {
			cleanup();
			//  Now create a new Graph
			graph = startup();
			GraphTraversalSource g = graph.traversal();
			
			
			TitanVertex u1 = graph.addVertex(T.label, "user", "name", "U1");
			TitanVertex d1 = graph.addVertex(T.label, "device", "name", "D1");
			u1.addEdge("connected", d1, "weight", Math.random());
			
			TitanVertex u2 = graph.addVertex(T.label, "user", "name", "U2");
			TitanVertex d2 = graph.addVertex(T.label, "device", "name", "D2");
			u2.addEdge("connected", d2, "weight", Math.random());

			TitanVertex u3 = graph.addVertex(T.label, "user", "name", "U3");
			u3.addEdge("connected", d1, "weight", Math.random());

			TitanVertex u4 = graph.addVertex(T.label, "user", "name", "U4");
			u4.addEdge("connected", d1, "weight", Math.random());

			TitanVertex u5 = graph.addVertex(T.label, "user", "name", "U5");
			u5.addEdge("connected", d2, "weight", Math.random());
			
			System.out.println("**************************");
			System.out.println("Group Users by Device");
			//g.E().outV().has(T.label, "device").group().by("name").forEachRemaining(System.out::println);
	        //g.V().as("user").outE().as("edge").otherV().as("device").select("user", "device").by(__.valueMap(true)).forEachRemaining(System.out::println);

			g.V().has(T.label, "device").forEachRemaining(device -> {
				System.out.println();
				System.out.print(device.property("name").value()+" -> ");
					device.vertices(Direction.IN, "connected")
						.forEachRemaining(user -> System.out.print(user.property("name").value() + " "));	
				System.out.println();
			});
			
			System.out.println("**************************");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			graph.close();
		}	

	}
	
	private static StandardTitanGraph startup() {
		BaseConfiguration conf = new BaseConfiguration();
		conf.setProperty("storage.backend", "cassandra");
		conf.setProperty("storage.hostname", "localhost");

		StandardTitanGraph graph = (StandardTitanGraph) TitanFactory.open(conf);

		return graph;
	}

	private static void cleanup() {
		BaseConfiguration conf = new BaseConfiguration();
		conf.setProperty("storage.backend", "cassandra");
		conf.setProperty("storage.hostname", "localhost");

		StandardTitanGraph graph = (StandardTitanGraph) TitanFactory.open(conf);
		graph.close();
		TitanCleanup.clear(graph);
	}

}
