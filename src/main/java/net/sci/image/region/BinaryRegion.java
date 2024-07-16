/**
 * 
 */
package net.sci.image.region;

import java.util.Iterator;

import net.sci.array.Dimensional;

/**
 * General definition of a binary region, independently of the dimension.
 * 
 * See a region as a container of positions.
 */
public interface BinaryRegion extends Dimensional
{
    /**
     * Counts the number of elements within this binary region.
     * 
     * @return the number of elements within this binary region.
     */
    public long elementCount();
    
    public Iterable<int[]> positions();
    
    public interface PositionIterator extends Iterator<int[]>
    {
        public void next(int[] pos);
    }
}
