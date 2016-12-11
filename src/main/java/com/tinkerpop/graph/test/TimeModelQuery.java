package com.tinkerpop.graph.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;

import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.tinkerpop.graph.utils.DateUtil;

import org.apache.tinkerpop.gremlin.process.traversal.Order;

import ch.qos.logback.core.net.SyslogOutputStream;

public class TimeModelQuery {
	
	/**
	 * @param args
	 * http://www.tinkerpop.com/docs/3.0.0.M7/#range-step
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		System.out.println("Hello Universe!");
		StandardTitanGraph graph = null;
		try {
			//cleanup();
			//graph = startup();
			
			//LocalDateTime time = LocalDateTime.parse("01.01.2016  01:01:01", DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm:ss"));
			LocalDateTime time = LocalDateTime.parse("01.01.2016  01:01:01", DateTimeFormatter.ofPattern("MM.dd.yyyy  HH:mm:ss"));
			ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("PST");
			long epoch = time.atZone(zoneId).toEpochSecond();
			
		       LocalDateTime time2 = LocalDateTime.parse("06.01.2016  01:01:01", DateTimeFormatter.ofPattern("MM.dd.yyyy  HH:mm:ss"));
		       long epoch2 = time2.atZone(zoneId).toEpochSecond();
			
			// build the graph
			long t1 = LocalDate.ofYearDay(2016, Calendar.DAY_OF_MONTH-1).toEpochDay();
			System.out.println(" *********** "+t1);
			long t2 = LocalDate.ofYearDay(2016, Calendar.DAY_OF_MONTH-2).toEpochDay();
			System.out.println(" *********** "+t2);
			long t3 = LocalDate.ofYearDay(2016, Calendar.DAY_OF_MONTH-3).toEpochDay();
			System.out.println(" *********** "+t3);
			long t4 = LocalDate.ofYearDay(2016, Calendar.DAY_OF_MONTH-4).toEpochDay();
			System.out.println(" *********** "+t4);			
			
			TitanVertex u1 = graph.addVertex(T.label, "user", "name", "U1");
			TitanVertex d1 = graph.addVertex(T.label, "device", "name", "D1");
			u1.addEdge("connected", d1, "weight", Math.random(), "time", t1);
			
			TitanVertex u2 = graph.addVertex(T.label, "user", "name", "U2");
			TitanVertex d2 = graph.addVertex(T.label, "device", "name", "D2");
			u2.addEdge("connected", d2, "weight", Math.random(), "time", t1);

			TitanVertex u3 = graph.addVertex(T.label, "user", "name", "U3");
			u3.addEdge("connected", d1, "weight", Math.random(), "time", t2);

			TitanVertex u4 = graph.addVertex(T.label, "user", "name", "U4");
			u4.addEdge("connected", d1, "weight", Math.random(), "time", t3);

			TitanVertex u5 = graph.addVertex(T.label, "user", "name", "U5");
			u5.addEdge("connected", d1, "weight", Math.random(), "time", t4);
			u1.addEdge("connected", d2, "weight", Math.random(), "time", t2);
			
			//Search by Time
			GraphTraversalSource gst = graph.traversal();

			Collection result = new HashSet<String>();
			long twoDaysFromNow = LocalDate.ofYearDay(2016, Calendar.DAY_OF_MONTH-2).toEpochDay();
			System.out.println( " $$$$$ twoDaysFromNow >> "+twoDaysFromNow);
			
			gst.E().has("time",P.gte(twoDaysFromNow)).as("connection")
			.inV().as("device").select("connection").outV().as("user")
			.select("device","user").by("name").fill(result);

			System.out.println("*****************************************");
			
			System.out.println(" Which users connected to what devices 2 days ago  ... ");
			
			result.stream().forEach(x -> System.out.println(x));

			System.out.println("*****************************************");
			
			Collection result2 = new HashSet<String>();
			
			gst.E()
			.has("time", P.between(t4, t3)).as("connection")
			.inV().as("device").select("connection").outV().as("user")
			.select("device","user").by("name").fill(result2);
			
			result2.stream().forEach(x -> System.out.println(x));;
			
			System.out.println("*****************************************");

			
			Collection result3 = new HashSet<String>();
			
			gst.V()
			.has("name", "U1")
			.outE("connected")
			.order(Scope.local)
			.by("time", Order.decr)
			.inV()
			.values("name")
			.fill(result3);
			
			System.out.println("Show the devices which U1 was connected most-recently");
			
			System.out.println(result3);;
			
			System.out.println("*****************************************");

			graph.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
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


}
