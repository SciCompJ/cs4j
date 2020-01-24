package net.sci.image.morphology;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;

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
                array.setInt(1, dx + 1, dy + 1);
                array.setInt(2, dx + 4, dy + 1);
                array.setInt(3, dx + 7, dy + 1);
                array.setInt(4, dx + 1, dy + 4);
                array.setInt(5, dx + 4, dy + 4);
                array.setInt(6, dx + 7, dy + 4);
                array.setInt(7, dx + 1, dy + 7);
                array.setInt(8, dx + 4, dy + 7);
                array.setInt(9, dx + 7, dy + 7);
            }
        }
        
        int[] labels = LabelImages.findAllLabels(array);
        assertEquals(9, labels.length);
        assertEquals(1, labels[0]);
        assertEquals(9, labels[8]);
    }
    
}
