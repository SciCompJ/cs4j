/**
 * 
 */
package net.sci.image.morphology.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.color.RGB8Array2D;
import net.sci.image.morphology.strel.SquareStrel;
import net.sci.image.morphology.strel.Strel2D;

/**
 * @author dlegland
 *
 */
public class DilationTest
{
    @Test
    public void testDilation_RGB8Array2D_Square3x3()
    {
        RGB8Array2D array = MorphologicalFilterTestData.create_ThreeSquares_RGB8Array();
        Strel2D strel = new SquareStrel(3);
        
        Array<?> res = new Dilation(strel).process(array);
        
        assertTrue(res instanceof RGB8Array2D);
        assertEquals(2, res.dimensionality());
        assertEquals(14, res.size(0));
        assertEquals(14, res.size(1));
        
        RGB8Array2D res2d = (RGB8Array2D) res;
        
        // red
        assertEquals(255, res2d.getSample( 1,  1, 0));
        assertEquals(255, res2d.getSample( 8,  8, 0));
        
        // green
        assertEquals(255, res2d.getSample( 5,  3, 1));
        assertEquals(255, res2d.getSample(12, 10, 1));
        
        // blue
        assertEquals(255, res2d.getSample( 3, 10, 2));
        assertEquals(255, res2d.getSample( 5, 12, 2));
    }
    
    @Test
    public void testDilation_Array2DofString_Square3x3()
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
        Array<String> res = (Array<String>) new Dilation(strel).process(array);
//        Array2D.wrap(res).printContent(System.out, " %7s");
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        Array2D<String> res2d = (Array2D<String>) Array2D.wrap(res);
        assertEquals("Rome", res2d.get(1, 1));
        assertEquals("Tokyo", res2d.get(3, 2));
        assertEquals("Paris", res2d.get(4, 3));
    }
}
