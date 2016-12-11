package com.tinkerpop.graph.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONReader;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoReader;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.thinkaurelius.titan.graphdb.vertices.CacheVertex;

public class NorthWindGraphExample {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		System.out.println("Hello Universe!");
		StandardTitanGraph graph = null;
		try {
			//cleanup();
			graph = startup();
			/**
			 * Note : if following steps are performed , then northwind.json will be created
			 * # start the Gremlin shell
				> wget -q http://sql2gremlin.com/assets/northwind.groovy -O /tmp/northwind.groovy
				> ${LATEST_DIRECTORY}/bin/gremlin.sh /tmp/northwind.groovy
				> In your Gremlin shell create the Northwind graph, a graph traversal source and you’re ready to go:
					gremlin> graph = NorthwindFactory.createGraph()
			 */
			
			// load the graph if not loaded already
			if(!graph.containsEdgeLabel("livesInRegion")){ // just a simple hack to check if a property is present or not.
				
				InputStream inStream = NorthWindGraphExample.class.getResourceAsStream("/northwind.json");
				GraphSONReader.build().create().readGraph(inStream, graph);
			}
			
			GraphTraversalSource gst = graph.traversal();
			
			//System.out.println("**************************");
			//gst.V().hasLabel("category").valueMap().forEachRemaining(System.out::println);
			
			System.out.println("**************************");
			System.out.println("Find order with size > 0");
			gst.V().has("product", "unitsOnOrder", P.neq(0)).
            valueMap("name", "unitsOnOrder").forEachRemaining(System.out::println);
			
			System.out.println("**************************");
			System.out.println("Find orders with price between 5 - 10");
			gst.V().has("product", "unitPrice", P.between(5f, 10f)).
            valueMap("name", "unitPrice").forEachRemaining(System.out::println);
			
			System.out.println("**************************");
			
			System.out.println("Order by price lower to higher");
			
			gst.V().hasLabel("product").order(Scope.local).by("unitPrice", Order.decr).limit(5).
            valueMap("name", "unitPrice").forEachRemaining(System.out::println);
			
			System.out.println("**************************");
			
			System.out.println("Paged result set , query the next 5 products");
			
			gst.V().hasLabel("product").order(Scope.local).by("unitPrice", Order.decr).range(5, 10).
            valueMap("name", "unitPrice").forEachRemaining(System.out::println);
			
			System.out.println("**************************");
			
			System.out.println("Group product by total price");
			
			//gst.V().hasLabel("product").groupCount().by("unitPrice").
            //order(Scope.local).by("unitPrice", Order.decr).forEachRemaining(System.out::println);
			
			System.out.println("**************************");
			
			System.out.println("Join - Beverages as Categories");
			
			gst.V().has("name","Beverages").in("inCategory").values("name").forEachRemaining(System.out::println);;
			
			System.out.println("**************************");
			
			System.out.println("Left Join - count the number of orders for each customer.");
			
			gst.V().hasLabel("customer").match(
			           __.as("c").values("customerId").as("customerId"),
			           __.as("c").out("ordered").count().as("orders")
					).select("customerId", "orders").forEachRemaining(System.out::println);
			
			System.out.println("*********************");
			
			System.out.println("Recommendations ... ");
			
			/**
			Recommendation ::
			This sample shows how to recommend 5 products for a specific customer. 
			The products are chosen as follows:
			determine what the customer has already ordered
			determine who else ordered the same products
			determine what others also ordered
			determine products which were not already ordered by the initial customer, but ordered by the others
			rank products by occurence in other orders 
			 
			 */
			Collection collection = new ArrayList();
			gst.V().has("customerId", "ALFKI").as("customer1").
			// all products orders by a customer
            out("ordered").out("contains").out("is").aggregate("productsByCustomer1"). 
           // for each product .. find all other customers
            in("is").in("contains").in("ordered").where(P.neq("customer1")).
           // for each of other customers find products not ordered by intial customer 
            out("ordered").out("contains").out("is").where(P.without("productsByCustomer1")).
            //aggregate("recommendedProducts").
            values("name").
            order(Scope.local).
           // rank products by occurence in other orders
            //select("otherCustomers", "recommendedProducts").by("name").
            groupCount().
            //order().by(Order.decr).
            //forEachRemaining(System.out::println);
            //mapKeys().limit(5).
            //values("name").
            fill(collection);
			
			System.out.println("*********************");
			
			collection.stream().forEach(x -> System.out.println(x));
			
			//gremlin> g.V().hasLabel("person").as("p").out("created").as("s").in("created").where(neq("p")).select("p","s").by("name").groupCount()
			//==>[[p:peter, s:lop]:2, [p:josh, s:lop]:2, [p:marko, s:lop]:2]

			
			System.out.println("*********************");
			
			// some  groovy dsl - Flow Ranking - https://github.com/tinkerpop/gremlin/wiki/Flow-Rank-Pattern 
			
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
