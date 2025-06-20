/**
 * 
 */
package net.sci.image.segmentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;

/**
 * 
 */
public class IsodataThresholdTest
{

    /**
     * Test method for {@link net.sci.image.segmentation.IsodataThreshold#computeThresholdValue(net.sci.array.numeric.ScalarArray)}.
     * @throws IOException 
     */
    @Test
    public final void testComputeThresholdValueScalarArrayOfQ() throws IOException
    {
        String fileName = getClass().getResource("/images/grains.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        UInt8Array2D array = (UInt8Array2D) image.getData();
        
        IsodataThreshold algo = new IsodataThreshold();
        
        double value = algo.computeThresholdValue(array);
//        System.out.printf("Threshold value: %6.2f%n", value);
        
        assertTrue(value > 120);
        assertTrue(value < 170);
    }

    /**
     * Test method for {@link net.sci.image.segmentation.AutoThreshold#processScalar(net.sci.array.numeric.ScalarArray)}.
     * @throws IOException 
     */
    @Test
    public final void testProcessScalar() throws IOException
    {
        String fileName = getClass().getResource("/images/grains.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        Array<?> array = image.getData();
        
        IsodataThreshold algo = new IsodataThreshold();
        
        BinaryArray result = algo.process(array);
        
        assertEquals(2, result.dimensionality());
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
    }

}
