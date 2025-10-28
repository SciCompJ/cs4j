/**
 * 
 */
package net.sci.table;

import java.util.ArrayList;
import java.util.stream.IntStream;

import net.sci.table.impl.DefaultCategoricalColumn;

/**
 * A Table column containing categorical data. Categorical data are selected
 * from a finite number of levels.
 * 
 * @author dlegland
 *
 */
public interface CategoricalColumn extends Column
{
    // =============================================================
    // static methods

    /**
     * Creates a new categorical column from a name, and a list of item level
     * names. The resulting column has same size as the array of levels.
     * 
     * {@snippet lang="java" :
     * String[] levels = new String[]{"true", "true", "false", "true", "false"};
     * CategoricalColumn column = CategoricalColumn.create("Levels", levels);
     * int colLength = column.length(); // returns 5
     * }
     * 
     * @param name
     *            the name of the column
     * @param levels
     *            the name of each element within the column
     * @return a new categorical column.
     */
    public static CategoricalColumn create(String name, String[] levels)
    {
        int nRows = levels.length;
        
        ArrayList<String> uniqueLevels = new ArrayList<String>();
        int[] indices = new int[nRows];
        for (int i = 0; i < nRows; i++)
        {
            String level = levels[i];
            int index = uniqueLevels.indexOf(level);
            if (index == -1)
            {
                index = uniqueLevels.size();
                uniqueLevels.add(level);
            }
            indices[i] = index;
        }
        
        String[] levelNames = uniqueLevels.toArray(new String[] {});
        return create(name, indices, levelNames);
    }
    
    /**
     * Creates a new categorical column from a name, the array of level index
     * for each element, and the array of level names.
     * 
     * Example:
     * {@snippet lang="java" :
     * int[] indices = new int[]{0, 1, 2, 1, 2, 2, 0, 0};
     * String[] names = new String[]{"Setosa", "Virginica", "Versicolor"};
     * CategoricalColumn column = CategoricalColumn.create("Species", indices, names);
     * int colLength = column.length(); // returns 8
     * }
     * 
     * @param name
     *            the name of the column
     * @param indices
     *            the array of level index for each element. The size of this
     *            array determines the size of the column.
     * @param levelNames
     *            the name of each level. The length of this array determines
     *            the maximum index value.
     * @return a new categorical column.
     */
    public static CategoricalColumn create(String name, int[] indices, String[] levelNames)
    {
        return new DefaultCategoricalColumn(name, indices, levelNames);
    }
    
    /**
     * Converts an arbitrary column into a categorical column by considering
     * each unique value in the input column as a level. If the input column is
     * already an instance of CategoricalColumn, it is simply returned.
     * 
     * @param column
     *            the column to convert
     * @return the converted categorical column, with same length
     */
    public static CategoricalColumn convert(Column column)
    {
        if (column instanceof CategoricalColumn)
        {
            return (CategoricalColumn) column;
        }
        
        int nRows = column.length();
        String[] levels = new String[nRows];
        for (int iRow = 0; iRow < nRows; iRow++)
        {
            levels[iRow] = "" + column.getValue(iRow);
        }
        
        return create(column.getName(), levels);
    }
    
    /**
     * Concatenates two categorical columns. Returns a new column whose length
     * is the sum of the two columns, and containing all levels of each column.
     * The name of the first column is used as name of the result column.
     * 
     * @param col1
     *            the first column
     * @param col2
     *            the second column
     * @return the concatenated column
     */
    public static CategoricalColumn concatenate(CategoricalColumn col1, CategoricalColumn col2)
    {
        int n1 = col1.length();
        int n2 = col2.length();
        
        String[] allItems = new String[n1+n2];
        for (int i = 0; i < n1; i++)
        {
            allItems[i] = col1.getString(i);
        }
        for (int i = 0; i < n2; i++)
        {
            allItems[n1 + i] = col2.getString(i);
        }
        return create(col1.getName(), allItems);
    }

    
    // =============================================================
    // New methods

    /**
     * Returns the different levels that can be represented within this
     * categorical column.
     * 
     * @return the different levels that can be represented within this
     *         categorical column.
     */
    public String[] levelNames();
    
    /**
     * Returns the level index at the specified row.
     * 
     * @param row
     *            the row index
     * @return the level index at the specified row
     */
    public int getLevelIndex(int row);

    /**
     * Changes the level index at the specified row.
     * 
     * @param row
     *            the row index
     * @param index
     *            the new index at the specified row
     */
    public void setLevelIndex(int row, int index);

    
    // =============================================================
    // Specialization of the Column interface

    @Override
    public default CategoricalColumn selectRows(int[] rowIndices)
    {
        int[] newIndices = IntStream.of(rowIndices)
                .map(index -> getLevelIndex(index))
                .toArray();
        return create(this.getName(), newIndices, levelNames());
    }
    
    /**
     * Returns the category name for the specified row index.
     * 
     * @param row
     *            the row index (0-based)
     * @return the category name for specified row index
     */
    @Override
    public String getString(int row);
    
    @Override
    public default void setValue(int row, double value)
    {
        throw new RuntimeException("Can not change the value of a Categorical column");
    }

    /**
     * Creates a new Categorical column with the specified length, using the
     * same levels as the original column.
     * 
     * @param name
     *            the name of the new column
     * @param colLength
     *            the length of the new column
     * @return a new Categorical column.
     */
    @Override
    public default CategoricalColumn newInstance(String name, int colLength)
    {
        return create(name, new int[colLength], levelNames());
    }
    
    @Override
    public CategoricalColumn duplicate();
    
    @Override
    public default String contentSummary()
    {
        return new StringBuilder()
                .append("categorical")
                .append(" with " + levelNames().length + " levels")
                .toString();
    }
}
