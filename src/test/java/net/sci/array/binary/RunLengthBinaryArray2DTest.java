/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.image.morphology.strel.Cross3x3Strel;
import net.sci.image.morphology.strel.Strel2D;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryArray2DTest
{
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#convert(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testConvert()
    {
        BinaryArray2D array = new BufferedBinaryArray2D(12, 12);
        fillRect(array, 2, 10, 2, 10, true);
        fillRect(array, 4, 8, 4, 8, false);
        array.setBoolean(6, 6, true);
        
        RunLengthBinaryArray2D converted = RunLengthBinaryArray2D.convert(array);
        
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(array.getBoolean(x, y) == converted.getBoolean(x, y));  
            }
        }
    }

    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testDilation()
    {
        BufferedBinaryArray2D array = new BufferedBinaryArray2D(12, 12);
        // an isolated pixel
        array.setBoolean(2, 2, true);
        
        // a ring-like structure
        fillRect(array, 6, 9, 2, 5, true);
        fillRect(array, 7, 8, 3, 4, false);
        
        // diagonal pixels
        array.setBoolean(2, 6, true);
        array.setBoolean(3, 7, true);
        array.setBoolean(4, 8, true);
        array.setBoolean(5, 9, true);
        
        // create a cross-shaped structuring element
        BufferedBinaryArray2D strel = new BufferedBinaryArray2D(3, 3);
        strel.setBoolean(1, 0, true);
        strel.setBoolean(0, 1, true);
        strel.setBoolean(1, 1, true);
        strel.setBoolean(2, 1, true);
        strel.setBoolean(1, 2, true);
        
        RunLengthBinaryArray2D array2 = RunLengthBinaryArray2D.convert(array);
        RunLengthBinaryArray2D strel2 = RunLengthBinaryArray2D.convert(strel);
        
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.dilation(array2, strel2, new int[] {1, 1});
        
        Strel2D strel2d = new Cross3x3Strel();
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel2d.dilation(array)));
        
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }
    
    private static final void fillRect(BinaryArray2D array, int xmin, int xmax, int ymin, int ymax, boolean state)
    {
        for (int y = ymin; y <= ymax; y++)
        {
            for (int x = xmin; x <= xmax; x++)
            {
                array.setBoolean(x, y, state);
            }
        }
    }
    
    public final static void main(String... args)
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(8, 4);
        
        array.setBoolean(2, 1, true);
        System.out.println("init:");
        array.print(System.out);
        
        array.setBoolean(3, 1, true);
        System.out.println("add after:");
        array.print(System.out);
        
        array.setBoolean(1, 1, true);
        System.out.println("add before:");
        array.print(System.out);
        
        array.setBoolean(5, 1, true);
        System.out.println("add further:");
        array.print(System.out);
        
        array.setBoolean(4, 1, true);
        System.out.println("add between:");
        array.print(System.out);
        
        System.out.println("number of runs: " + array.rows.get(1).runs.size());
        
        
        
        array.setBoolean(5, 1, false);
        System.out.println("remove at the end:");
        array.print(System.out);
        
        array.setBoolean(1, 1, false);
        System.out.println("remove at start:");
        array.print(System.out);
        
        array.setBoolean(3, 1, false);
        System.out.println("remove in between:");
        array.print(System.out);
        
        System.out.println("number of runs: " + array.rows.get(1).runs.size());

        array.setBoolean(2, 1, false);
        System.out.println("remove single run:");
        array.print(System.out);
        
        System.out.println("number of runs: " + array.rows.get(1).runs.size());
    }
}
