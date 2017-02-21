/**
 * 
 */
package net.sci.image.process;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class Find
{
    /**
     * Find the position of non-zero pixels within a two-dimensional image.
     * 
     * @param image
     *            a scalar array representing an image.
     * @return the position of all non-zero pixels.
     */
    public static final Collection<Point2D> findPixels(ScalarArray2D<?> image)
    {
        int sizeX = image.getSize(0);
        int sizeY = image.getSize(1);
        
        ArrayList<Point2D> posList = new ArrayList<>();
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (image.getValue(x, y) != 0)
                {
                    posList.add(new Point2D(x, y));
                }
            }
        }
        
        return posList;
    }
}
