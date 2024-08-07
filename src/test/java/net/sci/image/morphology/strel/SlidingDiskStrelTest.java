/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class SlidingDiskStrelTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.SlidingDiskStrel#dilation(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testDilation_UInt8()
    {
        // Creates a disk structuring element with radius 6
        Strel2D strel = new SlidingDiskStrel(1);
        
        // Creates a simple array with white dot in the middle
        UInt8Array2D array = UInt8Array2D.create(5, 5);
        array.setValue(2, 2, 255);
        
        // applies dilation on array
        ScalarArray2D<?> dilated = strel.dilation(array);

        assertEquals(dilated.getValue(4, 1),   0, 0.1);
        assertEquals(dilated.getValue(1, 1), 255, 0.1);
        assertEquals(dilated.getValue(3, 3), 255, 0.1);
        assertEquals(dilated.getValue(4, 3),   0, 0.1);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.SlidingDiskStrel#dilation(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testDilation_Float32()
    {
        // Creates a disk structuring element with radius 6
        Strel2D strel = new SlidingDiskStrel(1);
        
        // Creates a simple array with white dot in the middle
        Float32Array2D array = Float32Array2D.create(5, 5);
        array.setValue(2, 2, 1000.0);
        
        // applies dilation on array
        ScalarArray2D<?> dilated = strel.dilation(array);

        assertEquals(dilated.getValue(4, 1),    0.0, 0.1);
        assertEquals(dilated.getValue(1, 1), 1000.0, 0.1);
        assertEquals(dilated.getValue(3, 3), 1000.0, 0.1);
        assertEquals(dilated.getValue(4, 3),    0.0, 0.1);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.SlidingDiskStrel#erosion(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testErosion_UInt8()
    {
        // Creates a disk structuring element with radius 6
        Strel2D strel = new SlidingDiskStrel(1);
        
        // Creates a simple array with white dot in the middle
        UInt8Array2D array = UInt8Array2D.create(5, 5);
        array.fillValue(255.0);
        array.setValue(2, 2, 0.0);
        
        // applies dilation on array
        ScalarArray2D<?> eroded = strel.erosion(array);

        assertEquals(eroded.getValue(4, 1), 255, 0.1);
        assertEquals(eroded.getValue(1, 1),   0, 0.1);
        assertEquals(eroded.getValue(3, 3),   0, 0.1);
        assertEquals(eroded.getValue(4, 3), 255, 0.1);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.SlidingDiskStrel#erosion(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testErosion_Float32()
    {
        // Creates a disk structuring element with radius 6
        Strel2D strel = new SlidingDiskStrel(1);
        
        // Creates a simple array with white dot in the middle
        Float32Array2D array = Float32Array2D.create(5, 5);
        array.fillValue(1000.0);
        array.setValue(2, 2, 0.0);
        
        // applies dilation on array
        ScalarArray2D<?> eroded = strel.erosion(array);

        assertEquals(eroded.getValue(4, 1), 1000.0, 0.1);
        assertEquals(eroded.getValue(1, 1),    0.0, 0.1);
        assertEquals(eroded.getValue(3, 3),    0.0, 0.1);
        assertEquals(eroded.getValue(4, 3), 1000.0, 0.1);
    }
}
