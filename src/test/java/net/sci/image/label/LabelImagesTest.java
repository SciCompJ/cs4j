package net.sci.image.label;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.UInt8Array2D;

public class LabelImagesTest
{
    @Test
    public void testFindAllLabels()
    {
        UInt8Array2D array = UInt8Array2D.create(10,  10);
        for (int dy = 0; dy < 2; dy++)
        {
            for (int dx = 0; dx < 2; dx++)
            {
                array.setInt(dx + 1, dy + 1, 1);
                array.setInt(dx + 4, dy + 1, 2);
                array.setInt(dx + 7, dy + 1, 3);
                array.setInt(dx + 1, dy + 4, 4);
                array.setInt(dx + 4, dy + 4, 5);
                array.setInt(dx + 7, dy + 4, 6);
                array.setInt(dx + 1, dy + 7, 7);
                array.setInt(dx + 4, dy + 7, 8);
                array.setInt(dx + 7, dy + 7, 9);
            }
        }
        
        int[] labels = LabelImages.findAllLabels(array);
        assertEquals(9, labels.length);
        assertEquals(1, labels[0]);
        assertEquals(9, labels[8]);
    }

    /**
     * Test method for {@link net.sci.image.label.LabelImages#cropLabel(net.sci.array.numeric.IntArray, int, int)}.
     */
    @Test
    public final void testCropLabel()
    {
        UInt8Array2D array = createFourRegionsLabelMap();
        
        IntArray<?> res = LabelImages.cropLabel(array, 7, 0);
        
        assertTrue(res.elementClass().equals(array.elementClass()));
        assertEquals(res.size(0), 5);
        assertEquals(res.size(1), 5);
    }

    /**
     * Test method for {@link net.sci.image.label.LabelImages#keepLabels(net.sci.array.numeric.IntArray, int[])}.
     */
    @Test
    public final void test_keepLabels_intArray()
    {
        UInt8Array2D array = createFourRegionsLabelMap();
        
        IntArray<?> res = LabelImages.keepLabels(array, new int[] {4, 5});
        
        assertTrue(res.elementClass().equals(array.elementClass()));
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(res.getInt(new int[] {1, 1}), 0);
        assertEquals(res.getInt(new int[] {1, 5}), 4);
        assertEquals(res.getInt(new int[] {5, 1}), 5);
        assertEquals(res.getInt(new int[] {5, 5}), 0);
    }
    
    private static final UInt8Array2D createFourRegionsLabelMap()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 8);
        array.setInt(1, 1, 3);
        for (int i = 3; i < 8; i++)
        {
            array.setInt(1, i, 4);
            array.setInt(i, 1, 5);
        }
        for (int i = 3; i < 8; i++)
        {
            for (int j = 3; j < 8; j++)
            {
                array.setInt(i, j, 7);
            }
        }
        return array;
    }
}
