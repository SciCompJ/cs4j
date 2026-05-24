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
import net.sci.array.Array;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.geom.Point;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.Point3D;

/**
 * Computes the collection of boundary points from a binary image.
 * 
 * For 2D binary images, returns the coordinates of the points located at the
 * middle of boundary edges. For 3D binary images, returns the coordinates of
 * the points located at the middle of boundary faces.
 */
public class BinaryImageBoundaryFacetMidPoints extends AlgoStub
{
    /**
     * Default empty constructor.
     */
    public BinaryImageBoundaryFacetMidPoints()
    {
    }
    
    /**
     * Computes the collection of boundary points from a binary array. The input
     * array is expected to contain binary elements. Boundary points are located
     * at the middle of the boundary facets.
     *
     * @param array
     *            the input array (2D or 3D array of binary elements expected)
     * @return the collection of boundary points, as a list of points.
     * @throws RuntimeException if the array does not contain elements, or if the dimensionality is not managed.
     */
    public List<? extends Point> process(Array<?> array)
    {
        if (array.elementClass() != Binary.class)
        {
            throw new RuntimeException("Requires an array containing binary elements, not " + array.elementClass().getName());
        }
        
        BinaryArray binaryArray = BinaryArray.wrap(array);
        return switch(binaryArray.dimensionality())
        {
            case 2 -> processBinary2d(BinaryArray2D.wrap(binaryArray));
            case 3 -> processBinary3d(BinaryArray3D.wrap(binaryArray));
            default -> throw new RuntimeException(
                    "Can not process array with dimensionality " + binaryArray.dimensionality());
        };
    }
    
    /**
     * Computes the collection of boundary points from a 2D binary image.
     * Boundary points are located at the middle of the boundary edges, making
     * each pixel having a "diamond" shape.
     *
     * @param array
     *            the 2D binary array
     * @return the collection of boundary points, as a list of Point2D.
     */
    public List<Point2D> processBinary2d(BinaryArray2D array)
    {
        return reduceMap2d(computeMap2d(array));
    }
    
    /**
     * Computes the collection of boundary points from a 2D binary array, by
     * returning the result as a map of x-coordinates indexed by the
     * y-coordinate. This structure allows to efficiently compute related
     * structures such as convex hulls or bounding boxes.
     * 
     * @param array
     * @return
     */
    public TreeMap<Double, TreeSet<Double>> computeMap2d(BinaryArray2D array)
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
                if (upPixel != currentPixel) addPoint(pointMaps, x, y - 0.5);
                
                // check boundary with left pixel
                if (leftPixel != currentPixel) addPoint(pointMaps, x - 0.5, y);

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
     * Computes the collection of boundary points from a 3D binary image.
     * Boundary points are located at the middle of the boundary edges, making
     * each voxel having an octahedron shape.
     *
     * @param array
     *            the 3D binary array
     * @return the collection of boundary points, as a list of Point3D.
     */
    public List<Point3D> processBinary3d(BinaryArray3D array)
    {
        return reduceMap3d(computeMap3d(array));
    }
    
    /**
     * Computes the collection of boundary points from a 3D binary array, by
     * returning the result in a nested Map data structure. This structure can
     * be used to efficiently compute related structures such as convex hulls or
     * bounding boxes.
     * 
     * Results are organized as follow:
     * <ul>
     * <li>The keys of the first map correspond to the z-coordinates of the
     * boundary points.</li>
     * <li>The associated value is also a map, using the y-coordinates of the
     * boundary points as keys</li>
     * <li>The associated value of the second map is a set of
     * x-coordinates.</li>
     * </ul>
     * 
     * @param array the binary array to process
     * @return the coordinates of the boundary points in a nested map structure. 
     */
    public TreeMap<Double, TreeMap<Double, TreeSet<Double>>> computeMap3d(BinaryArray3D array)
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
                    topVoxel = x < sizeX && y < sizeY  && z > 0 ? array.getBoolean(x, y, z - 1) : false;
                    upVoxel = x < sizeX && y > 0  && z < sizeZ ? array.getBoolean(x, y - 1, z) : false;
                    
                    // check boundary by comparing current voxel with neighbors
                    if (topVoxel != currentVoxel) addPoint(pointMaps, x, y, z - 0.5);
                    if (upVoxel != currentVoxel) addPoint(pointMaps, x, y - 0.5, z);
                    if (leftVoxel != currentVoxel) addPoint(pointMaps, x - 0.5, y, z);
                    
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

    /**
     * Converts the data structure returned by the {@code processBinary3d}
     * method into a list of 3D points corresponding to the middle points of the
     * boundary faces of voxels within a binary image.
     * 
     * @param map
     *            a map of map of x-coordinates indexed by y-coordinates, indexed by z-coordinates
     * @return a list of instances of Point2D
     */
    public static final List<Point3D> reduceMap3d(Map<Double, TreeMap<Double, TreeSet<Double>>> map)
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

}
