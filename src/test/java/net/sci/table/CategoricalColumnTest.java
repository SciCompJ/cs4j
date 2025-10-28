/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class CategoricalColumnTest
{

    /**
     * Test method for {@link net.sci.table.CategoricalColumn#create(java.lang.String, java.lang.String[])}.
     */
    @Test
    public final void test_create_StringArray()
    {
        String item1 = "item1", item2 = "item2", item3 = "item3";
        String[] itemNames = new String[] {item1, item2, item2, item1, item3, item2};
        
        CategoricalColumn col = CategoricalColumn.create("name", itemNames);
        
        assertEquals(itemNames.length, col.length());
        assertEquals(item1, col.getString(0));
        assertEquals(item2, col.getString(1));
        assertEquals(item3, col.getString(4));
    }

    /**
     * Test method for {@link net.sci.table.CategoricalColumn#convert(net.sci.table.IntegerColumn)}.
     */
    @Test
    public final void test_convert_integerColumn()
    {
        int[] intValues = new int[]{1, 2, 2, 1, 3, 2};
        IntegerColumn intCol = IntegerColumn.create("name", intValues);
        
        CategoricalColumn col = CategoricalColumn.convert(intCol);
        
        assertEquals(intValues.length, col.length());
        assertEquals(3, col.levelNames().length);
    }

    /**
     * Test method for {@link net.sci.table.CategoricalColumn#convert(net.sci.table.IntegerColumn)}.
     */
    @Test
    public final void test_convert_floatColumn()
    {
        double[] floatValues = new double[]{1.2, 2.3, 2.3, 1.2, 3.4, 2.3};
        FloatColumn floatCol = FloatColumn.create("name", floatValues);
        
        CategoricalColumn col = CategoricalColumn.convert(floatCol);
        
        assertEquals(floatValues.length, col.length());
        assertEquals(3, col.levelNames().length);
    }

    /**
     * Test method for {@link net.sci.table.CategoricalColumn#concatenate(net.sci.table.CategoricalColumn, net.sci.table.CategoricalColumn)}.
     */
    @Test
    public final void test_concatenate()
    {
        String item1 = "item1", item2 = "item2", item3 = "item3", item4 = "item4";
        String[] items1 = new String[] {item1, item2, item2, item1, item3, item2};
        String[] items2 = new String[] {item2, item3, item4, item3};
        CategoricalColumn col1 = CategoricalColumn.create("name1", items1);
        CategoricalColumn col2 = CategoricalColumn.create("name2", items2);
        
        CategoricalColumn col = CategoricalColumn.concatenate(col1, col2);
        
        assertEquals(col1.length()+col2.length(), col.length());
        assertEquals(4, col.levelNames().length);
        
        assertEquals(item1, col.getString(0));
        assertEquals(item2, col.getString(6));
        assertEquals(item3, col.getString(7));
        assertEquals(item4, col.getString(8));
    }

}
