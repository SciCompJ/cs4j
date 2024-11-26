/**
 * 
 */
package net.sci.table.process;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.CategoricalColumn;
import net.sci.table.Table;

/**
 * 
 */
public class ConfusionMatrixTest
{
    /**
     * Test method for {@link net.sci.table.process.ConfusionMatrix#process(net.sci.table.Column, net.sci.table.Column)}.
     */
    @Test
    public final void testProcess()
    {
        String[] levelNames = new String[] {"true", "false"};
        int[] actualInds = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
        int[] predInds   = new int[] {1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1};
        CategoricalColumn col1 = CategoricalColumn.create("actual", actualInds, levelNames);
        CategoricalColumn col2 = CategoricalColumn.create("predicted", predInds, levelNames);
        
        Table matrix = new ConfusionMatrix().process(col1, col2);
        
        assertEquals(matrix.rowCount(), levelNames.length);
        assertEquals(matrix.columnCount(), levelNames.length);
        
        assertEquals(matrix.getValue(0, 0), 6, 0.01); // true positive
        assertEquals(matrix.getValue(1, 1), 3, 0.01); // true negative
        assertEquals(matrix.getValue(0, 1), 2, 0.01); // false positive
        assertEquals(matrix.getValue(1, 0), 1, 0.01); // false negative
        
        // check axes
        Axis rowAxis = matrix.getRowAxis();
        assertTrue(rowAxis instanceof CategoricalAxis);
        assertEquals(rowAxis.getName(), col1.getName());
        assertArrayEquals(((CategoricalAxis) rowAxis).itemNames(), col1.levelNames());
        Axis colAxis = matrix.getColumnAxis();
        assertTrue(colAxis instanceof CategoricalAxis);
        assertEquals(colAxis.getName(), col2.getName());
        assertArrayEquals(((CategoricalAxis) colAxis).itemNames(), col2.levelNames());
    }

}
