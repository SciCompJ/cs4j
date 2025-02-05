/**
 * 
 */
package net.sci.image.morphology.filtering;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.image.morphology.strel.SquareStrel;
import net.sci.image.morphology.strel.Strel2D;

/**
 * 
 */
public class ClosingTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.filtering.Closing#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_ArrayOfString_Square3x3()
    {
        String[] strings = new String[] {
                "Berlin", "London", "Madrid", "Paris", "Rome", "Tokyo"
        };
        int[][] inds = new int[][] {
            {5, 5, 3, 4, 6, 2}, 
            {4, 2, 4, 3, 6, 5},
            {1, 3, 5, 2, 2, 3}, 
            {4, 1, 5, 3, 3, 4}, 
        };
        
        Array2D<String> array = Array2D.create(inds[0].length, inds.length, "");
        array.fill((x,y) -> strings[inds[y][x] - 1]);
//        array.printContent(System.out, " %7s");
        
        Strel2D strel = new SquareStrel(3);
        
        @SuppressWarnings("unchecked")
        Array<String> res = (Array<String>) new Closing(strel).process(array);
//        Array2D.wrap(res).printContent(System.out, " %7s");
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        Array2D<String> res2d = (Array2D<String>) Array2D.wrap(res);
        assertEquals("Paris", res2d.get(1, 3));
        assertEquals("Tokyo", res2d.get(4, 1));
        assertEquals("Paris", res2d.get(4, 3));
    }
    
}
