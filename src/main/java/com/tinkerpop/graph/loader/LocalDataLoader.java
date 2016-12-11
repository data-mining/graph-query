
package com.tinkerpop.graph.loader;


import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;

public class LocalDataLoader {

    public LocalDataLoader() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        StandardTitanGraph graph = null;
        try {
            cleanup();
            graph = startup();

            // Create Schema
            TitanTransaction tx = graph.newTransaction();

            System.out.println("*** Creating Sample Vertex and Edges ***");

            Vertex ug1 = tx.addVertex(T.label, "UserGroup", "userGroupName", "Technical", "uuid",
                    "297ac8df-cb23-4fdb-89c2-c551f90fb9f0");

            Vertex ug2 = tx.addVertex(T.label, "UserGroup", "userGroupName", "Management", "uuid",
                    "94257118-f632-44a3-aec8-e841f61bf784");

            Vertex ug3 = tx.addVertex(T.label, "UserGroup", "userGroupName", "Human Resource", "uuid",
                    "d02ec68f-eab2-4e1d-a7f8-58539fbcae09");

            /////////

            Vertex u1 = tx.addVertex(T.label, "User", "userName", "dachulin", "uuid",
                    "79352101-d49d-458f-8c18-d6af4863eb97");

            Vertex u2 = tx.addVertex(T.label, "User", "userName", "utjaiswa", "uuid",
                    "b55f4d53-b9d6-4155-8602-ee1946d52385");

            Vertex u3 = tx.addVertex(T.label, "User", "userName", "rvilkhu", "uuid",
                    "dcdbccd0-7da4-470b-b2a1-645a4e30fc23");

            Vertex u4 = tx.addVertex(T.label, "User", "userName", "manchakr", "uuid",
                    "7597103d-9a3d-4b00-9576-67fca4c45683");

            Vertex u5 = tx.addVertex(T.label, "User", "userName", "vveguru", "uuid",
                    "0a97e905-92bd-424d-83ef-e4b58d0ca087");

            Vertex u6 = tx.addVertex(T.label, "User", "userName", "shgattu", "uuid",
                    "c63cc431-1a98-4f43-a9c7-30f4a1c3d555");

            Vertex u7 = tx.addVertex(T.label, "User", "userName", "liangbin", "uuid",
                    "fbe113ce-97c9-4a71-8445-d056a7ebbadd");

            Vertex u8 = tx.addVertex(T.label, "User", "userName", "kmandal", "uuid",
                    "fe4f9ce7-3860-4e26-b658-3e9503008fe5");

            Vertex u9 = tx.addVertex(T.label, "User", "userName", "rnethi", "uuid",
                    "3016298b-fbc5-404c-acf7-a0f933fa7c02");

            Vertex u10 = tx.addVertex(T.label, "User", "userName", "apbanerj", "uuid",
                    "17d2b9c7-67ed-4bd8-951a-046913424501");

            
            u1.addEdge("MemberOfUG", ug1);
            u2.addEdge("MemberOfUG", ug1);
            u3.addEdge("MemberOfUG", ug2);
            u4.addEdge("MemberOfUG", ug3);
            u5.addEdge("MemberOfUG", ug2);
            u6.addEdge("MemberOfUG", ug2);
            u7.addEdge("MemberOfUG", ug3);

            //

            Vertex ed1 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "rnethi-M-88XY", "uuid",
                    "f17807f8-c36d-4f04-bb00-9053f014d3ac");

