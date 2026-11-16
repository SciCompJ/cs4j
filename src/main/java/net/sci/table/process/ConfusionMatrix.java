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
 * A confusion matrix allows visualization of the performance of an algorithm,
 * usually a classification algorithm. Each row of the matrix represents the
 * instances in an actual class while each column represents the instances in a
 * predicted class.
 */
public class ConfusionMatrix
{
    /**
     * Default empty constructor.
     */
    public ConfusionMatrix()
    {
    }
    
    /**
     * Computes the confusion matrix from the levels in two categorical columns
     * with the same number of rows.
     * 
     * @param col1
     *            the first column (usually corresponding to the actual values)
     * @param col2
     *            the second column (usually corresponding to the predicted
     *            values)
     * @return the confusion matrix obtained from the two columns.
     */
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
