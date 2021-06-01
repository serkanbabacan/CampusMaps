
import java.util.*;

public class Graph {
    // Implement the graph data structure here
    // Use Edge and Vertex classes as you see fit

    //List of Nodes in a map with their id's as keys
    public Map<Long,Vertex> nodes ;

    Graph(){
        nodes = new HashMap<>();

    }

    //Add a vertex to the graph and set its adj list
    public void add_vertex(Vertex V){
        nodes.put(V.getId(),V);
    }

    public void add_ways(GraphBuildingHandler.Way ways){
        for(int i = 0; i < ways.getListOfNodes().size()-1 ; i++) {
            Edge road = new Edge();
            road.setName(ways.getName());
            road.setSpeed(ways.getSpeed());

            // If one way tag is true
            if(ways.isOneWay()){
                Long src_id = ways.getListOfNodes().get(i);
                Long dest_id = ways.getListOfNodes().get(i+1);

                Vertex src = nodes.get(src_id);
                Vertex dest = nodes.get(dest_id);

                road.setSource(src);
                road.setDestination(dest);

                src.adj.add(road);
            }

            // If one way tag is false or there is no tag
            // Then assume its two way road
            else{
                //Road to dest to source
                Edge road2 = new Edge();
                road2.setName(ways.getName());
                road2.setSpeed(ways.getSpeed());

                Long src_id = ways.getListOfNodes().get(i);
                Long dest_id = ways.getListOfNodes().get(i+1);

                Vertex src = nodes.get(src_id);
                Vertex dest = nodes.get(dest_id);

                road.setSource(src);
                road.setDestination(dest);

                src.adj.add(road);

                road2.setSource(dest);
                road2.setDestination(src);

                dest.adj.add(road2);

            }
        }

    }




}
