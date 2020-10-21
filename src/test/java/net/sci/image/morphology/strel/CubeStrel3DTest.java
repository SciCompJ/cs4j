/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class CubeStrel3DTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.AbstractSeparableStrel3D#dilation(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testDilation()
    {
        UInt8Array3D array = createSingleVoxelArray();
        Strel3D se = CubeStrel3D.fromRadius(3);
        
        ScalarArray3D<?> result = se.dilation(array);

        assertTrue(result instanceof UInt8Array3D);
        assertEquals(result.size(0), array.size(0));
        assertEquals(result.size(1), array.size(1));
        assertEquals(result.size(2), array.size(2));
        
        assertEquals(result.getValue(7, 7, 7), 255, .01);
        assertEquals(result.getValue(13, 13, 13), 255, .01);
        assertEquals(result.getValue(6, 6, 6), 0, .01);
        assertEquals(result.getValue(14, 14, 14), 0, .01);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.AbstractSeparableStrel3D#erosion(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testErosion()
    {
        UInt8Array3D array = complement(createSingleVoxelArray());
        Strel3D se = CubeStrel3D.fromRadius(3);
        
        ScalarArray3D<?> result = se.erosion(array);

        assertTrue(result instanceof UInt8Array3D);
        assertEquals(result.size(0), array.size(0));
        assertEquals(result.size(1), array.size(1));
        assertEquals(result.size(2), array.size(2));
        
        assertEquals(result.getValue(7, 7, 7), 0, .01);
        assertEquals(result.getValue(13, 13, 13), 0, .01);
        assertEquals(result.getValue(6, 6, 6), 255, .01);
        assertEquals(result.getValue(14, 14, 14), 255, .01);
    }
    
    private final static UInt8Array3D createSingleVoxelArray()
    {
        UInt8Array3D array = UInt8Array3D.create(21,  21,  21);
        array.setValue(10, 10, 10, 255);
        return array;
    }
    
    private final static UInt8Array3D complement(UInt8Array3D array)
    {
        UInt8Array3D result = array.duplicate();
        for (int[] pos : result.positions())
        {
            result.setInt(pos, 255 - array.getInt(pos));
        }
        return result;
    }
}
