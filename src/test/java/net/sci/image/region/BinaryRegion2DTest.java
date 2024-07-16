/**
 * 
 */
package net.sci.image.region;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class BinaryRegion2DTest
{
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion2D#elementCount()}.
     */
    @Test
    public final void testElementCount()
    {
        BinaryRegion2D region = new BinaryRegion2D();
        for (int y = 2; y < 5; y++)
        {
            for (int x = 2; x < 5; x++)
            {
                region.set(x, y, true);
            }
        }
        region.set(3, 3, false);
        
        assertEquals(8, region.elementCount());
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion2D#get(int, int)}.
     */
    @Test
    public final void testGetSet()
    {
        BinaryRegion2D region = new BinaryRegion2D();
        for (int y = 2; y < 5; y++)
        {
            for (int x = 2; x < 5; x++)
            {
                region.set(x, y, true);
            }
        }
        region.set(3, 3, false);
        
        // check outside region
        assertFalse(region.get(0, 0));
        assertFalse(region.get(5, 5));
        
        // check within region
        assertTrue(region.get(2, 2));
        assertTrue(region.get(4, 2));
        assertTrue(region.get(2, 4));
        assertTrue(region.get(4, 4));
        
        // check the hole within the region
        assertFalse(region.get(3, 3));
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion2D#positions()}.
     */
    @Test
    public final void testPositions()
    {
        BinaryRegion2D region = new BinaryRegion2D();
        for (int y = 2; y < 5; y++)
        {
            for (int x = 2; x < 5; x++)
            {
                region.set(x, y, true);
            }
        }
        region.set(3, 3, false);
        
        int count = 0;
        for (@SuppressWarnings("unused") int[] pos : region.positions())
        {
            count++;
//            System.out.println(String.format("[%2d,%2d]", pos[0], pos[1]));
        }
        
        assertEquals(8, count);
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion2D#positionIterator()}.
     */
    @Test
    public final void testPositionIterator()
    {
        BinaryRegion2D region = new BinaryRegion2D();
        for (int y = 2; y < 5; y++)
        {
            for (int x = 2; x < 5; x++)
            {
                region.set(x, y, true);
            }
        }
        region.set(3, 3, false);
        
        BinaryRegion.PositionIterator iter = region.positionIterator();
        int[] pos = new int[2];
        
        int count = 0; 
        while(iter.hasNext())
        {
            count++;
            iter.next(pos);
//            System.out.println(String.format("[%2d,%2d]", pos[0], pos[1]));
        }
        
        assertEquals(8, count);
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion2D#intBounds()}.
     */
    @Test
    public final void testIntBounds()
    {
        BinaryRegion2D region = new BinaryRegion2D();
        region.set( 6,  4, true);
        region.set( 8,  5, true);
        region.set( 5,  6, true);
        region.set( 3,  7, true);
        region.set( 4,  8, true);
        region.set( 5,  9, true);
        
        int[][] bounds = region.intBounds();
        
        assertEquals(3, bounds[0][0]);
        assertEquals(8, bounds[0][1]);
        assertEquals(4, bounds[1][0]);
        assertEquals(9, bounds[1][1]);
    }

}
