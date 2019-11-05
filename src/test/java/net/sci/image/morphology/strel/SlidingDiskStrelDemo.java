/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array2D;

/**
 * Simple demo file for using square strel.
 * 
 * @author dlegland
 *
 */
public class SlidingDiskStrelDemo
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // Creates a disk structuring element with radius 6
        Strel2D strel = new SlidingDiskStrel(6);
        
        // Creates a simple array with white dot in the middle
        UInt8Array2D array = UInt8Array2D.create(15, 15);
        array.setValue(7, 7, 255);
        
        // applies dilation on array
        ScalarArray2D<?> dilated = strel.dilation(array);
        
        // display result
        dilated.print(System.out);
    }
    
}
