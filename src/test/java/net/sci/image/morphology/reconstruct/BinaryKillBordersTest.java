/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;

/**
 * @author dlegland
 *
 */
public class BinaryKillBordersTest
{

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.BinaryKillBorders#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d()
    {
        BinaryArray2D array = createBlocksArray2d();
        
        BinaryKillBorders algo = new BinaryKillBorders();
        BinaryArray2D res = algo.processBinary2d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(4, 0));
        assertFalse(res.getBoolean(8, 0));
        assertFalse(res.getBoolean(0, 4));
        assertTrue(res.getBoolean(4, 4));
        assertFalse(res.getBoolean(8, 4));
        assertFalse(res.getBoolean(0, 8));
        assertFalse(res.getBoolean(4, 8));
        assertFalse(res.getBoolean(8, 8));
    }

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.BinaryKillBorders#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testProcessBinary3d()
    {
        BinaryArray3D array = createBlocksArray3d();
        
        BinaryKillBorders algo = new BinaryKillBorders();
        BinaryArray3D res = algo.processBinary3d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        assertFalse(res.getBoolean(0, 0, 0));
        assertFalse(res.getBoolean(4, 0, 0));
        assertFalse(res.getBoolean(8, 0, 0));
        assertFalse(res.getBoolean(0, 4, 0));
        assertFalse(res.getBoolean(4, 4, 0));
        assertFalse(res.getBoolean(8, 4, 0));
        assertFalse(res.getBoolean(0, 8, 0));
        assertFalse(res.getBoolean(4, 8, 0));
        assertFalse(res.getBoolean(8, 8, 0));
        
        assertFalse(res.getBoolean(0, 0, 4));
        assertFalse(res.getBoolean(4, 0, 4));
        assertFalse(res.getBoolean(8, 0, 4));
        assertFalse(res.getBoolean(0, 4, 4));
        assertTrue(res.getBoolean(4, 4, 4));
        assertFalse(res.getBoolean(8, 4, 4));
        assertFalse(res.getBoolean(0, 8, 4));
        assertFalse(res.getBoolean(4, 8, 4));
        assertFalse(res.getBoolean(8, 8, 4));
        
        assertFalse(res.getBoolean(0, 0, 8));
        assertFalse(res.getBoolean(4, 0, 8));
        assertFalse(res.getBoolean(8, 0, 8));
        assertFalse(res.getBoolean(0, 4, 8));
        assertFalse(res.getBoolean(4, 4, 8));
        assertFalse(res.getBoolean(8, 4, 8));
        assertFalse(res.getBoolean(0, 8, 8));
        assertFalse(res.getBoolean(4, 8, 8));
        assertFalse(res.getBoolean(8, 8, 8));
    }

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.BinaryKillBorders#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess()
    {
        BinaryArray2D array = createBlocksArray2d();
        
        BinaryKillBorders algo = new BinaryKillBorders();
        BinaryArray2D res = BinaryArray2D.wrap(algo.process(array));
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(4, 0));
        assertFalse(res.getBoolean(8, 0));
        assertFalse(res.getBoolean(0, 4));
        assertTrue(res.getBoolean(4, 4));
        assertFalse(res.getBoolean(8, 4));
        assertFalse(res.getBoolean(0, 8));
        assertFalse(res.getBoolean(4, 8));
        assertFalse(res.getBoolean(8, 8));
    }
    
    private static final BinaryArray2D createBlocksArray2d()
    {
        BinaryArray2D array = BinaryArray2D.create(10, 10);
        
        for (int y = 0; y < 2; y++)
        {
            for (int x = 0; x < 2; x++)
            {
                array.setBoolean(x    , y    , true);
                array.setBoolean(x + 4, y    , true);
                array.setBoolean(x + 8, y    , true);
                array.setBoolean(x    , y + 4, true);
                array.setBoolean(x + 4, y + 4, true);
                array.setBoolean(x + 8, y + 4, true);
                array.setBoolean(x    , y + 8, true);
                array.setBoolean(x + 4, y + 8, true);
                array.setBoolean(x + 8, y + 8, true);
            }
        }
        
        return array;
    }

    private static final BinaryArray3D createBlocksArray3d()
    {
        BinaryArray3D array = BinaryArray3D.create(10, 10, 10);
        
        for (int z = 0; z < 2; z++)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 2; x++)
                {
                    array.setBoolean(x    , y    , z   , true);
                    array.setBoolean(x + 4, y    , z   , true);
                    array.setBoolean(x + 8, y    , z   , true);
                    array.setBoolean(x    , y + 4, z   , true);
                    array.setBoolean(x + 4, y + 4, z   , true);
                    array.setBoolean(x + 8, y + 4, z   , true);
                    array.setBoolean(x    , y + 8, z   , true);
                    array.setBoolean(x + 4, y + 8, z   , true);
                    array.setBoolean(x + 8, y + 8, z   , true);
                    
                    array.setBoolean(x    , y    , z + 4, true);
                    array.setBoolean(x + 4, y    , z + 4, true);
                    array.setBoolean(x + 8, y    , z + 4, true);
                    array.setBoolean(x    , y + 4, z + 4, true);
                    array.setBoolean(x + 4, y + 4, z + 4, true);
                    array.setBoolean(x + 8, y + 4, z + 4, true);
                    array.setBoolean(x    , y + 8, z + 4, true);
                    array.setBoolean(x + 4, y + 8, z + 4, true);
                    array.setBoolean(x + 8, y + 8, z + 4, true);
                    
                    array.setBoolean(x    , y    , z + 8, true);
                    array.setBoolean(x + 4, y    , z + 8, true);
                    array.setBoolean(x + 8, y    , z + 8, true);
                    array.setBoolean(x    , y + 4, z + 8, true);
                    array.setBoolean(x + 4, y + 4, z + 8, true);
                    array.setBoolean(x + 8, y + 4, z + 8, true);
                    array.setBoolean(x    , y + 8, z + 8, true);
                    array.setBoolean(x + 4, y + 8, z + 8, true);
                    array.setBoolean(x + 8, y + 8, z + 8, true);
                }
            }
        }
        
        return array;
    }

}
