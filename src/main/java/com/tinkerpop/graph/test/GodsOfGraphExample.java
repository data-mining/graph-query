/**
 * 
 */
package com.tinkerpop.graph.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.thinkaurelius.titan.core.TitanElement;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.attribute.Geo;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.tinkerpop.graph.loader.GodsGraphFactory;

/**
 * @author kaniska
 * 
 *         Ref
 *         :http://s3.thinkaurelius.com/docs/titan/1.0.0/getting-started.html
 *         Ref
 *         :http://tinkerpop.incubator.apache.org/docs/3.0.1-incubating/#repeat-
 *         step Ref
 *         :http://www.datastax.com/dev/blog/the-benefits-of-the-gremlin-graph-
 *         traversal-machine Ref
 *         :https://academy.datastax.com/resources/getting-started-tinkerpop-and
 *         -gremlin Ref
 *         :http://www.slideshare.net/calebwjones/intro-to-graph-databases-using
 *         -tinkerpops-titandb-and-gremlin Ref
 *         :http://events.linuxfoundation.org/sites/events/files/slides/
 *         ApacheCon2015TinkerPop3.pdf
 * 
 */
public class GodsOfGraphExample {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		System.out.println("Hello Universe!");
		StandardTitanGraph graph = null;
		try {
			cleanup();
			graph = startup();
			GodsGraphFactory.load(graph);

			// -> Imp : Creates a collection of global and vertex-centric
			// indices on the graph.

			GraphTraversalSource g = graph.traversal();

			System.out.println(" Graph Traversal Example .. ");

			GraphTraversal<Vertex, Path> path = g.V().as("left").outE().as("rel").otherV().as("right").path();
			System.out.println("{ ");
			path.toList().stream().map(GodsOfGraphExample::getRelationTypes).distinct()
					// .filter(u -> !u.toString().contains("iPhone"))
					// .filter(u -> !u.toString().contains("galaxy"))
					.forEach(x -> System.out.println(x));
			System.out.println(" }");

			// The typical pattern for accessing data
			// Locate the entry point into the graph using a graph index
			// From the entry elements, a Gremlin path description describes how
			// to traverse to other elements in the graph via the explicit graph
			// structure

			/**
			 * the Saturn vertex has a name of "saturn, " an age of 10000, and a
			 * type of "titan. " The grandchild of Saturn can be retrieved with
			 * a traversal that expresses: "Who is Saturn’s grandchild?" (the
			 * inverse of "father" is "child"). The result is Hercules.
			 */

			// simple examples :
			// print the name 1. saturn

			Vertex saturn = g.V().has("name", "saturn").next();
			System.out.println(" 1. " + saturn.values().next());

			// saturn = g.V().has('name',
			// 'hercules').out('father').out('father').values('name');

			// prints all properties - {name=[saturn], age=[10000]}
			System.out.println("Properties of Saturn >>>>");
			Collection result1 = new ArrayList<>();
			g.V(saturn).valueMap().fill(result1);
			for (Object object : result1) {
				System.out.println(object);
			}

			System.out.println("Who is Saturn’s grandchild ?");
			Collection result2 = new ArrayList<>();
			g.V(saturn).in("father").in("father").values("name").fill(result2);
			for (Object object : result2) {
				System.out.println(object);
			}

			// System.out.println("Navigate the Ancestors - traverse similar
			// edges vertex by vertex");
			// g.V().has('name',
			// 'hercules').repeat(out('father')).emit().values('name')

			System.out.println("Find Personas aged > 5000");
			Collection result = new ArrayList<>();
			g.V().has("age", P.gte(5000)).values("name").fill(result);
			for (Object d : result) {
				System.out.println(d);
			}

			//
			System.out.println("Find Personas showing love for their places ");
			// Is it working ?
			result = new ArrayList<>();
			Collection titleList = new ArrayList<>();
			titleList.add("love");
			g.E().filter(new Predicate<Traverser<Edge>>() {
				@Override
				public boolean test(Traverser<Edge> t) {
					// TODO Auto-generated method stub
					org.apache.tinkerpop.gremlin.structure.Property p = (org.apache.tinkerpop.gremlin.structure.Property) t
							.get().property("reason");
					if (p.isPresent()) {
						return p.value().toString().contains("love");
					}
					return false;
				}
			}).values("name").fill(result);
			for (Object d : result) {
				System.out.println(d);
			}

			//
			System.out.println(" Which gods were involed in battle within 50 kilometers of Athens ? >>>>");
			Collection r3 = new ArrayList<>();
			g.E().has("place", Geo.geoWithin(Geoshape.circle(37.97, 23.72, 50))).as("source").inV().as("God2")
					.select("source").outV().as("God1").select("God1", "God2").by("name").fill(r3);

			for (Object object : r3) {
				System.out.println(object);
			}
			/**
			 * {God1=hercules, God2=nemean} {God1=hercules, God2=hydra}
			 */

			System.out.println(" Use a loop to express > Hercules is the vertex that is 2-steps");
			System.out.println(" away from Saturn along the in('father') path. >>>>>");
			Collection r4 = new ArrayList<>();
			TitanVertex hercules = (TitanVertex) g.V(saturn).repeat(__.in("father")).times(2).next();

			System.out.println(" Show that hercules is a demigod => son of a god and human >>>>");
			g.V(hercules).out("father", "mother").label().fill(r4);

			System.out.println(hercules.label() + " is son of " + r4);

			System.out.println(" Find all the other gods with whom Hercules battled > 10 time >>>>");
			Collection r5 = new ArrayList<>();
			// g.V(hercules).outE("battled").filter(t
			// ->((Integer)t.get().property("time").value()) >
			// 10).inV().values("name").fill(r5);

			g.V(hercules).outE("battled").has("time", P.gt(10)).inV().values("name").fill(r5);
			// Note use predicate P.gt(10)
			// hercules.query().edges().
			for (Object d : r5) {
				System.out.println(d);
			}

			System.out.println(" Who are pluto's cohabitants ?  >>>>>");
			Collection r6 = new ArrayList<>();
			Vertex pluto = g.V().has("name", "pluto").next();
			// look into outgoing edge 'lives' -> then find all the incoming
			// edges for the edge destination
			// exclude pluto
			g.V(pluto).as("V").out("lives").in("lives").where(P.neq("V")).values("name").fill(r6);
			for (Object d : r6) {
				System.out.println(d);
			}

			System.out.println(" Where do pluto's brothers live ? >>>>> ");
			// select(...).by("name") -> fetches only name properties of God and
			// Plce
			Collection r7 = new ArrayList<>();
			g.V(pluto).out("brother").as("God").out("lives").as("Place").select("God", "Place").by("name").fill(r7);
			for (Object d : r7) {
				System.out.println(d);
			}

			// More Complex Query
			System.out.println(" What do the brothers of Pluto love about their places ? >>>> ");
			// TBD -> not working !!!!
			Collection r8 = new ArrayList<>();

			// g.V(pluto).has("reason").filter(t ->
			// t.get().value("reason").toString().contains("love"))
			g.V(pluto).has("reason", P.within("loves")).as("source").values("reason").as("reason").select("source")
					.outV().values("name").as("god").select("source").inV().values("name").as("thing")
					.select("god", "reason", "thing").fill(r8);
			for (Object d : r8) {
				System.out.println(d);
			}

			/////
			Collection r1 = new ArrayList();
			g.V().out().out().out().simplePath().count().fill(r1);
			System.out.println("How many 3-step acyclic paths exist in the graph ? ");
			System.out.println(r1);

			/////
			Collection r2 = new ArrayList();
			g.V().out("battled").values("name").groupCount().fill(r2);
			System.out.println("How many types of entities battled ? ");
			System.out.println(r2);

			/////
			r4 = new ArrayList();
			// g.V().as("x").out("father").out("father").coalesce("grandfather",
			// "x").fill(r3);
			// System.out.println("Derive all implicit grandfather relationships
			// ? ");
			// System.out.println(r3);

			// g.V(hercules).as('h').out('battled').in('battled').where(neq('h')).values('name')
			// Top 10 ordered Traversals

			/**
			 * 
			 * h = g..V().has('name', 'hercules').next()
			 * g.V(h).local(outE('battled').order().by('time',
			 * decr).limit(10)).inV().values('name')
			 * g.V(h).local(outE('battled').has('rating',
			 * 5.0).order().by('time', decr).limit(10)).values('place')
			 * 
			 */

			/////
			// graph.indexQuery("vertexByText", "v.text:(farm uncle
			///// berry)").vertices()

			///// its not quite working ..... ...
			TitanVertex h = (TitanVertex) g.V().has("name", "hercules").next();
			h.query().direction(Direction.OUT).vertices()
					.forEach(new Consumer<com.thinkaurelius.titan.graphdb.vertices.CacheVertex>() {
						public void accept(com.thinkaurelius.titan.graphdb.vertices.CacheVertex t) {
							// System.out.println(t.it().values("name"));
						};
					});

			// System.out.println(h.query().direction(Direction.OUT).labels("battled").has("time",P.lt(5))
			// );

			/**
			 * Ref : https://groups.google.com/forum/#!topic/aureliusgraphs/
			 * 5xyz4Ur_lm4 
			 * 
			 * GremlinPipeline pipe = new
			 * GremlinPipeline(g).V("name",hercules.value("name"))
			 * .outE("battled") .filter(new PipeFunction<Edge, Boolean>() {
			 * public Boolean compute(final Edge e) { return !(new
			 * GremlinPipeline(e)
			 * .bothE("viewed").outV().has("sessionId","avinash")
			 * .outE("viewed").inV().has("productId").hasNext()); } }); for
			 * (Object p : pipe) { Vertex product = (Vertex) p;
			 * System.out.println(product.property("productId")); }
			 **/

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			graph.close();
			TitanCleanup.clear(graph);
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

	private static String getRelationTypes(Path path) {
		TitanElement leftElem = ((TitanElement) path.get("left"));
		String leftLabel = leftElem.label();
		String leftName = leftElem.value("name");

		TitanElement rightElem = ((TitanElement) path.get("right"));
		String rightLabel = rightElem.label();
		String rightName = rightElem.value("name");

		String relLabel = ((TitanElement) path.get("rel")).label();
		return "\"" + leftLabel + "[" + leftName + "]" + "\" -> \"" + rightLabel + "[" + rightName + "]" + "\" [label="
				+ relLabel + "];";
	}
}
