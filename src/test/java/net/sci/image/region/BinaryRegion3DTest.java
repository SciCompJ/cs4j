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
public class BinaryRegion3DTest
{
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion3D#elementCount()}.
     */
    @Test
    public final void testElementCount()
    {
        BinaryRegion3D region = createHollowCube();
        assertEquals(26, region.elementCount());
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion3D#get(int, int, int)}.
     */
    @Test
    public final void testGetSet()
    {
        BinaryRegion3D region = createHollowCube();

        // check outside region
        assertFalse(region.get(0, 0, 0));
        assertFalse(region.get(5, 5, 5));
        
        // check within region
        assertTrue(region.get(2, 2, 2));
        assertTrue(region.get(4, 2, 2));
        assertTrue(region.get(2, 4, 2));
        assertTrue(region.get(4, 4, 2));
        assertTrue(region.get(2, 2, 4));
        assertTrue(region.get(4, 2, 4));
        assertTrue(region.get(2, 4, 4));
        assertTrue(region.get(4, 4, 4));
        
        // check the hole within the region
        assertFalse(region.get(3, 3, 3));
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion3D#positions()}.
     */
    @Test
    public final void testPositions()
    {
        BinaryRegion3D region = createHollowCube();
        
        int count = 0;
        for (@SuppressWarnings("unused") int[] pos : region.positions())
        {
            count++;
//            System.out.println(String.format("[%2d,%2d,%2d]", pos[0], pos[1], pos[2]));
        }
        
        assertEquals(26, count);
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion3D#positionIterator()}.
     */
    @Test
    public final void testPositionIterator()
    {
        BinaryRegion3D region = createHollowCube();
        
        BinaryRegion.PositionIterator iter = region.positionIterator();
        int[] pos = new int[3];
        
        int count = 0; 
        while(iter.hasNext())
        {
            count++;
            iter.next(pos);
//            System.out.println(String.format("[%2d,%2d,%2d]", pos[0], pos[1], pos[2]));
        }
        
        assertEquals(26, count);
    }
    
    /**
     * Test method for {@link net.sci.image.region.BinaryRegion3D#intBounds()}.
     */
    @Test
    public final void testIntBounds()
    {
        BinaryRegion3D region = new BinaryRegion3D();
        region.set(10, 10, 10, true);
        region.set( 2, 10, 10, true);
        region.set( 3, 10, 10, true);
        region.set( 4, 10, 10, true);
        region.set(18, 10, 10, true);
        region.set(19, 10, 10, true);
        region.set( 9,  3, 10, true);
        region.set(10,  3, 10, true);
        region.set(10, 18, 10, true);
        region.set(11, 18, 10, true);
        region.set(11, 10,  4, true);
        region.set(12, 10,  4, true);
        region.set(13, 10,  4, true);
        region.set( 6, 10, 17, true);
        region.set( 7, 10, 17, true);
        region.set( 8, 10, 17, true);
        
        int[][] bounds = region.intBounds();
        
        assertEquals( 2, bounds[0][0]);
        assertEquals(19, bounds[0][1]);
        assertEquals( 3, bounds[1][0]);
        assertEquals(18, bounds[1][1]);
        assertEquals( 4, bounds[2][0]);
        assertEquals(17, bounds[2][1]);
    }

    private static final BinaryRegion3D createHollowCube()
    {
        BinaryRegion3D region = new BinaryRegion3D();
        for (int z = 2; z < 5; z++)
        {
            for (int y = 2; y < 5; y++)
            {
                for (int x = 2; x < 5; x++)
                {
                    region.set(x, y, z, true);
                }
            }
        }
        region.set(3, 3, 3, false);
        return region;
    }
    
}
