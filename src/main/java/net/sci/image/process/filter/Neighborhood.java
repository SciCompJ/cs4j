/**
 * 
 */
package net.sci.image.process.filter;

/**
 * Iterates over the neighbor of an array element.
 * 
 * Instances of the Neighborhood interface are supposed to be defined with
 * respect to an element.
 * 
 * @author dlegland
 */
public interface Neighborhood
{
    /**
     * Returns an Iterable on the neighbors of a given position.
     * 
     * @param pos
     *            the reference position
     * @return the neighbors of the position (may contain the original position,
     *         depending on neighborhood definition).
     */
    public Iterable<int[]> neighbors(int[] pos);
}
