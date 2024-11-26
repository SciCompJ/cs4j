/**
 * 
 */
package net.sci.table.process;

import net.sci.axis.CategoricalAxis;
import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.IntegerColumn;
import net.sci.table.Table;

/**
 * 
 */
public class ConfusionMatrix
{

    public Table process(Column col1, Column col2)
    {
        if (col1.length() != col2.length())
        {
            throw new IllegalArgumentException("The two input columns must have the same length");
        }
        
        // convert explicitly to categorical
        CategoricalColumn cat1 = convert(col1);
        CategoricalColumn cat2 = convert(col2);
        
        int nRows = cat1.levelNames().length;
        int nCols = cat2.levelNames().length;
        Table res = Table.create(nRows, nCols);
        
        for (int iRow = 0; iRow < col1.length(); iRow++)
        {
            int lev1 = cat1.getLevelIndex(iRow);
            int lev2 = cat2.getLevelIndex(iRow);
            res.setValue(lev1, lev2, res.getValue(lev1, lev2) + 1);
        }
        
        res.setRowAxis(new CategoricalAxis(cat1.getName(), cat1.levelNames()));
        res.setColumnAxis(new CategoricalAxis(cat2.getName(), cat2.levelNames()));
        res.setName("ConfusionMatrix");
        return res;
    }
    
    private static final CategoricalColumn convert(Column column)
    {
        if (column instanceof CategoricalColumn) return (CategoricalColumn) column;
        if (column instanceof IntegerColumn)
        {
            return CategoricalColumn.convert((IntegerColumn) column);
        }
        
        throw new RuntimeException(
                "Unable to convert to categorical column from class: " + column.getClass());
    }
}
