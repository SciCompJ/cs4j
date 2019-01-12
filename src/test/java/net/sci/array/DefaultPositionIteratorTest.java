/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class DefaultPositionIteratorTest
{

    /**
     * Test method for {@link net.sci.array.DefaultPositionIterator#forward()}.
     */
    @Test
    public final void testForward_2d()
    {
        int[] dims = new int[]{4,3};
        DefaultPositionIterator iter = new DefaultPositionIterator(dims);
        int count = 0;
        while(iter.hasNext())
        {
            iter.forward();
            count++;
        }
        assertEquals(12, count);
    }

    /**
     * Test method for {@link net.sci.array.DefaultPositionIterator#forward()}.
     */
    @Test
    public final void testForward_3d()
    {
        int[] dims = new int[]{4, 3, 2};
        DefaultPositionIterator iter = new DefaultPositionIterator(dims);
        int count = 0;
        while(iter.hasNext())
        {
            iter.forward();
            count++;
        }
        assertEquals(24, count);
    }

}
