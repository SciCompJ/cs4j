/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.*;
import net.sci.array.vector.Float32VectorArray2D;
import net.sci.array.vector.VectorArray2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class VectorArrayRGB8ViewTest
{

    @Test
    public final void test()
    {
        VectorArray2D<?,?> array = createTestArray();
        RGB8Array view = new VectorArrayRGB8View(array, 3, 0, 255, 6, 0, 255, 9, 0, 255);
        
        assertEquals(2, view.dimensionality());
        assertEquals(20, view.size(0));
        assertEquals(15, view.size(1));

        RGB8 rgb = view.get(new int[]{10, 5});
        int[] samples = rgb.getSamples();
        assertEquals(60, samples[0]);
        assertEquals(120, samples[1]);
        assertEquals(180, samples[2]);
    }
    
    private VectorArray2D<?,?> createTestArray()
    {
        VectorArray2D<?,?> array = Float32VectorArray2D.create(20, 15, 10);
        
        for (int y = 0; y < 15; y++)
        {
            for (int x = 0; x < 20; x++)
            {
                for (int c = 0; c < 10; c++)
                array.setValue(x, y, c, c * 20);
            }
        }
        
        return array;
    }

}
