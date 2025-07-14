/**
 * 
 */
package net.sci.axis;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class CategoricalAxisTest
{

    /**
     * Test method for {@link net.sci.axis.CategoricalAxis#length()}.
     */
    @Test
    public final void testLength()
    {
        CategoricalAxis axis = createDefaultAxis();
        
        assertEquals(6, axis.length());
    }

    /**
     * Test method for {@link net.sci.axis.CategoricalAxis#itemNames()}.
     */
    @Test
    public final void testItemNames()
    {
        CategoricalAxis axis = createDefaultAxis();
        
        assertEquals(6, axis.itemNames().length);
    }

    /**
     * Test method for {@link net.sci.axis.CategoricalAxis#selectElements(int[])}.
     */
    @Test
    public final void testSelectElements()
    {
        CategoricalAxis axis = createDefaultAxis();
        int[] inds = new int[] {3, 4, 5};
        
        CategoricalAxis res = axis.selectElements(inds);
        
        assertEquals(3, res.length());
        assertEquals(axis.getName(), res.getName());
    }
    
    private static final CategoricalAxis createDefaultAxis()
    {
        String[] levels = new String[] {"Red", "Green", "Blue", "Cyan", "Magenta", "Yellow"};
        return new CategoricalAxis("Colors", levels);
    }

}
