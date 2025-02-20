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
