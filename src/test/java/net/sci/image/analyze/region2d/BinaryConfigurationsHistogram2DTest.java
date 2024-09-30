/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.label.LabelImages;

/**
 * @author dlegland
 *
 */
public class BinaryConfigurationsHistogram2DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region2d.BinaryConfigurationsHistogram2D#process(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinaryArray2D_square2x2()
    {
        // create a 4-by-4 array containing a 2-by-2 square in the middle
        BinaryArray2D array = BinaryArray2D.create(4, 4);
        array.setBoolean(1, 1, true);
        array.setBoolean(2, 1, true);
        array.setBoolean(1, 2, true);
        array.setBoolean(2, 2, true);
        
        int[] histo = new BinaryConfigurationsHistogram2D().process(array);
        
        // check size of histogram
        assertEquals(16, histo.length);
        
        // check all configurations have been counted
        int sum = 0;
        for (int i = 0; i < 16; i++)
        {
            sum += histo[i];
        }
        assertEquals(5*5, sum);
        
        assertEquals(16, histo[0]); // all 0
        assertEquals(1, histo[1]); // one corner
        assertEquals(1, histo[2]); // one corner
        assertEquals(1, histo[4]); // one corner
        assertEquals(1, histo[8]); // one corner
        assertEquals(1, histo[3]); // one side
        assertEquals(1, histo[5]); // one side
        assertEquals(1, histo[10]); // one side
        assertEquals(1, histo[12]); // one side
        assertEquals(1, histo[15]); // four pixels
    }

    /**
     * Test method for {@link net.sci.image.analyze.region2d.BinaryConfigurationsHistogram2D#process(net.sci.array.numeric.IntArray2D, int[])}.
     */
    @Test
    public final void test_process_IntArray2D_allLabels()
    {
        IntArray2D<?> array = createFourRegionsLabelMap();
        int[] labels = LabelImages.findAllLabels(array);
        
        int[][] histo = new BinaryConfigurationsHistogram2D().process(array, labels);
        
        // check size of histograms
        assertEquals(histo.length, labels.length);
        assertEquals(histo[0].length, 16);
    }

    /**
     * Test method for {@link net.sci.image.analyze.region2d.BinaryConfigurationsHistogram2D#process(net.sci.array.numeric.IntArray2D, int[])}.
     */
    @Test
    public final void test_process_IntArray2D_selectedLabels()
    {
        IntArray2D<?> array = createFourRegionsLabelMap();
        int[] labels = new int[] {5, 8, 9};
        
        int[][] histo = new BinaryConfigurationsHistogram2D().process(array, labels);
        
        // check size of histograms
        assertEquals(histo.length, labels.length);
        assertEquals(histo[0].length, 16);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.BinaryConfigurationsHistogram2D#processInnerFrame(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessInnerFrame()
    {
        int[][] data = new int[][] {
            {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0}, 
            {0, 1, 0, 0,  1, 1, 1, 1,  0, 0, 0, 0,  0, 0, 0, 1}, 
            {0, 1, 1, 0,  0, 1, 1, 1,  0, 0, 0, 0,  1, 1, 0, 0}, 
            {0, 1, 1, 1,  0, 1, 1, 1,  0, 1, 1, 0,  0, 0, 1, 0}, 
            {0, 0, 0, 0,  0, 1, 1, 1,  0, 0, 0, 1,  0, 0, 1, 0}, 
            {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 1, 0}, 
            {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 1, 1, 0}, 
            {0, 0, 0, 0,  0, 0, 1, 1,  1, 1, 0, 0,  1, 0, 1, 0}, 
            {0, 0, 0, 0,  0, 0, 1, 0,  1, 0, 0, 1,  1, 0, 0, 0}, 
            {0, 0, 0, 0,  1, 0, 1, 0,  1, 0, 0, 1,  1, 0, 1, 0}, 
            {0, 1, 0, 0,  1, 0, 1, 1,  1, 0, 1, 0,  1, 0, 1, 0}, 
            {0, 1, 0, 1,  1, 0, 0, 0,  0, 0, 1, 0,  0, 0, 1, 0}, 
            {0, 1, 1, 1,  0, 0, 0, 0,  0, 0, 1, 1,  1, 1, 1, 0}, 
            {0, 0, 1, 1,  0, 0, 0, 0,  0, 1, 1, 1,  1, 1, 0, 0}, 
            {0, 0, 0, 0,  0, 0, 0, 0,  0, 1, 0, 0,  0, 0, 0, 0}, 
            {0, 0, 0, 0,  0, 1, 1, 0,  0, 0, 0, 0,  0, 0, 0, 0}, 
        };
        int sizeX = 16;
        int sizeY = 16;
        BinaryArray2D array = BinaryArray2D.create(sizeX, sizeY);
        array.fillBooleans((x,y) -> data[y][x] > 0);
        
        int[] histo = new BinaryConfigurationsHistogram2D().processInnerFrame(array);
        
        // check size of histogram
        assertEquals(16, histo.length);
        
        // check all configurations have been counted
        int sum = 0;
        for (int i = 0; i < 16; i++)
        {
            sum += histo[i];
        }
        assertEquals(15*15, sum);
        
        // Compare with pre-computed values 
        // (adapted from Ohser and Muecklich, p. 131. Pixel positions 1 and 2 are switched)
        int[] exp = new int[] {70, 12, 13, 12,  13, 21, 2, 5,  16, 2, 19, 5,  11, 5, 7, 12};
        for (int i = 0; i < 16; i++)
        {
            assertEquals(exp[i], histo[i]);
        }
    }


    /**
     * Creates a label map containing four regions.
     * 
     * @return a label map containing four regions.
     */
    private static final UInt8Array2D createFourRegionsLabelMap()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 8);
        array.setInt(1, 1, 3);
        for (int i = 3; i < 7; i++)
        {
            array.setInt(i, 1, 5);
            array.setInt(1, i, 8);
        }
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                array.setInt(1, i, 9);
            }
        }
        return array;
    }
}
