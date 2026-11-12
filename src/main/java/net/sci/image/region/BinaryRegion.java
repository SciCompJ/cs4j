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
     * Checks whether the region is empty.
     * 
     * @return true is the region is empty.
     */
    public boolean isEmpty();
    
    /**
     * Counts the number of elements within this binary region.
     * 
     * @return the number of elements within this binary region.
     */
    public long elementCount();
    
    /**
     * Returns the (integer) bounds enclosed by this region, as an array of int
     * arrays.
     * 
     * @return the (integer) bounds enclosed by this region, as an array of int
     *         arrays. The size of the resulting array in nd-by-2.
     */
    public int[][] intBounds();
    
    /**
     * Allows to iterate over positions in a region.
     * 
     * {@snippet :
     * for (int[] pos : region.positions())
     * {
     *     doProcessing(array, pos);
     * }
     * }
     * 
     * @see #positionIterator()
     * 
     * @return an Iterable over region positions.
     */
    public Iterable<int[]> positions();
    
    /**
     * Allows to iterate over positions in a region, by pre-allocating int array
     * for storing positions.
     * 
     * {@snippet :
     * BinaryRegion.PositionIterator iter = region.positionIterator();
     * int[] pos = new int[region.dimensionality()];
     * while (iter.hasNext())
     * {
     *     iter.next(pos);
     *     doProcessing(array, pos);
     * }
     * }
     * 
     * @see #positionIterator()
     * 
     * @return an Iterable over region positions.
     */
    public PositionIterator positionIterator();
    
    /**
     * Allows for iterating over the positions within a binary region. Positions
     * are represented as int arrays.
     * 
     * The interface extends Iterator, and additionally provides a method for
     * iterating over a pre-allocated int array.
     */
    public interface PositionIterator extends Iterator<int[]>
    {
        /**
         * Iterates to the next position, and populates the pre-allocated int
         * array.
         * 
         * @param pos the pre-allocated int array for storing the position.
         */
        public void next(int[] pos);
    }
}
