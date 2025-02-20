/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

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
        array.setInt(7, 7, 255);
        
        // applies dilation on array
        ScalarArray2D<?> dilated = strel.dilation(array);
        
        // display result
        System.out.println("Dilation of UInt8Array2D:");
        dilated.printContent(System.out);
        
        
        // Creates a simple array with white dot in the middle
        Float32Array2D arrayF32 = Float32Array2D.create(15, 15);
        arrayF32.setValue(7, 7, 0.5);
        
        // applies dilation on array
        ScalarArray2D<?> dilatedF32 = strel.dilation(arrayF32);
        
        // display result
        System.out.println("Dilation of Float32Array2D:");
        dilatedF32.printContent(System.out, "%5.2f");
    }
    
}
