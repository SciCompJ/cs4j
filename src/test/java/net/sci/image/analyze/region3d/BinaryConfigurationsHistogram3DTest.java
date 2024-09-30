/**
 * 
 */
package net.sci.image.analyze.region3d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.label.LabelImages;

/**
 * @author dlegland
 *
 */
public class BinaryConfigurationsHistogram3DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region2d.BinaryConfigurationsHistogram2D#process(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinaryArray3D_cube2x2x2()
    {
        // create a 7-by-7-by-7 array containing a 4-by-4-by-4 cube in the middle
        BinaryArray3D array = BinaryArray3D.create(6, 6, 6);
        for (int z = 1; z < 5; z++)
        {
            for (int y = 1; y < 5; y++)
            {
                for (int x = 1; x < 5; x++)
                {
                    array.setBoolean(x, y, z, true);
                }
            }
        }
        
        int[] histo = new BinaryConfigurationsHistogram3D().process(array);
        
        // check size of histogram
        assertEquals(256, histo.length);
        
        // check all configurations have been counted
        int sum = 0;
        for (int i = 0; i < 256; i++)
        {
            sum += histo[i];
        }
        assertEquals(7*7*7, sum);
        
        assertEquals(2*7*7+5*(7+7+2*5), histo[0]); // all 0
        int[] cornerIndices = new int[] {1, 2, 4, 8, 16, 32, 64, 128};
        for (int ind : cornerIndices) assertEquals(1, histo[ind]);
        int[] edgeIndices = new int[] {3, 5, 10, 12, 17, 34, 48, 68, 80, 136, 160, 192};
        for (int ind : edgeIndices) assertEquals(3, histo[ind]);
        int[] faceIndices = new int[] {15, 51, 85, 170, 204, 240};
        for (int ind : faceIndices) assertEquals(9, histo[ind]);
        int[] cubeIndices = new int[] {255};
        for (int ind : cubeIndices) assertEquals(27, histo[ind]);
    }

    /**
     * Test method for {@link net.sci.image.analyze.region3d.BinaryConfigurationsHistogram3D#process(net.sci.array.numeric.IntArray3D, int[])}.
     */
    @Test
    public final void test_process_IntArray3D_allLabels()
    {
        IntArray3D<?> array = createEightRegionsLabelMap();
        int[] labels = LabelImages.findAllLabels(array);
        
        int[][] histo = new BinaryConfigurationsHistogram3D().process(array, labels);
        
        // check size of histograms
        assertEquals(histo.length, labels.length);
        assertEquals(histo[0].length, 256);
    }

    /**
     * Test method for {@link net.sci.image.analyze.region3d.BinaryConfigurationsHistogram3D#process(net.sci.array.numeric.IntArray3D, int[])}.
     */
    @Test
    public final void test_process_IntArray3D_selectedLabels()
    {
        IntArray3D<?> array = createEightRegionsLabelMap();
        int[] labels = new int[] {5, 8, 9};
        
        int[][] histo = new BinaryConfigurationsHistogram3D().process(array, labels);
        
        // check size of histograms
        assertEquals(histo.length, labels.length);
        assertEquals(histo[0].length, 256);
    }
    

    /**
     * Creates a label map containing eight regions, with labels
     * 3, 5, 7, 9, 11, 13, 15, 17.
     * 
     * @return a label map containing eight regions.
     */
    private static final UInt8Array3D createEightRegionsLabelMap()
    {
        UInt8Array3D array = UInt8Array3D.create(8, 8, 8);
        
        // single voxel region
        array.setInt(1, 1, 1, 3);
        
        // lines of four voxels
        for (int i = 3; i < 7; i++)
        {
            array.setInt(i, 1, 1,  5);
            array.setInt(1, i, 1,  7);
            array.setInt(1, 1, i, 11);
        }
        
        // 4x4 voxels planes
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                array.setInt(i, j, 1,  9);
                array.setInt(i, 1, j, 13);
                array.setInt(1, i, j, 15);
            }
        }        
        // 4x4x4 cubic region
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                for (int k = 3; k < 7; k++)
                {
                    array.setInt(i, j, k, 17);
                }
            }
        }        
        return array;
    }

}
