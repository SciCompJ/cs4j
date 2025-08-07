/**
 * 
 */
package net.sci.image.discretize;

import net.sci.array.numeric.ScalarArray2D;
import net.sci.geom.geom2d.Domain2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.PolygonalDomain2D;

/**
 * Generate 2D phantom images.
 * 
 * @author dlegland
 *
 */
public class Phantoms2D
{
    /**
     * private constructor to prevent instantiations.
     */
    private Phantoms2D(){};
    
    public static final void fillDisk(ScalarArray2D<?> array, Point2D center, double radius, double value)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // get disk center
        double xc = center.x();
        double yc = center.y();
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (Math.hypot(x - xc, y - yc) <= radius)
                {
                    array.setValue(x, y, value);
                }
            }
        }
    }
    
    public static final void fillPolygon(ScalarArray2D<?> array, PolygonalDomain2D poly, double value)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (poly.contains(x, y))
                {
                    array.setValue(x, y, value);
                }
            }
        }
    }
    
    public static final void fillDomain(ScalarArray2D<?> array, Domain2D domain, double value)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (domain.contains(x, y))
                {
                    array.setValue(x, y, value);
                }
            }
        }
    }
}