            Vertex ed2 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "LIANGBIN-M-88XY", "uuid",
                    "87bb8a74-baf3-46e7-8722-1bc0a5ff93c3");

            Vertex ed3 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "SRV_62.7.4.6", "uuid",
                    "c75d9d43-4860-42d2-b20a-e60c42d75963");

            Vertex ed4 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "SRV_62.7.4.3", "uuid",
                    "12fac2a5-13ae-4923-bb4d-ef27f3b84ebe");

            Vertex ed5 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "dachulin-M-88XY", "uuid",
                    "17ae4373-b9a1-4bac-92f8-8f4039545e22");

            Vertex ed6 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "rvilkhu-M-88XY", "uuid",
                    "b0f9790a-ff63-4c1b-8eb9-be0817daed3d");

            Vertex ed7 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "apbanerj-M-88XY", "uuid",
                    "6f0cf412-199b-493e-8bc2-a12ed7cf2e53");

            Vertex ed8 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "kmandal-M-88XY", "uuid",
                    "aadd7f65-1177-40f0-abd3-93bccf47011b");

            Vertex ed9 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "SRV_rocket", "uuid",
                    "97242358-cf16-4a06-afde-a8dd5e0f813e");

            Vertex ed10 = tx.addVertex(T.label, "EndDevice", "endDeviceName", "SRV_62.7.4.4", "uuid",
                    "1d3012d8-cb4b-41fc-b5b5-5be781d82dc5");

            //
            // 01.01.2016 01:01:01 => 1451638861
            // 06.01.2016 01:01:01 => 1464768061

            u1.addEdge("AssociateWith", ed1, "lifeStart", 1451635200, "lifeEnd", 1464768061);
            u1.addEdge("AssociateWith", ed2, "lifeStart", 1452635200, "lifeEnd", 1464768061);
            u1.addEdge("AssociateWith", ed3, "lifeStart", 1452635200, "lifeEnd", 1464768061);
            u2.addEdge("AssociateWith", ed4, "lifeStart", 1464564400, "lifeEnd", 1464764400);
            u2.addEdge("AssociateWith", ed1, "lifeStart", 1464664400, "lifeEnd", 1464764400);
            u2.addEdge("AssociateWith", ed2, "lifeStart", 1452635200, "lifeEnd", 1455635200);
            u3.addEdge("AssociateWith", ed3, "lifeStart", 1452635200, "lifeEnd", 1464764400);

            ///// /////////////////////////// //////////////////

            Vertex ep1 = tx.addVertex(T.label, "EndPoint", "ipAddr", "10.10.1.13", "uuid",
                    "a300604d-5d82-4d32-911b-5436dc9d204d");

            Vertex ep2 = tx.addVertex(T.label, "EndPoint", "ipAddr", "10.10.1.15", "uuid",
                    "ddc42928-faeb-42b5-96a9-0a68c7fbd075");

            Vertex ep3 = tx.addVertex(T.label, "EndPoint", "ipAddr", "10.10.3.18", "uuid",
                    "5c2881bc-9f96-4d4d-981c-680e56a1e908");

            Vertex ep4 = tx.addVertex(T.label, "EndPoint", "ipAddr", "10.10.1.14", "uuid",
                    "1066fc1c-02dc-4711-a48c-2fc8204077c2");
            
            Vertex ep5 = tx.addVertex(T.label, "EndPoint", "ipAddr", "10.10.1.11", "uuid",
                    "1066fc1c-02dc-4711-a48c-2fc8204077c9");
            
            Vertex ep6 = tx.addVertex(T.label, "EndPoint", "ipAddr", "10.10.1.19", "uuid",
                    "1066fc1c-02dc-4711-a48c-2fc82ertyiop");

            ep1.addEdge("BondTo", ed1, "lifeStart", 1451635200, "lifeEnd", 1464768061);
            ep2.addEdge("BondTo", ed2, "lifeStart", 1452635200, "lifeEnd", 1464768061);
            ep3.addEdge("BondTo", ed3, "lifeStart", 1452635200, "lifeEnd", 1464768061);
            ep4.addEdge("BondTo", ed4, "lifeStart", 1464564400, "lifeEnd", 1464764400);
            

            ///////////////////////////////

            Vertex app1 = tx.addVertex(T.label, "ServiceInstance", "serviceName", "WWW", "uuid",
                    "f62fce74-4366-4fb9-a0ac-b3a8f4d4300a");

            Vertex app2 = tx.addVertex(T.label, "ServiceInstance", "serviceName", "Cisco Jabber", "uuid",
                    "ddc42928-faeb-42b5-96a9-0a68c7fbd075");

            Vertex app3 = tx.addVertex(T.label, "ServiceInstance", "serviceName", "Webex", "uuid",
                    "3e3e924a-530d-4bda-8cf9-976bcff95c14");

            Vertex app4 = tx.addVertex(T.label, "ServiceInstance", "serviceName", "Youtube", "uuid",
                    "ff52a218-c543-4057-a37b-a4f17a00a9aa");

            app1.addEdge("ConsumeBy", ep1, "numBytes", 9300, "numPackets", 27, "lifeStart", 1451635200, "lifeEnd",
                    1464768061);
            app2.addEdge("ConsumeBy", ep2, "numBytes", 6300, "numPackets", 30, "lifeStart", 1452635200, "lifeEnd",
                    1464768061);
            app3.addEdge("ConsumeBy", ep3, "numBytes", 3000, "numPackets", 59, "lifeStart", 1452635200, "lifeEnd",
                    1464768061);
            app1.addEdge("ConsumeBy", ep4, "numBytes", 3200, "numPackets", 80, "lifeStart", 1464564400, "lifeEnd",
                    1464764400);
            
            
            app1.addEdge("Provide", ep5, "serviceName", "WWW", "lifeStart", 1451635200, "lifeEnd",1464768061);
            app2.addEdge("Provide", ep6, "serviceName", "Cisco Jabber","lifeStart", 1452635200, "lifeEnd",1464768061);
            app3.addEdge("Provide", ep5, "serviceName", "Webex",  "lifeStart", 1452635200, "lifeEnd",1464768061);
            app1.addEdge("Provide", ep6, "serviceName", "WWW","lifeStart", 1464564400, "lifeEnd",1464764400);
     


            tx.commit();
            
            graph.close();

        } catch (Exception ex) {
            ex.printStackTrace();
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
