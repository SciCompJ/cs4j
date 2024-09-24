/**
 * 
 */
package net.sci.image.binary.labeling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray1D;
import net.sci.array.numeric.IntArray1D;
import net.sci.image.Image;
import net.sci.image.ImageType;

/**
 * 
 */
public class FloodFillComponentsLabeling1DTest
{

    /**
     * Test method for {@link net.sci.image.binary.labeling.FloodFillComponentsLabeling1D#processBinary1d(net.sci.array.binary.BinaryArray1D)}.
     */
    @Test
    public final void testProcessBinary1d()
    {
        BinaryArray1D array = createTestArray();
        
        FloodFillComponentsLabeling1D op = new FloodFillComponentsLabeling1D();
        IntArray1D<?> res= op.processBinary1d(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.getInt(1), 1);
        assertEquals(res.getInt(2), 0);
        assertEquals(res.getInt(3), 2);
        assertEquals(res.getInt(5), 0);
        assertEquals(res.getInt(8), 3);
    }

    /**
     * Test method for {@link net.sci.image.binary.labeling.FloodFillComponentsLabeling1D#process(net.sci.image.Image)}.
     */
    @Test
    public final void testProcessImage()
    {
        BinaryArray1D array = createTestArray();
        Image image = new Image(array);
        
        FloodFillComponentsLabeling1D op = new FloodFillComponentsLabeling1D();
        Image resImage = op.process(image);
        
        assertEquals(resImage.getType(), ImageType.LABEL);
        assertEquals(resImage.getDisplaySettings().getDisplayRange()[0], 0.0, 0.01);
        assertEquals(resImage.getDisplaySettings().getDisplayRange()[1], 3.0, 0.01);
    }

    private static final BinaryArray1D createTestArray()
    {
        BinaryArray1D array = BinaryArray1D.create(10);
        array.setBoolean(1, true);
        array.setBoolean(3, true);
        array.setBoolean(4, true);
        array.setBoolean(6, true);
        array.setBoolean(7, true);
        array.setBoolean(8, true);
        
        return array;
    }
}
