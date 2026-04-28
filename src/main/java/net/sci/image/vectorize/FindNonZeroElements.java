/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.Array;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.geom.Point;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.Point3D;

/**
 * Finds the position of non-zero pixels within the image.
 * 
 * @author dlegland
 *
 */
public class FindNonZeroElements
{
    /**
     * Find the position of non-zero pixels within a two-dimensional image.
     * 
     * @param array
     *            a scalar array representing an image.
     * @return the position of all non-zero pixels.
     */
    public static final Collection<Point2D> findPixels(ScalarArray2D<?> array)
    {
        return new FindNonZeroElements().process2d(array);
    }
    
    /**
     * Find the position of non-zero voxels within a three-dimensional image.
     * 
     * @param array
     *            a scalar array representing an image.
     * @return the position of all non-zero pixels.
     */
    public static final Collection<Point3D> findVoxels(ScalarArray3D<?> array)
    {
        return new FindNonZeroElements().process3d(array);
    }
    
    /**
     * Default empty constructor.
     */
    public FindNonZeroElements()
    {
    }
    
    public Collection<? extends Point> process(Array<?> array)
    {
        // first check type
        if (Scalar.class.isAssignableFrom(array.elementClass()))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> scalarArray = ScalarArray.wrap((Array<? extends Scalar>) array);
            
            return switch (scalarArray.dimensionality())
            {
                case 2 -> process2d(ScalarArray2D.wrap(scalarArray));
                case 3 -> process2d(ScalarArray2D.wrap(scalarArray));
                default -> throw new IllegalArgumentException("Implemented only for 2D and 3D arrays, not: " + scalarArray.dimensionality());
            };
        }
        else
        {
            throw new IllegalArgumentException("Not implemented for arrays with class: " + array.elementClass());
        }
    }
    
    /**
     * Find the position of non-zero pixels within a two-dimensional image.
     * 
     * @param array
     *            a scalar array representing an image.
     * @return the position of all non-zero pixels.
     */
    public Collection<Point2D> process2d(ScalarArray2D<?> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        ArrayList<Point2D> posList = new ArrayList<>();
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (array.getValue(x, y) != 0)
                {
                    posList.add(new Point2D(x, y));
                }
            }
        }
        
        return posList;
    }
    /**
     * Find the position of non-zero voxels within a three-dimensional image.
     * 
     * @param array
     *            a scalar array representing an image.
     * @return the position of all non-zero pixels.
     */
    public Collection<Point3D> process3d(ScalarArray3D<?> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        ArrayList<Point3D> posList = new ArrayList<>();
        
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    if (array.getValue(x, y, z) != 0)
                    {
                        posList.add(new Point3D(x, y, z));
                    }
                }
            }
        }
        return posList;
    }
}
