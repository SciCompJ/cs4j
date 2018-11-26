package net.sci.array.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array3D;

public class PermuteDimensionsTest
{
    @Test
    public final void testPermuteDimensions_intArray()
    {
        int[] newOrder = new int[] {2, 0, 1};
        PermuteDimensions op = new PermuteDimensions(newOrder);
        assertNotNull(op);
    }

    @Test
    public final void testCanProcess()
    {
        int[] newOrder = new int[] {2, 0, 1};
        PermuteDimensions op = new PermuteDimensions(newOrder);
        
        Array<?> array = UInt8Array3D.create(5, 4, 3); 
        assertTrue(op.canProcess(array));
    }

    @Test
    public final void testProcess()
    {
        // create input array
        int[] dims = new int[] {5, 4, 3};
        Array<?> array = UInt8Array.create(dims);
        
        // create operator
        int[] newOrder = new int[] {2, 0, 1};
        PermuteDimensions op = new PermuteDimensions(newOrder);
        
        // apply
        Array<?> result = op.process(array);
        
        // check result
        int[] newDims = new int[] {3, 5, 4};
        assertTrue(Arrays.isSameDimensionality(array, result));
        for (int d = 0; d < 3; d++)
        {
            assertTrue(result.getSize(d) == newDims[d]);
        }
    }
    
}
