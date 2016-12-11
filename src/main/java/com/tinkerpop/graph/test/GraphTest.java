
package com.tinkerpop.graph.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraphQuery;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;


public class GraphTest {

    public GraphTest() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws NoSuchElementException, JSONException {
        populateGraph();
        //queryData();
        //cleanup();
    }
    
    private static void queryData() throws NoSuchElementException, JSONException{
        System.out.println("Hello World!");
        System.out.println("************************************");
        
        StandardTitanGraph graph = startup();

        /////
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        
        GraphTraversalSource gst = graph.traversal();
        Collection result = new ArrayList<>();
        gst.V().has("userName", "utjaiswa").outE("AssociateWith").inV()
        .valueMap().fill(result);
        //result.stream().forEach(x -> System.out.println(x));
       System.out.println("*****************************************");
        for (Object object : result) {
            System.out.println(object);
        }
       
        System.out.println("*****************************************");
        
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        
        GraphTraversal userTraversal =  gst.V().hasLabel("User");
        //Groups Member of
        
        // Devices Associated
       userTraversal.outE("AssociatedWith");
        
        // Within Time Range
       userTraversal.has("lifeEnd",P.inside(300, 900));
        
       userTraversal.valueMap().fill(result);
        System.out.println("*****************************************");
        for (Object object : result) {
            System.out.println(object);
        }
        
        System.out.println("*****************************************");
        
        //
        long from = 146363800;
        long to = 446366800;
        
        TitanGraphQuery<?> queryPipeline = graph.query();
        //queryPipeline = queryPipeline.has("label", "USER_GROUP").has("userGroupName", userGroupName);
        queryPipeline = queryPipeline.has("label", "Bin Liang");
        //queryPipeline = queryPipeline.has("label", "AssociatedWith").interval("lifeEnd", from, to).has("label", "EndDevice");
        Iterable<TitanVertex> vertices = queryPipeline.vertices();
        JSONArray result2 = new JSONArray();
        for (Object item : vertices) {
            TitanVertex vertex = (TitanVertex)item;
            JSONObject field = new JSONObject();
            field.put("entity_id", vertex.value("endDevName").toString());
            result2.put(field);
        }
        System.out.println("************************************");
        System.out.println(" result " + result2.toString());
        
        System.out.println("*****************************************");
        
        graph.close();
    }

