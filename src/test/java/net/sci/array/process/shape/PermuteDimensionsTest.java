package net.sci.array.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.Arrays;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array3D;

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
            assertTrue(result.size(d) == newDims[d]);
        }
    }
    

    @Test
    public final void testCreateView_StringArray3D()
    {
        Array3D<String> array = createStringArray3D();

        PermuteDimensions permDims = new PermuteDimensions(new int[] {2, 0, 1});
        Array<?> view = permDims.createView(array);
        
        assertEquals(3, view.dimensionality());
        assertEquals(3, view.size(0));
        assertEquals(5, view.size(1));
        assertEquals(4, view.size(2));

        assertEquals(array.get(3, 2, 1), view.get(new int[]{1, 3, 2}));
        
        // modifies value in array and check equality
        array.set(3, 2, 1, "Hello!");
        assertEquals(array.get(3, 2, 1), view.get(new int[]{1, 3, 2}));
    }

    private Array3D<String> createStringArray3D()
    {
        String[] digits = new String[]{"a", "b", "c", "d", "e"};
        Array3D<String> array = Array3D.create(5, 4, 3, "");
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    String str = digits[z] + digits[y] + digits[x];
                    array.set(x, y, z, str);
                }
            }
        }
        return array;
    }

}
