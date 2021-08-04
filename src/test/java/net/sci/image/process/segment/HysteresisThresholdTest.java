/**
 * 
 */
package net.sci.image.process.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.scalar.BinaryArray2D;
import net.sci.array.scalar.BinaryArray3D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class HysteresisThresholdTest
{
    /**
     * Test method for {@link net.sci.image.process.segment.HysteresisThreshold#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_2D_merge()
    {
        UInt8Array2D array = createArray2d();
        
        HysteresisThreshold algo = new HysteresisThreshold(180, 80);
        BinaryArray2D result = BinaryArray2D.wrap(algo.processScalar(array));
        
        assertEquals(result.size(0), array.size(0));
        assertEquals(result.size(1), array.size(1));
        assertTrue(result.getBoolean(4, 4));
        assertTrue(result.getBoolean(8, 4));
        assertTrue(result.getBoolean(5, 4));
        assertFalse(result.getBoolean(0, 0));
        assertFalse(result.getBoolean(11, 7));
    }
    
    /**
     * Test method for {@link net.sci.image.process.segment.HysteresisThreshold#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_2D_split()
    {
        UInt8Array2D array = createArray2d();
        
        HysteresisThreshold algo = new HysteresisThreshold(140, 110);
        BinaryArray2D result = BinaryArray2D.wrap(algo.processScalar(array));
        
        assertEquals(result.size(0), array.size(0));
        assertEquals(result.size(1), array.size(1));
        assertTrue(result.getBoolean(4, 4));
        assertTrue(result.getBoolean(8, 4));
        assertFalse(result.getBoolean(5, 4));
        assertFalse(result.getBoolean(0, 0));
        assertFalse(result.getBoolean(11, 7));
    }
    
    /**
     * Test method for {@link net.sci.image.process.segment.HysteresisThreshold#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_3D_merge()
    {
        UInt8Array3D array = createArray3d();
        
        HysteresisThreshold algo = new HysteresisThreshold(180, 80);
        BinaryArray3D result = BinaryArray3D.wrap(algo.processScalar(array));
        
        assertEquals(result.size(0), array.size(0));
        assertEquals(result.size(1), array.size(1));
        assertEquals(result.size(2), array.size(2));
        assertTrue(result.getBoolean(4, 4, 4));
        assertTrue(result.getBoolean(8, 4, 4));
        assertTrue(result.getBoolean(5, 4, 4));
        assertFalse(result.getBoolean(0, 0, 0));
        assertFalse(result.getBoolean(11, 7, 7));
    }

    /**
     * Test method for {@link net.sci.image.process.segment.HysteresisThreshold#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_3D_split()
    {
        UInt8Array3D array = createArray3d();
        
        HysteresisThreshold algo = new HysteresisThreshold(140, 110);
        BinaryArray3D result = BinaryArray3D.wrap(algo.processScalar(array));
        
        assertEquals(result.size(0), array.size(0));
        assertEquals(result.size(1), array.size(1));
        assertEquals(result.size(2), array.size(2));
        assertTrue(result.getBoolean(4, 4, 4));
        assertTrue(result.getBoolean(8, 4, 4));
        assertFalse(result.getBoolean(5, 4, 4));
        assertFalse(result.getBoolean(0, 0, 0));
        assertFalse(result.getBoolean(11, 7, 7));
    }

    private UInt8Array2D createArray2d()
    {
        UInt8Array2D array = UInt8Array2D.create(12, 8);
        array.fillValue(50);
        for (int y = 1; y < 7; y++)
        {
            for (int x = 1; x < 11; x++)
            {
                array.setValue(x, y, 100);
            }
        }
        for (int y = 2; y < 6; y++)
        {
            for (int x = 2; x < 5; x++)
            {
                // fill a rectangle from (2,2) to (4,5)
                array.setValue(x, y, 200);
                // fill a rectangle from (6,2) to (9,6)
                array.setValue(x + 4, y, 150); 
            }
        }
        return array;
    }

    private UInt8Array3D createArray3d()
    {
        UInt8Array3D array = UInt8Array3D.create(12, 8, 8);
        array.fillValue(50);
        for (int z = 1; z < 7; z++)
        {
            for (int y = 1; y < 7; y++)
            {
                for (int x = 1; x < 11; x++)
                {
                    array.setValue(x, y, z, 100);
                }
            }
        }
        for (int z = 2; z < 6; z++)
        {
            for (int y = 2; y < 6; y++)
            {
                for (int x = 2; x < 5; x++)
                {
                    // fill a rectangle from (2,2,2) to (4,5,5)
                    array.setValue(x, y, z, 200);
                    // fill a rectangle from (6,2,2) to (9,5,5)
                    array.setValue(x + 4, y, z, 150); 
                }
            }
        }
        return array;
    }
}
