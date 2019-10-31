/**
 * 
 */
package net.sci.image.morphology.filter;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.color.RGB8Array2D;
import net.sci.image.morphology.strel.SquareStrel;
import net.sci.image.morphology.strel.Strel2D;

/**
 * @author dlegland
 *
 */
public class ErosionTest
{
    @Test
    public void testDilation_RGB8Array2D_Square3x3()
    {
        RGB8Array2D array = MorphologicalFilterTestData.create_ThreeSquares_RGB8Array();
        Strel2D strel = new SquareStrel(3);
        
        Array<?> res = new Erosion(strel).process(array);
        
        assertTrue(res instanceof RGB8Array2D);
        assertEquals(2, res.dimensionality());
        assertEquals(14, res.size(0));
        assertEquals(14, res.size(1));
        
        RGB8Array2D res2d = (RGB8Array2D) res;
        
        // red
        assertEquals(  0, res2d.getSample( 2,  2, 0));
        assertEquals(255, res2d.getSample( 3,  3, 0));
        assertEquals(255, res2d.getSample( 6,  6, 0));
        assertEquals(  0, res2d.getSample( 7,  7, 0));
        
        // green
        assertEquals(  0, res2d.getSample( 6,  4, 1));
        assertEquals(255, res2d.getSample( 7,  5, 1));
        assertEquals(255, res2d.getSample(10,  8, 1));
        assertEquals(  0, res2d.getSample(11,  9, 1));
        
        // blue
        assertEquals(  0, res2d.getSample( 4,  6, 2));
        assertEquals(255, res2d.getSample( 5,  7, 2));
        assertEquals(255, res2d.getSample( 8, 10, 2));
        assertEquals(  0, res2d.getSample( 9, 11, 2));
    }
    
}
