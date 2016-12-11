package com.tinkerpop.graph.test;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.decoration.SubgraphStrategy;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.tinkerpop.graph.loader.GodsGraphFactory;

public class GraphTraversalStrategy {

	public static void main(String[] args) {

		System.out.println("Hello Universe!");
		StandardTitanGraph graph = null;
		try {
			cleanup();
			graph = startup();
			GodsGraphFactory.load(graph);
			GraphTraversalSource g = graph.traversal();

			//
			final Traversal<org.apache.tinkerpop.gremlin.structure.Vertex, ?> vertexCriterion = __.has("name", P.within("josh", "lop", "ripple"));

			// final SubgraphStrategy strategy = SubgraphStrategy.build().vertexCriterion(vertexCriterion).create();
			//final GraphTraversalSource sg = graph.strategy(strategy);

			//

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			graph.close();
		}

		/**
		 * TinkerGraph graph = TinkerFactory.createModern();
		 * 
		 * GraphStep strategy = SubgraphStrategy.build().vertexCriterion(vertex
		 * -> true) .edgeCriterion(edge -> (int) edge.id() >= 8 && (int)
		 * edge.id() <= 10).create(); StrategyGraph sg =
		 * graph.strategy(strategy);
		 * 
		 * // all vertices are here System.out.println("" +
		 * sg.V().count().next() + " of " + sg.V().count().next() + " vertices"
		 * );
		 * 
		 * // only the given edges are included System.out.println("" +
		 * sg.E().count().next() + " of " + sg.E().count().next() + " edges");
		 **/

		/**
		 * GraphTraversal<Vertex, Vertex> traversal =
		 * graph.traversal().V().has("subjectId", "test").
		 * outE("isAssigned").has("receivedStatus", P.within(receivedStatuses)).
		 * order().by("assignedOn", Order.decr).range(0,
		 * 15).as("e_asg").inV().as("v_svc").
		 * choose(__.values("status").is("New"), __.values("status"),
		 * __.values("previousStatus"));
		 * 
		 */
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
		
	}
}
