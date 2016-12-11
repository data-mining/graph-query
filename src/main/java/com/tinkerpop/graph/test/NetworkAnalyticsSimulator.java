/**
 * 
 */
package com.tinkerpop.graph.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.tinkerpop.graph.loader.NetworkDummyDataLoader;
import com.tinkerpop.graph.utils.Timer;

/**
 * @author kaniska
 *
 */
public class NetworkAnalyticsSimulator {

	/**
	 * @param args
	 */
	
	private static Logger logger = LoggerFactory.getLogger(NetworkAnalyticsSimulator.class);
	
	public static void main(String[] args) {
		System.out.println("Hello Universe! ");
		StandardTitanGraph graph = null;
		try {
			cleanup();
			graph = startup();

			// Load the data

			loadData(graph);
			
			// Analyze the data
			
			analyzeData(graph);
			
			timestampQuery(graph);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			graph.close();
			TitanCleanup.clear(graph);
		}

	}
	
	private static void loadData(TitanGraph graph){
		int noOfUsers = 100;
		int noOfDevices = 100;

		List<Object> users = new ArrayList<Object>(noOfUsers);
		List<Object> devices = new ArrayList<Object>(noOfDevices);
		
		Timer overallTimer = new Timer();		
		NetworkDummyDataLoader.createUsers(graph, noOfUsers, users);		
		NetworkDummyDataLoader.createDevices(graph, noOfDevices, devices);
		
		int totalEdges = noOfDevices > noOfUsers ? noOfDevices : noOfUsers;		
		NetworkDummyDataLoader.createConnections(graph, totalEdges *4, users, devices);
		
		overallTimer.end();
		System.out.println("*****************************************");

		logger.info("Overall : " + overallTimer.getTimeTakenSeconds() + "secs to complete.");
		
		logger.info("Finished.");
		
		System.out.println("*****************************************");

	}
	
	/**
	 * every USER is x distance away from a CENTRAL CONTROLLER
	 * Get the DEVICES that a specific USER is CONNECTED ,
	 * Then find all USERS within 4 miles and find which DEVICES these users are connected with 
	 * @param graph 
	 * 
	 */
	
	private static void analyzeData(Graph graph){
		
		Timer queryTimer = new Timer();
		
		GraphTraversalSource gst = graph.traversal();
		
		Vertex userV = gst.V().has("name", "U2").next();

		int min = (Integer.parseInt(userV.property("distance").value().toString()) - 1);
		int max = (Integer.parseInt(userV.property("distance").value().toString()) + 1);
		
		Set<String> nearbyDevices = new HashSet<String>();
		
		/**
		gst.V(userV).out("connected").in().has("distance", 
				P.between(min,max)).out("connected")..toSet().iterator().forEachRemaining(v -> {
					nearbyDevices.add( ((TitanVertex)v).value("name") );
				});
				**/
		
		Set<Map<String, Object>> nearByUsersAndDevices = gst.V(userV).out("connected").in().has("distance", 
				P.between(min,max)).as("user").out("connected").as("device").select("user","device").by("name").toSet();
		
		nearByUsersAndDevices.stream().forEach(v -> {System.out.println(v);});
		
		queryTimer.end();
		System.out.println("*****************************************");
		//logger.info("Queries took " + queryTimer.getTimeTakenMillis() + "ms.");
		
		//logger.info(nearbyDevices.size() + " nearby devices for user " + userV.property("name") +  " - " + nearbyDevices.toString());

		System.out.println("*****************************************");
		
		System.out.println("");
		
	}
	
	public static void timestampQuery(Graph graph){
		
		Timer queryTimer = new Timer();
		
		GraphTraversalSource gst = graph.traversal();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);
		
		Collection result = new HashSet<String>();
		
		long twoDaysFromNow = LocalDate.ofYearDay(2016, Calendar.DAY_OF_MONTH-2).toEpochDay();
		
		System.out.println( " $$$$$ twoDaysFromNow >> "+twoDaysFromNow);
		
		gst.E().has("time",P.gt(twoDaysFromNow)).inV().values("name").fill(result);
	
		//timeBasedUserConnections.stream().forEach(v -> result.add(v.value("name")));
		
		queryTimer.end();

		System.out.println("*****************************************");
		logger.info("Queries took " + queryTimer.getTimeTakenMillis() + "ms.");
		
		logger.info(" Following users established connection 2 days back  ... " + result.toString());

		System.out.println("*****************************************");
		System.out.println("");
	
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
