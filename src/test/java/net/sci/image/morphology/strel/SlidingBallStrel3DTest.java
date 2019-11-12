/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class SlidingBallStrel3DTest
{
    
    /**
     * Create a 3D Ball Strel with radius equal to 1.
     *  
     * Test method for {@link net.sci.image.morphology.strel.SlidingBallStrel3D#SlidingBallStrel3D(double)}.
     */
    @Test
    public final void testSlidingBallStrel3D_R1()
    {
        Strel3D strel = new SlidingBallStrel3D(1.0);
        assertEquals(strel.getShifts().length, 19);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.SlidingBallStrel3D#dilation(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testDilation()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 5, 5);
        array.setInt(2, 2, 2, 255);
        
        Strel3D strel = new SlidingBallStrel3D(1.0);
        
        ScalarArray3D<?> dilated = strel.dilation(array);
        
        assertEquals(dilated.getValue(1, 1, 1),   0, .01);
        assertEquals(dilated.getValue(2, 1, 1), 255, .01);
        assertEquals(dilated.getValue(3, 1, 1),   0, .01);
        assertEquals(dilated.getValue(1, 2, 1), 255, .01);
        assertEquals(dilated.getValue(2, 2, 1), 255, .01);
        assertEquals(dilated.getValue(3, 2, 1), 255, .01);
        assertEquals(dilated.getValue(1, 3, 1),   0, .01);
        assertEquals(dilated.getValue(2, 3, 1), 255, .01);
        assertEquals(dilated.getValue(3, 3, 1),   0, .01);
        
        assertEquals(dilated.getValue(1, 1, 2), 255, .01);
        assertEquals(dilated.getValue(2, 1, 2), 255, .01);
        assertEquals(dilated.getValue(3, 1, 2), 255, .01);
        assertEquals(dilated.getValue(1, 2, 2), 255, .01);
        assertEquals(dilated.getValue(2, 2, 2), 255, .01);
        assertEquals(dilated.getValue(3, 2, 2), 255, .01);
        assertEquals(dilated.getValue(1, 3, 2), 255, .01);
        assertEquals(dilated.getValue(2, 3, 2), 255, .01);
        assertEquals(dilated.getValue(3, 3, 2), 255, .01);
        
        assertEquals(dilated.getValue(1, 1, 3),   0, .01);
        assertEquals(dilated.getValue(2, 1, 3), 255, .01);
        assertEquals(dilated.getValue(3, 1, 3),   0, .01);
        assertEquals(dilated.getValue(1, 2, 3), 255, .01);
        assertEquals(dilated.getValue(2, 2, 3), 255, .01);
        assertEquals(dilated.getValue(3, 2, 3), 255, .01);
        assertEquals(dilated.getValue(1, 3, 3),   0, .01);
        assertEquals(dilated.getValue(2, 3, 3), 255, .01);
        assertEquals(dilated.getValue(3, 3, 3),   0, .01);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.SlidingBallStrel3D#erosion(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testErosion()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 5, 5);
        array.fillValue(255);
        array.setInt(2, 2, 2, 0);
        
        Strel3D strel = new SlidingBallStrel3D(1.0);
        
        ScalarArray3D<?> eroded = strel.erosion(array);
        
        assertEquals(eroded.getValue(1, 1, 1), 255, .01);
        assertEquals(eroded.getValue(2, 1, 1),   0, .01);
        assertEquals(eroded.getValue(3, 1, 1), 255, .01);
        assertEquals(eroded.getValue(1, 2, 1),   0, .01);
        assertEquals(eroded.getValue(2, 2, 1),   0, .01);
        assertEquals(eroded.getValue(3, 2, 1),   0, .01);
        assertEquals(eroded.getValue(1, 3, 1), 255, .01);
        assertEquals(eroded.getValue(2, 3, 1),   0, .01);
        assertEquals(eroded.getValue(3, 3, 1), 255, .01);
        
        assertEquals(eroded.getValue(1, 1, 2),   0, .01);
        assertEquals(eroded.getValue(2, 1, 2),   0, .01);
        assertEquals(eroded.getValue(3, 1, 2),   0, .01);
        assertEquals(eroded.getValue(1, 2, 2),   0, .01);
        assertEquals(eroded.getValue(2, 2, 2),   0, .01);
        assertEquals(eroded.getValue(3, 2, 2),   0, .01);
        assertEquals(eroded.getValue(1, 3, 2),   0, .01);
        assertEquals(eroded.getValue(2, 3, 2),   0, .01);
        assertEquals(eroded.getValue(3, 3, 2),   0, .01);
        
        assertEquals(eroded.getValue(1, 1, 3), 255, .01);
        assertEquals(eroded.getValue(2, 1, 3),   0, .01);
        assertEquals(eroded.getValue(3, 1, 3), 255, .01);
        assertEquals(eroded.getValue(1, 2, 3),   0, .01);
        assertEquals(eroded.getValue(2, 2, 3),   0, .01);
        assertEquals(eroded.getValue(3, 2, 3),   0, .01);
        assertEquals(eroded.getValue(1, 3, 3), 255, .01);
        assertEquals(eroded.getValue(2, 3, 3),   0, .01);
        assertEquals(eroded.getValue(3, 3, 3), 255, .01);
    }
    
}
