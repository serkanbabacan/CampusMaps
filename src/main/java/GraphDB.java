import org.xml.sax.SAXException;
import java.util.*;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class GraphDB {


    public Graph graph = new Graph();
    public TST<Vertex> tst = new TST<>();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    static String normalizeString(String s) {
        // Should match all strings that are not alphabetical
        String regex = "[^a-zA-Z]";
        return s.replaceAll(regex, "").toLowerCase(Locale.ENGLISH);
    }

    private void clean() {
        // Remove the vertices with no incoming and outgoing connections from your graph
        graph.nodes.entrySet().removeIf(entry -> (entry.getValue().adj.size() == 0) );

    }

    public double distance(Vertex v1, Vertex v2) {
        // Return the euclidean distance between two vertices
        return Math.sqrt(Math.pow((v2.getLng() - v1.getLng()),2) +
                Math.pow((v2.getLat() - v1.getLat()),2));
    }


    public long closest(double lon, double lat) {
        // Returns the closest vertex to the given latitude and longitude values
        Vertex temp = new Vertex(lat,lon,null);
        Map<Vertex,Double> map = new HashMap<>();
        for(Vertex V : graph.nodes.values()){
            double dist = distance(V,temp);
            map.put(V,dist);
        }
        Map.Entry<Vertex, Double> min = Collections.min(map.entrySet(),
                Comparator.comparing(Map.Entry::getValue));
        return min.getKey().getId();
    }

    double lon(long v) {
        // Returns the longitude of the given vertex, v is the vertex id
        return graph.nodes.get(v).getLng();
    }


    double lat(long v) {
        // Returns the latitude of the given vertex, v is the vertex id
        return graph.nodes.get(v).getLat();
    }
}
