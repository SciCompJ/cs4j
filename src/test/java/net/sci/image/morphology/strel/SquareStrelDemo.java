/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * Simple demo file for using square strel.
 * 
 * @author dlegland
 *
 */
public class SquareStrelDemo
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // Creates a 5x5 square structuring element
        Strel2D strel = Strel2D.Shape.SQUARE.fromDiameter(5);
        
        // Creates a simple array with white dot in the middle
        UInt8Array2D array = UInt8Array2D.create(9, 9);
        array.setValue(255, 4, 4);
        
        // applies dilation on array
        ScalarArray2D<?> dilated = strel.dilation(array);
        
        // display result
        dilated.printContent(System.out);
    }
    
}
