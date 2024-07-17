/**
 * 
 */
package net.sci.image.morphology.filtering;

import net.sci.array.color.RGB8Array2D;

/**
 * @author dlegland
 *
 */
public class MorphologicalFilterTestData
{
    /**
     * Creates a 14-by-14 array of RGB8 containing one 6-by-6 square within each
     * channel.
     */
    public static RGB8Array2D create_ThreeSquares_RGB8Array()
    {
        RGB8Array2D array = RGB8Array2D.create(14, 14);
        
        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 6; x++)
            {
                array.setValue(x + 2, y + 2, 0, 255);
                array.setValue(x + 6, y + 4, 1, 255);
                array.setValue(x + 4, y + 6, 2, 255);
            }
        }

        return array;
    }
  
}
