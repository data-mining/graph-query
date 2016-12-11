package com.tinkerpop.graph.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.TitanVertexProperty;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;

/**
 * 
 * @author kaniska
 *
 * Ref : http://s3.thinkaurelius.com/docs/titan/1.0.0/schema.html#property-cardinality

The default cardinality setting is SINGLE. Note, that property keys used on edges and properties have cardinality SINGLE. Attaching multiple values for a single key on an edge or property is not supported.

mgmt = graph.openManagement()
birthDate = mgmt.makePropertyKey('birthDate').dataType(Long.class).cardinality(Cardinality.SINGLE).make()
name = mgmt.makePropertyKey('name').dataType(String.class).cardinality(Cardinality.SET).make()
sensorReading = mgmt.makePropertyKey('sensorReading').dataType(Double.class).cardinality(Cardinality.LIST).make()
mgmt.commit()
 
 *
 */

public class PropertyHistoryQuery {

	public static void main(String[] args) {
		System.out.println("Hello Universe!");
		StandardTitanGraph graph = null;
		try {
			cleanup();
			graph = startup();
			//SampleGraphFactory.load(graph);
			TitanManagement mgmt = graph.openManagement();
			mgmt.makePropertyKey("name").dataType(String.class).cardinality(Cardinality.LIST).make();
			mgmt.makePropertyKey("updatedBy").dataType(String.class).cardinality(Cardinality.LIST).make();
			mgmt.makePropertyKey("dateTime").dataType(Date.class).cardinality(Cardinality.LIST).make();
			mgmt.commit();
			TitanVertex v = graph.addVertex();
			TitanVertexProperty p1 = v.property("name", "iPhone5");
			p1.property("updatedBy", "sam");
			p1.property("dateTime", new Date());
			p1.property("updatedBy", "hari");
			p1.property("dateTime", new Date());
			
			
			TitanVertexProperty p2 = v.property("name", "galaxy");
			p2.property("updatedBy", "ram");
			p2.property("dateTime", new Date());
			p2.property("updatedBy", "marko");
			p2.property("dateTime", new Date());
			
			TitanVertexProperty p3 = v.property("name", "blackberry");
			p3.property("updatedBy", "tom");
			p3.property("dateTime", new Date());
			p3.property("updatedBy", "arka");
			p3.property("dateTime", new Date());
			
			graph.tx().commit();
			
			GraphTraversalSource g = graph.traversal();
			
			System.out.println("Lets have some fun with ... Proeprty Traversal ");
			Collection r1 = new ArrayList<>();
			GraphTraversal gt = g.V().properties().valueMap();
			while(gt.hasNext()) {
				HashMap v1 = (HashMap) gt.next();
				System.out.println(v1);
			}
			
			System.out.println("Probably a better traversal ... ");
			// {dateTime=Wed Apr 06 12:21:41 PDT 2016, updatedBy=hari}
			// {dateTime=Wed Apr 06 12:21:41 PDT 2016, updatedBy=marko}
			/// hmmm looks like properties getting overwritten ..
			
			/////		
			
			g.V().properties("name").forEachRemaining(parent -> {
				System.out.println( " Parent Property " + parent.value());
				
				((TitanVertexProperty)parent).properties().forEachRemaining(child -> {
					System.out.println( " 	Child Property " + child);
				});
			});
			
			System.out.println("**************************");
			
			/**
			 Parent Property iPhone5
			 	Child Property p[updatedBy->hari]
			 	Child Property p[dateTime->Wed Apr 06 12:55:08 ]
			 Parent Property galaxy
			 	Child Property p[updatedBy->marko]
			 	Child Property p[dateTime->Wed Apr 06 12:55:08 ]
			**************************

			 */
			
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			graph.close();
			TitanCleanup.clear(graph);
		}

	}
	
	// sample code for Traverser 
	// https://github.com/ergh99/connections-backend/blob/47b5cf93f999770cefd6a7a31c2d7123ead3e91b/user-data/src/main/java/com/ergh99/web/connections/users/service/impl/UserDataRepositoryImpl.java
	
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
