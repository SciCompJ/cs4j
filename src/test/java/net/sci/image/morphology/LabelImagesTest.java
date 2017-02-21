package net.sci.image.morphology;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.scalar2d.UInt8Array2D;

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
    
}
