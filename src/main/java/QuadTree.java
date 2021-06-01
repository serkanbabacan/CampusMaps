import java.util.*;


public class QuadTree {
    public QTreeNode root;
    private String imageRoot;

    public QuadTree(String imageRoot) {
        // Instantiate the root element of the tree with depth 0
        // Use the ROOT_ULLAT, ROOT_ULLON, ROOT_LRLAT, ROOT_LRLON static variables of MapServer class
        // Call the build method with depth 1
        // Save the imageRoot value to the instance variable
        root = new QTreeNode("root" ,MapServer.ROOT_ULLAT,MapServer.ROOT_ULLON,MapServer.ROOT_LRLAT,MapServer.ROOT_LRLON,0);
        build(root,1);
        this.imageRoot = imageRoot;
    }

    public void build(QTreeNode subTreeRoot, int depth) {
        // Recursive method to build the tree
        if(depth> 7)
            return;

        String name = subTreeRoot.getName();
        if(name.equals("root"))
            name = "";

        subTreeRoot.NW = new QTreeNode(name + "1", subTreeRoot.getUpperLeftLatitude(), subTreeRoot.getUpperLeftLongtitude(),
                (subTreeRoot.getUpperLeftLatitude() + subTreeRoot.getLowerRightLatitude()) / 2,
                (subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude()) / 2,
                depth);
        build(subTreeRoot.NW, depth+1);

        subTreeRoot.NE = new QTreeNode(name + "2", subTreeRoot.getUpperLeftLatitude(),
                (subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude()) / 2,
                (subTreeRoot.getUpperLeftLatitude() + subTreeRoot.getLowerRightLatitude()) / 2,
                subTreeRoot.getLowerRightLongtitude(),
                depth);
        build(subTreeRoot.NE, depth+1);


        subTreeRoot.SW = new QTreeNode(name + "3", (subTreeRoot.getUpperLeftLatitude() + subTreeRoot.getLowerRightLatitude()) / 2,
                subTreeRoot.getUpperLeftLongtitude(),
                subTreeRoot.getLowerRightLatitude(),
                (subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude()) / 2,
                depth);
        build(subTreeRoot.SW, depth+1);

        subTreeRoot.SE = new QTreeNode(name + "4", (subTreeRoot.getUpperLeftLatitude() + subTreeRoot.getLowerRightLatitude()) / 2,
                (subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude()) / 2,
                subTreeRoot.getLowerRightLatitude(),
                subTreeRoot.getLowerRightLongtitude(),
                depth);
        build(subTreeRoot.SE, depth+1);


    }

    public Map<String, Object> search(Map<String, Double> params) {
        /*
         * Parameters are:
         * "ullat": Upper left latitude of the query box
         * "ullon": Upper left longitude of the query box
         * "lrlat": Lower right latitude of the query box
         * "lrlon": Lower right longitude of the query box
         * */

        // Instantiate a QTreeNode to represent the query box defined by the parameters
        // Calculate the lonDpp value of the query box
        // Call the search() method with the query box and the lonDpp value
        // Call and return the result of the getMap() method to return the acquired nodes in an appropriate way

        QTreeNode box = new QTreeNode("box", params.get("ullat"),params.get("ullon"),params.get("lrlat"),params.get("lrlon"),0);
        double lonDpp = (box.getLowerRightLongtitude() - box.getUpperLeftLongtitude()) / params.get("w");
        ArrayList<QTreeNode> list = new ArrayList<>();
        search(box,root,lonDpp,list);
        return getMap(list);
    }

    private Map<String, Object> getMap(ArrayList<QTreeNode> list) {
        Map<String, Object> map = new HashMap<>();

        // Check if the root intersects with the given query box
        if (list.size()==0) {
            map.put("query_success", false);
            return map;
        }

        // Use the get2D() method to get organized images in a 2D array
        map.put("render_grid", get2D(list));

        // Upper left latitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_ul_lat", list.get(0).getUpperLeftLatitude());

        // Upper left longitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_ul_lon", list.get(0).getUpperLeftLongtitude());

        // Upper lower right latitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_lr_lat", list.get(list.size()-1).getLowerRightLatitude());

        // Upper lower right longitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_lr_lon", list.get(list.size()-1).getLowerRightLongtitude());

        // Depth of the grid (can be thought as the depth of a single image)
        map.put("depth", list.get(0).getDepth());

        map.put("query_success", true);
        return map;
    }

    private String[][] get2D(ArrayList<QTreeNode> list) {
        // After you retrieve the list of images using the recursive search mechanism described above, you
        // should order them as a grid. This grid is nothing more than a 2D array of file names. To order
        // the images, you should determine correct row and column for each image (node) in the retrieved
        // list. As a hint, you should consider the latitude values of images to place them in the row, and
        // the file names of the images to place them in a column.

        //Create a map with ULLAT as keys and list of node names as values
        Map<Double,ArrayList<String>> map = new LinkedHashMap<>();
        for(QTreeNode node : list){
            if(map.containsKey(node.getUpperLeftLatitude()))
                map.get(node.getUpperLeftLatitude()).add(node.getName());
            else{
                ArrayList<String> arr = new ArrayList<>();
                arr.add(node.getName());
                map.put(node.getUpperLeftLatitude(),arr);
            }
        }
        String[][] images = new String[map.size()][];

        int i=0;
        for(double j : map.keySet()){
            images[i]= new String[map.get(j).size()];
            for(int k = 0;k<map.get(j).size();k++){
                images[i][k] = imageRoot + map.get(j).get(k) + ".png";
            }
            i++;
        }

        return images;
    }

    public void search(QTreeNode queryBox, QTreeNode tile, double lonDpp, ArrayList<QTreeNode> list) {
        // The first part includes a recursive search in the tree. This process should consider both the
        // lonDPP property (discussed above) and if the images in the tree intersect with the query box.
        // (To check the intersection of two tiles, you should use the checkIntersection() method)
        // To achieve this, you should retrieve the first depth (zoom level) of the images which intersect
        // with the query box and have a lower lonDPP than the query box.
        // This method should fill the list given by the "ArrayList<QTreeNode> list" parameter

        if(checkIntersection(tile,queryBox)){
            if(tile.getLonDPP() <= lonDpp || tile.getDepth() >6){
                list.add(tile);
            }
                else{
                    search(queryBox,tile.NW,lonDpp,list);
                    search(queryBox,tile.NE,lonDpp,list);
                    search(queryBox,tile.SW,lonDpp,list);
                    search(queryBox,tile.SE,lonDpp,list);
            }
        }

    }

    public boolean checkIntersection(QTreeNode tile, QTreeNode queryBox) {
        // Return true if two tiles are intersecting with each other
        return queryBox.getUpperLeftLongtitude() <= tile.getLowerRightLongtitude() &&
                queryBox.getLowerRightLongtitude() >= tile.getUpperLeftLongtitude() &&
                queryBox.getUpperLeftLatitude() >= tile.getLowerRightLatitude() &&
                queryBox.getLowerRightLatitude() <=tile.getUpperLeftLatitude();
    }
}