/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;


/**
 * @author dlegland
 *
 */
public class KillBordersTest
{

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.KillBorders#processScalar2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcess_Blocks_2d()
    {
        UInt8Array2D array = createBlocksArray2d();
        
        KillBorders algo = new KillBorders();
        UInt8Array2D res = (UInt8Array2D) algo.processScalar2d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertEquals(res.getInt(0, 0), 0);
        assertEquals(res.getInt(4, 0), 0);
        assertEquals(res.getInt(8, 0), 0);
        assertEquals(res.getInt(0, 4), 0);
        assertEquals(res.getInt(4, 4), 200);
        assertEquals(res.getInt(8, 4), 0);
        assertEquals(res.getInt(0, 8), 0);
        assertEquals(res.getInt(4, 8), 0);
        assertEquals(res.getInt(8, 8), 0);
    }

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.KillBorders#processScalar3d(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcess_Blocks_3d()
    {
        UInt8Array3D array = createBlocksArray3d();
        
        KillBorders algo = new KillBorders();
        UInt8Array3D res = (UInt8Array3D) algo.processScalar3d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        assertEquals(res.getInt(0, 0, 0), 0);
        assertEquals(res.getInt(4, 0, 0), 0);
        assertEquals(res.getInt(8, 0, 0), 0);
        assertEquals(res.getInt(0, 4, 0), 0);
        assertEquals(res.getInt(4, 4, 0), 0);
        assertEquals(res.getInt(8, 4, 0), 0);
        assertEquals(res.getInt(0, 8, 0), 0);
        assertEquals(res.getInt(4, 8, 0), 0);
        assertEquals(res.getInt(8, 8, 0), 0);
        
        assertEquals(res.getInt(0, 0, 4), 0);
        assertEquals(res.getInt(4, 0, 4), 0);
        assertEquals(res.getInt(8, 0, 4), 0);
        assertEquals(res.getInt(0, 4, 4), 0);
        assertEquals(res.getInt(4, 4, 4), 200);
        assertEquals(res.getInt(8, 4, 4), 0);
        assertEquals(res.getInt(0, 8, 4), 0);
        assertEquals(res.getInt(4, 8, 4), 0);
        assertEquals(res.getInt(8, 8, 4), 0);
        
        assertEquals(res.getInt(0, 0, 8), 0);
        assertEquals(res.getInt(4, 0, 8), 0);
        assertEquals(res.getInt(8, 0, 8), 0);
        assertEquals(res.getInt(0, 4, 8), 0);
        assertEquals(res.getInt(4, 4, 8), 0);
        assertEquals(res.getInt(8, 4, 8), 0);
        assertEquals(res.getInt(0, 8, 8), 0);
        assertEquals(res.getInt(4, 8, 8), 0);
        assertEquals(res.getInt(8, 8, 8), 0);
    }

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.BinaryKillBorders#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess()
    {
        UInt8Array2D array = createBlocksArray2d();
        
        KillBorders algo = new KillBorders();
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(algo.process(array)));
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertEquals(res.getInt(0, 0), 0);
        assertEquals(res.getInt(4, 0), 0);
        assertEquals(res.getInt(8, 0), 0);
        assertEquals(res.getInt(0, 4), 0);
        assertEquals(res.getInt(4, 4), 200);
        assertEquals(res.getInt(8, 4), 0);
        assertEquals(res.getInt(0, 8), 0);
        assertEquals(res.getInt(4, 8), 0);
        assertEquals(res.getInt(8, 8), 0);
    }
    
    private static final UInt8Array2D createBlocksArray2d()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 10);
        
        for (int y = 0; y < 2; y++)
        {
            for (int x = 0; x < 2; x++)
            {
                array.setInt(x    , y    , 200);
                array.setInt(x + 4, y    , 200);
                array.setInt(x + 8, y    , 200);
                array.setInt(x    , y + 4, 200);
                array.setInt(x + 4, y + 4, 200);
                array.setInt(x + 8, y + 4, 200);
                array.setInt(x    , y + 8, 200);
                array.setInt(x + 4, y + 8, 200);
                array.setInt(x + 8, y + 8, 200);
            }
        }
        
        return array;
    }

    private static final UInt8Array3D createBlocksArray3d()
    {
        UInt8Array3D array = UInt8Array3D.create(10, 10, 10);
        
        for (int z = 0; z < 2; z++)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 2; x++)
                {
                    array.setInt(x    , y    , z   , 200);
                    array.setInt(x + 4, y    , z   , 200);
                    array.setInt(x + 8, y    , z   , 200);
                    array.setInt(x    , y + 4, z   , 200);
                    array.setInt(x + 4, y + 4, z   , 200);
                    array.setInt(x + 8, y + 4, z   , 200);
                    array.setInt(x    , y + 8, z   , 200);
                    array.setInt(x + 4, y + 8, z   , 200);
                    array.setInt(x + 8, y + 8, z   , 200);
                    
                    array.setInt(x    , y    , z + 4, 200);
                    array.setInt(x + 4, y    , z + 4, 200);
                    array.setInt(x + 8, y    , z + 4, 200);
                    array.setInt(x    , y + 4, z + 4, 200);
                    array.setInt(x + 4, y + 4, z + 4, 200);
                    array.setInt(x + 8, y + 4, z + 4, 200);
                    array.setInt(x    , y + 8, z + 4, 200);
                    array.setInt(x + 4, y + 8, z + 4, 200);
                    array.setInt(x + 8, y + 8, z + 4, 200);
                    
                    array.setInt(x    , y    , z + 8, 200);
                    array.setInt(x + 4, y    , z + 8, 200);
                    array.setInt(x + 8, y    , z + 8, 200);
                    array.setInt(x    , y + 4, z + 8, 200);
                    array.setInt(x + 4, y + 4, z + 8, 200);
                    array.setInt(x + 8, y + 4, z + 8, 200);
                    array.setInt(x    , y + 8, z + 8, 200);
                    array.setInt(x + 4, y + 8, z + 8, 200);
                    array.setInt(x + 8, y + 8, z + 8, 200);
                }
            }
        }
        
        return array;
    }

}
