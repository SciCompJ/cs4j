/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BinaryRowTest
{

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Isolated()
    {
        BinaryRow row = createRow();
        
        row.set(15, true);
        
        assertTrue(row.get(15));
        assertEquals(3, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Left()
    {
        BinaryRow row = createRow();
        
        row.set(0, true);
        
        assertTrue(row.get(0));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Right()
    {
        BinaryRow row = createRow();
        
        row.set(10, true);
        
        assertTrue(row.get(10));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Between()
    {
        BinaryRow row = createRow();
        
        row.set(6, true);
        
        assertTrue(row.get(6));
        assertEquals(1, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Within()
    {
        BinaryRow row = createRow();
        
        row.set(4, true);
        
        assertTrue(row.get(4));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_False_Split()
    {
        BinaryRow row = createRow();
        
        row.set(3, false);
        
        assertFalse(row.get(3));
        assertEquals(3, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_False_Left()
    {
        BinaryRow row = createRow();
        
        row.set(1, false);
        
        assertFalse(row.get(1));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_False_Right()
    {
        BinaryRow row = createRow();
        
        row.set(5, false);
        
        assertFalse(row.get(5));
        assertEquals(2, row.runs.size());
    }

    
    /**
     * Generates a simple row composed of two runs, one with 5 elements, the
     * other one with 3 elements.
     * 
     * <pre>
     *  0  1  2  3  4  5  6  7  8  9 10
     *  -  o  o  o  o  o  -  o  o  o  -
     * </pre>
     * 
     * @return the sample row.
     */
    private BinaryRow createRow()
    {
        BinaryRow row = new BinaryRow();
        
        // first run
        row.set(1, true);
        row.set(2, true);
        row.set(3, true);
        row.set(4, true);
        row.set(5, true);
        
        // second run
        row.set(7, true);
        row.set(8, true);
        row.set(9, true);
        
        return row;
    }
}
