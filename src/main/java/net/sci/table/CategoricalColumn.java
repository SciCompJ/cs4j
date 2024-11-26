/**
 * 
 */
package net.sci.table;

import java.util.ArrayList;

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
     * @param levels
     *            the name of each level. The length of this array determines
     *            the maximum index value.
     * @return a new categorical column.
     */
    public static CategoricalColumn create(String name, int[] indices, String[] levels)
    {
        return new DefaultCategoricalColumn(name, indices, levels);
    }
    
    /**
     * Converts an integer column into a categorical column by considering each
     * unique value in the input column as a level.
     * 
     * @param column
     *            the column to convert
     * @return the converted categorical column, with same length
     */
    public static CategoricalColumn convert(IntegerColumn column)
    {
        ArrayList<Integer> uniqueValues = new ArrayList<Integer>();
        for (int i = 0; i < column.length(); i++)
        {
            int val = column.getInt(i);
            if (!uniqueValues.contains(val))
            {
                uniqueValues.add(val);
            }
        }
        
        String[] levels = uniqueValues.stream().map(val -> "" + val).toArray(String[]::new);
        int[] indices = new int[column.length()];
        for (int i = 0; i < column.length(); i++)
        {
            indices[i] = uniqueValues.indexOf(column.get(i));
        }
        
        return create(column.getName(), indices, levels); 
    }
    
    /**
     * Returns the different levels that can be represented within this
     * categorical column.
     * 
     * @return the different levels that can be represented within this
     *         categorical column.
     */
    public String[] levelNames();
    
    public int getLevelIndex(int row);

    public void setLevelIndex(int row, int index);

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

    @Override
    public default String contentSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("categorical");
        int nLevels = levelNames().length;
        sb.append(" with " + nLevels + " levels");

        return sb.toString();
    }
}
