/**
 * 
 */
package net.sci.image.process.filter;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BoxNeighborhoodTest
{

    /**
     * Test method for {@link net.sci.image.process.filter.BoxNeighborhood#iterator()}.
     */
    @Test
    public final void testIterator_2d()
    {
        // create neighborhood
        int[] sizes = new int[] {7, 5};
        int[] pos = new int[] {10, 10};
        Neighborhood nbg = new BoxNeighborhood(sizes);
        
        // iterate over neighbors
        int count = 0;
        for (@SuppressWarnings("unused") int[] pos2 : nbg.neighbors(pos))
        {
            count++;
        }
        
        assertEquals(35, count);
    }

    /**
     * Test method for {@link net.sci.image.process.filter.BoxNeighborhood#iterator()}.
     */
    @Test
    public final void testIterator_3d()
    {
        // create neighborhood
        int[] sizes = new int[] {7, 5, 3};
        int[] pos = new int[] {10, 10, 10};
        Neighborhood nbg = new BoxNeighborhood(sizes);
        
        // iterate over neighbors
        int count = 0;
        for (@SuppressWarnings("unused") int[] pos2 : nbg.neighbors(pos))
        {
            count++;
        }
        
        assertEquals(105, count);
    }

}
