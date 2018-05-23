package net.sci.array.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.color.RGB8Array;
import net.sci.array.color.RGB8Array2D;

public class ConcatenateTest
{
    
    @Test
    public final void testProcess_RGB8_Dim0()
    {
        RGB8Array2D array1 = RGB8Array2D.create(8, 6);
        RGB8Array2D array2 = RGB8Array2D.create(4, 6);
        Concatenate op = new Concatenate(0);
        
        Array<?> result = op.process(array1, array2);
        assertTrue(result instanceof RGB8Array);
        assertEquals(4+8, result.getSize(0));
        assertEquals(6, result.getSize(1));
    }
    
    @Test
    public final void testProcess_RGB8_Dim1()
    {
        RGB8Array2D array1 = RGB8Array2D.create(8, 6);
        RGB8Array2D array2 = RGB8Array2D.create(8, 10);
        Concatenate op = new Concatenate(1);
        
        Array<?> result = op.process(array1, array2);
        assertTrue(result instanceof RGB8Array);
        assertEquals(8, result.getSize(0));
        assertEquals(6+10, result.getSize(1));
    }
    
    @Test
    public final void testProcess_RGB8_Dim2()
    {
        RGB8Array2D array1 = RGB8Array2D.create(4, 3);
        RGB8Array2D array2 = RGB8Array2D.create(4, 3);
        Concatenate op = new Concatenate(2);
        
        Array<?> result = op.process(array1, array2);
        assertTrue(result instanceof RGB8Array);
        assertEquals(4, result.getSize(0));
        assertEquals(3, result.getSize(1));
        assertEquals(2, result.getSize(2));
    }
    
}
