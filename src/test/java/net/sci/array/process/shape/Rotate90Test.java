/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import net.sci.array.Array;
import net.sci.array.Array2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Rotate90Test
{

    /**
     * Test method for {@link net.sci.array.process.shape.Rotate90#process2d(net.sci.array.Array2D)}.
     */
    @Test
    public final void testProcess2d()
    {
        Array2D<String> array = createStringArray2D();
        Rotate90 rot = new Rotate90();
        
        Array2D<?> result = rot.process2d(array);
        
        assertEquals(4, result.getSize(0));
        assertEquals(5, result.getSize(1));
        
        assertEquals(array.get(3, 1), result.get(1, 1));
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Rotate90#createView(net.sci.array.Array)}.
     */
    @Test
    public final void testCreateView()
    {
        Array2D<String> array = createStringArray2D();
        
        Rotate90 rot = new Rotate90();
        
        Array<?> view = rot.createView(array);
        view.get(new int[]{3, 4});
        
        assertEquals(4, view.getSize(0));
        assertEquals(5, view.getSize(1));
        
        assertEquals(array.get(3, 1), view.get(new int[]{1, 1}));
    }

    private Array2D<String> createStringArray2D()
    {
        String[] digits = new String[]{"a", "b", "c", "d", "e"};
        Array2D<String> array = Array2D.create(5, 4, "");
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                String str = digits[y] + digits[x];
                array.set(x, y, str);
            }
        }
        return array;
    }

}
