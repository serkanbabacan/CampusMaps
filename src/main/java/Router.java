import java.util.*;


public class Router {

    private static List<Vertex> stops = new ArrayList<>();
    private static Vertex start, end;

    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        Map<Long, Long> edgeTo = new HashMap<>();
        Map<Long, Double> distTo = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        LinkedList<Long> route = new LinkedList<>();
        long src = g.closest(stlon, stlat);
        long dest = g.closest(destlon, destlat);
        start = g.graph.nodes.get(src);
        end = g.graph.nodes.get(dest);
        PriorityQueue<Long> pq = new PriorityQueue<Long>(new Comparator<Long>() {
            @Override
            public int compare(Long w, Long v) {
                double wCost = distTo.get(w) + g.distance(g.graph.nodes.get(w), g.graph.nodes.get(dest));
                double vCost = distTo.get(v) + g.distance(g.graph.nodes.get(v), g.graph.nodes.get(dest));
                if (wCost < vCost) {
                    return -1;
                }
                if (wCost > vCost) {
                    return 1;
                }
                return 0;
            }
        });

        // Add initial values to edgeTo, distTo, and pq
        for (long v : g.graph.nodes.keySet()) {
            distTo.put(v, Double.POSITIVE_INFINITY);
            edgeTo.put(v, (long) -117);
        }
        distTo.replace(src, 0.0);
        edgeTo.put(src, (long) 0);
        pq.add(src);

        // search algorithm
        while (!pq.isEmpty()) {
            long curr = pq.poll();
            if (curr == dest) {
                break;
            }
            if (!visited.contains(curr)) {
                visited.add(curr);
                for (Edge E : g.graph.nodes.get(curr).adj) {
                    Long neighbo = E.getDestination().getId();
                    double distance = distTo.get(curr) + g.distance(g.graph.nodes.get(curr), E.getDestination());
                    if (distance < distTo.get(neighbo)) {
                        distTo.put(neighbo, distance);
                        edgeTo.put(neighbo, curr);
                        pq.add(neighbo);
                    }
                }
            }
        }

        // Get path to destination.
        for (long e = dest; e != 0; e = edgeTo.get(e)) {
            route.add(0, e);
        }

        return route;
    }




    public static LinkedList<Long> addStop(GraphDB g, double lat, double lon) {
        // Find the closest vertex to the stop coordinates using g.closest()
        // Add the stop to the stop list
        // Recalculate your route when a stop is added and return the new route

        Vertex stop = g.graph.nodes.get(g.closest(lon,lat));
        stops.add(stop);

        //Save the start and end variables
        Vertex src = new Vertex(start.getLat(),start.getLng(),start.getId());
        Vertex dest = new Vertex(end.getLat(),end.getLng(), end.getId());

        //Path from source to stop 1
        LinkedList<Long> srctostop = shortestPath(g,src.getLng(),src.getLat(),stops.get(0).getLng(),stops.get(0).getLat());

        //Path from path 1 to path n
        for(int i=1;i<stops.size();i++){
            LinkedList<Long> stopToStop = shortestPath(g,stops.get(i-1).getLng(),stops.get(i-1).getLat(),
                    stops.get(i).getLng(),stops.get(i).getLat());
            srctostop.addAll(stopToStop);
        }
        int size = stops.size();

        //path from stop n to end
        LinkedList<Long> stopToEnd = shortestPath(g,stops.get(size-1).getLng(),stops.get(size-1).getLat(),dest.getLng(),dest.getLat());
        srctostop.addAll(stopToEnd);

        //Put the start and end variables again
        start = src;
        end = dest;
        return srctostop;
    }

    public static void clearRoute() {
        start = null;
        end = null;
        stops = new ArrayList<>();
    }
}
