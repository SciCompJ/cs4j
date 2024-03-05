/**
 * 
 */
package net.sci.image.binary;

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
     * Test method for {@link net.sci.image.binary.BinaryRegion2D#elementCount()}.
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
     * Test method for {@link net.sci.image.binary.BinaryRegion2D#get(int, int)}.
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
    
}
