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
import net.sci.array.binary.BinaryArray3D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.Point3D;

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
    public static final List<Point2D> reduceMap2d(Map<Double, TreeSet<Double>> map)
    {
        int n = map.values().stream().mapToInt(set -> set.size()).sum();
        ArrayList<Point2D> list = new ArrayList<Point2D>(n);
        map.keySet().forEach(y -> map.get(y).forEach(x -> list.add(Point2D.of(x, y))));
        return list;
    }
    
    /**
     * Converts the data structure returned by the {@code processBinary3d}
     * method into a list of 3D points corresponding to the middle points of the
     * boundary faces of voxels within a binary image.
     * 
     * @param map
     *            a map of map of x-coordinates indexed by y-coordinates, indexed by z-coordinates
     * @return a list of instances of Point2D
     */
    public static final List<Point3D> reduceMap3d(TreeMap<Double, TreeMap<Double, TreeSet<Double>>> map)
    {
        int n = map.values().stream().mapToInt(set -> set.size()).sum();
        ArrayList<Point3D> list = new ArrayList<Point3D>(n);
        map.keySet().forEach(z -> 
        {
            TreeMap<Double, TreeSet<Double>> map2 = map.get(z);
            map2.keySet().forEach(y -> 
                map2.get(y).forEach(x -> list.add(Point3D.of(x, y, z))));
        });
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
    
    public TreeMap<Double, TreeMap<Double, TreeSet<Double>>> processBinary3d(BinaryArray3D array)
    {
        // retrieve label map data
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // organize the boundary points within a map, using the
        // y-coordinate of the points as map key, and listing all the
        // x-coordinates within the row within an TreeSet
        TreeMap<Double, TreeMap<Double, TreeSet<Double>>> pointMaps = new TreeMap<Double, TreeMap<Double, TreeSet<Double>>>();
        
        // value of current, up, and left pixels.
        boolean currentVoxel, topVoxel, upVoxel, leftVoxel;
        
        // iterate on image pixel configurations
        for (int z = 0; z < sizeZ + 1; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY + 1; y++) 
            {
                leftVoxel = false;
                for (int x = 0; x < sizeX + 1; x++) 
                {
                    // update pixel values of configuration
                    currentVoxel = x < sizeX && y < sizeY && z < sizeZ ? array.getBoolean(x, y, z) : false;
                    upVoxel = x < sizeX && y > 0  && z < sizeZ ? array.getBoolean(x, y - 1, z) : false;
                    topVoxel = x < sizeX && y < sizeY  && z > 0 ? array.getBoolean(x, y, z - 1) : false;
                    
                    if (topVoxel != currentVoxel)
                    {
                        addPoint(pointMaps, x, y, z - 0.5);
                    }
                    
                    // check boundary with upper pixel
                    if (upVoxel != currentVoxel)
                    {
                        addPoint(pointMaps, x, y - 0.5, z);
                    }
                    
                    // check boundary with left pixel
                    if (leftVoxel != currentVoxel)
                    {
                        addPoint(pointMaps, x - 0.5, y, z);
                    }
                    
                    // update values of left label for next iteration
                    leftVoxel = currentVoxel;
                }
            }
        }

        return pointMaps;
    }
    
    private static final void addPoint(TreeMap<Double, TreeMap<Double, TreeSet<Double>>> map, double x, double y, double z)
    {
        // retrieve map for z-coordinate
        TreeMap<Double, TreeSet<Double>> mapZ = map.get(z);
        if (mapZ == null)
        {
            mapZ = new TreeMap<Double, TreeSet<Double>>();
        }
        
        // retrieve set for y-coordinate
        TreeSet<Double> set = mapZ.get(y);
        if (set == null)
        {
            set = new TreeSet<Double>();
        }
        
        // update nested data structure
        set.add(x);
        mapZ.put(y, set);
        map.put(z, mapZ);
    }

}
