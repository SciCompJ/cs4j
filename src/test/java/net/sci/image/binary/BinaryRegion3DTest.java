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
public class BinaryRegion3DTest
{
    
    /**
     * Test method for {@link net.sci.image.binary.BinaryRegion3D#elementCount()}.
     */
    @Test
    public final void testElementCount()
    {
        BinaryRegion3D region = createHollowCube();
        assertEquals(26, region.elementCount());
    }
    
    /**
     * Test method for {@link net.sci.image.binary.BinaryRegion3D#get(int, int, int)}.
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
