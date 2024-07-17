/**
 * 
 */
package net.sci.image.morphology.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
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
    
}
