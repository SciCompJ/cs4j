/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.image.morphology.strel.LinearZStrel3D;

/**
 * @author dlegland
 *
 */
public class LinearZStrel3DTest
{
    /**
     * Test method for {@link net.sci.image.morphology.strel.LinearZStrel3D#dilation(net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public void testDilation() 
    {
        UInt8Array3D image = createIsolatedVoxelImage();
        
        LinearZStrel3D strel = LinearZStrel3D.fromDiameter(7);
        ScalarArray3D<?> result = strel.dilation(image);
        
        assertEquals(255, result.getValue(5, 5, 5), .01);
        assertEquals(255, result.getValue(5, 5, 2), .01);
        assertEquals(255, result.getValue(5, 5, 8), .01);
        assertEquals(  0, result.getValue(5, 5, 1), .01);
        assertEquals(  0, result.getValue(5, 5, 9), .01);
    }

    private static final UInt8Array3D createIsolatedVoxelImage()
    {
        UInt8Array3D image = UInt8Array3D.create(10, 10, 10);
        image.setValue(255, 5, 5, 5);
        return image;
    }
    
}
