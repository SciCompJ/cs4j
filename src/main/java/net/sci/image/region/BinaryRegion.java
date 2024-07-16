/**
 * 
 */
package net.sci.image.region;

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
}
