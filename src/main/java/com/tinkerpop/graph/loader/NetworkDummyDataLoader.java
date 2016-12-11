package com.tinkerpop.graph.loader;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.graph.utils.DateUtil;

public class NetworkDummyDataLoader {

	private static Logger logger = LoggerFactory.getLogger(NetworkDummyDataLoader.class);
	
	public static void main(String[] args) {
		
	}
	
	
	public static void createConnections(TitanGraph graph, int totalEdges, List<Object> users, List<Object> devices) {
		logger.info("Adding " + totalEdges + " edges");
		
		TitanManagement managementSystem = graph.openManagement();
		managementSystem.makeEdgeLabel("connected");
		managementSystem.commit();
		
		TitanTransaction tx = graph.newTransaction();		
		
		for (int i = 0; i < totalEdges; i++) {			
			
			Vertex userV = graph.vertices(users.get(new Double(Math.random() * users.size()).intValue())).next();
			Vertex deviceV = graph.vertices(devices.get(new Double(Math.random() * devices.size()).intValue())).next();
			
			// add index on the date field - prefer long over date type to take advantage of limit(), interval, etc
			TitanEdge e = (TitanEdge) userV.addEdge("connected", deviceV, "weight", Math.random(), "time", DateUtil.getRandomDate());
			
			logger.info(userV.property("name").value().toString() + 
					" connected to " + deviceV.property("name").value().toString() 
						+ " @ " + e.property("time").value().toString() );
		}
		
		tx.commit();
	}

	public static void createDevices(TitanGraph graph, int deviceNum, List<Object> devices) {
		
		TitanManagement managementSystem = graph.openManagement();
		logger.info("Adding " + deviceNum + " Devices");
		for (int i = 0; i < deviceNum; i++) {
			
			TitanVertex v = graph.addVertex();

			String product= "D" + i;

			v.property("name", product);
			v.property("duration", (Math.random() * 99) + 1);			
			
			devices.add(v.id());				
		}
		managementSystem.commit();
	}

	public static void createUsers(TitanGraph graph , int noOfUsers, List<Object> users) {
		TitanManagement managementSystem = graph.openManagement();
		
		logger.info("Adding " + noOfUsers + " Users");
		for (int i = 0; i < noOfUsers; i++) {
			
			TitanVertex v = graph.addVertex();

			String user = "U" + i;

			v.property("name", user);
			v.property("userid", i);		
			v.property("distance", new Double(Math.random() * 60).intValue() + 10);
			
			graph.tx().commit();
			
			users.add(v.id());				
		}
		managementSystem.commit();
	}
	
}