    private static void populateGraph() {
        
        //cleanup();
        
        StandardTitanGraph graph = startup();
        
        TitanVertex ug1 = graph.addVertex(T.label, "UserGroup", "uuid", "297ac8df-cb23-4fdb-89c2-c551f90fb9f0", "userGroupName", "Engineer");
        TitanVertex ug2 = graph.addVertex(T.label, "UserGroup", "userGroupName", "Sales", "uuid", "94257118-f632-44a3-aec8-e841f61bf784");
        TitanVertex ug3 = graph.addVertex(T.label, "UserGroup", "userGroupName", "Human Resource", "uuid", "d02ec68f-eab2-4e1d-a7f8-58539fbcae09");
        //
        
        TitanVertex u1 = graph.addVertex(T.label, "User", "userName", "dachulin", "uuid", "79352101-d49d-458f-8c18-d6af4863eb97");
        TitanVertex u2 = graph.addVertex(T.label, "User", "userName", "utjaiswa", "uuid", "b55f4d53-b9d6-4155-8602-ee1946d52385");
        TitanVertex u3 = graph.addVertex(T.label, "User", "userName", "rvilkhu", "uuid", "dcdbccd0-7da4-470b-b2a1-645a4e30fc23");
        TitanVertex u4 = graph.addVertex(T.label, "User", "userName", "manchakr", "uuid", "7597103d-9a3d-4b00-9576-67fca4c45683");
        TitanVertex u5 = graph.addVertex(T.label, "User", "userName", "vveguru", "uuid", "0a97e905-92bd-424d-83ef-e4b58d0ca087");
        TitanVertex u6 = graph.addVertex(T.label, "User", "userName", "shgattu", "uuid", "c63cc431-1a98-4f43-a9c7-30f4a1c3d555");
        TitanVertex u7 = graph.addVertex(T.label, "User", "userName", "liangbin", "uuid", "fbe113ce-97c9-4a71-8445-d056a7ebbadd");
        TitanVertex u8 = graph.addVertex(T.label, "User", "userName", "kmandal", "uuid", "fe4f9ce7-3860-4e26-b658-3e9503008fe5");
        TitanVertex u9 = graph.addVertex(T.label, "User", "userName", "rnethi", "uuid", "3016298b-fbc5-404c-acf7-a0f933fa7c02");
        TitanVertex u10 = graph.addVertex(T.label, "User", "userName", "apbanerj", "uuid", "17d2b9c7-67ed-4bd8-951a-046913424501");
        //
        
        u1.addEdge("MemberOfUG", ug1);
        u2.addEdge("MemberOfUG", ug1);
        u3.addEdge("MemberOfUG", ug2);
        u4.addEdge("MemberOfUG", ug3);
        u5.addEdge("MemberOfUG", ug2);
        u6.addEdge("MemberOfUG", ug2);
        u7.addEdge("MemberOfUG", ug3);
        
        //
        TitanVertex ed1 = graph.addVertex(T.label, "EndDevice", "endDevName", "rnethi-M-88XY", "uuid", "f17807f8-c36d-4f04-bb00-9053f014d3ac");
        TitanVertex ed2 = graph.addVertex(T.label, "EndDevice", "endDevName", "LIANGBIN-M-88XY", "uuid", "87bb8a74-baf3-46e7-8722-1bc0a5ff93c3");
        TitanVertex ed3 = graph.addVertex(T.label, "EndDevice", "endDevName", "SRV_62.7.4.6", "uuid", "c75d9d43-4860-42d2-b20a-e60c42d75963");
        TitanVertex ed4 = graph.addVertex(T.label, "EndDevice", "endDevName", "SRV_62.7.4.3", "uuid", "12fac2a5-13ae-4923-bb4d-ef27f3b84ebe");
        TitanVertex ed5 = graph.addVertex(T.label, "EndDevice", "endDevName", "dachulin-M-88XY", "uuid", "17ae4373-b9a1-4bac-92f8-8f4039545e22");
        TitanVertex ed6 = graph.addVertex(T.label, "EndDevice", "endDevName", "rvilkhu-M-88XY", "uuid", "b0f9790a-ff63-4c1b-8eb9-be0817daed3d");
        TitanVertex ed7 = graph.addVertex(T.label, "EndDevice", "endDevName", "apbanerj-M-88XY", "uuid", "6f0cf412-199b-493e-8bc2-a12ed7cf2e53");
        TitanVertex ed8 = graph.addVertex(T.label, "EndDevice", "endDevName", "kmandal-M-88XY", "uuid", "aadd7f65-1177-40f0-abd3-93bccf47011b");
        TitanVertex ed9 = graph.addVertex(T.label, "EndDevice", "endDevName", "SRV_rocket", "uuid", "97242358-cf16-4a06-afde-a8dd5e0f813e");
        TitanVertex ed10 = graph.addVertex(T.label, "EndDevice", "endDevName", "SRV_62.7.4.4", "uuid", "1d3012d8-cb4b-41fc-b5b5-5be781d82dc5");
        //
        
        
        
        u1.addEdge("AssociateWith", ed1, "lifeStart", 1451635200, "lifeEnd", 1453635200);
        u1.addEdge("AssociateWith", ed2, "lifeStart", 1452635200, "lifeEnd", 1455635200);
        u1.addEdge("AssociateWith", ed3, "lifeStart", 1452635200, "lifeEnd", 1455635200);
        u2.addEdge("AssociateWith", ed4, "lifeStart", 1464564400, "lifeEnd", 1464764400);
        u2.addEdge("AssociateWith", ed1, "lifeStart", 1464664400, "lifeEnd", 1464768061);
        u2.addEdge("AssociateWith", ed2, "lifeStart", 1452635200, "lifeEnd", 1455635200);
        u3.addEdge("AssociateWith", ed3, "lifeStart", 1452635200, "lifeEnd", 1464768061);
        
        graph.close();
    }
    
    private static StandardTitanGraph startup() {
        BaseConfiguration conf = new BaseConfiguration();
        conf.setProperty("storage.backend", "cassandra");
        conf.setProperty("storage.hostname", "127.0.0.1");
        //conf.setProperty("storage.port","2181");
        conf.setProperty("index.search.backend", "elasticsearch");
        conf.setProperty("index.search.hostname", "127.0.0.1");

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
