/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.geom.geom2d.Point2D;

/**
 * Computes the collection of boundary points from a binary image.
 * 
 * For 2D binary images, returns the coordinates of the points located at the
 * middle of boundary edges. Points are stored in a map of x-coordinates indexed
 * by the y-coordinate. This structure allows to efficiently compute related
 * structures such as convex hulls or bounding boxes.
 * 
 * For 3D binary images, returns the coordinates of the points located at the
 * middle of boundary faces. Points are stored in a doubly-nested Map data
 * structure, in voxel coordinates.
 */
public class BinaryImageBoundaryFacetMidPoints extends AlgoStub
{
    /**
     * Converts the data structure returned by the {@code processBinary2d}
     * method into a list of points corresponding to the middle points of the
     * boundary edges of pixels within a binary image.
     * 
     * @param map
     *            a map of x-coordinates indexed by y-coordinates
     * @return a list of instances of Point2D
     */
    public static final List<Point2D> reduce(Map<Double, TreeSet<Double>> map)
    {
        int n = map.values().stream().mapToInt(set -> set.size()).sum();
        ArrayList<Point2D> list = new ArrayList<Point2D>(n);
        map.keySet().forEach(y -> map.get(y).forEach(x -> list.add(Point2D.of(x, y))));
        return list;
    }
    
    /**
     * Default empty constructor.
     */
    public BinaryImageBoundaryFacetMidPoints()
    {
    }
    
    public TreeMap<Double, TreeSet<Double>> processBinary2d(BinaryArray2D array)
    {
        // retrieve label map data
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // organize the boundary points within a map, using the
        // y-coordinate of the points as map key, and listing all the
        // x-coordinates within the row within an TreeSet
        TreeMap<Double, TreeSet<Double>> pointMaps = new TreeMap<Double, TreeSet<Double>>();
        
        // value of current, up, and left pixels.
        boolean currentPixel, upPixel, leftPixel;
        
        // iterate on image pixel configurations
        for (int y = 0; y < sizeY + 1; y++) 
        {
            this.fireProgressChanged(this, y, sizeY);
            
            leftPixel = false;
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                currentPixel = x < sizeX & y < sizeY ? array.getBoolean(x, y) : false;
                upPixel = x < sizeX & y > 0 ? array.getBoolean(x, y - 1) : false;

                // check boundary with upper pixel
                if (upPixel != currentPixel)
                {
                    addPoint(pointMaps, x, y - 0.5);
                }
                
                // check boundary with left pixel
                if (leftPixel != currentPixel)
                {
                    addPoint(pointMaps, x - 0.5, y);
                }

                // update values of left label for next iteration
                leftPixel = currentPixel;
            }
        }

        return pointMaps;
    }
    
    private static final void addPoint(TreeMap<Double, TreeSet<Double>> map, double x, double y)
    {
        TreeSet<Double> set = map.get(y);
        if (set == null)
        {
            set = new TreeSet<Double>();
        }
        set.add(x);
        map.put(y, set);
    }
}
