/**
 * 
 */
package net.sci.table;

/**
 * A Table column containing categorical data. Categorical data are selected
 * from a finite number of levels.
 * 
 * @author dlegland
 *
 */
public interface CategoricalColumn extends Column
{
    public static CategoricalColumn create(String name, int[] indices, String[] levels)
    {
        return new DefaultCategoricalColumn(name, indices, levels);
    }
    
    /**
     * Returns the category name for the specified row index.
     * 
     * @param row
     *            the row index (0-based)
     * @return the category name for specified row index
     */
    public String getName(int row);
    
    /**
     * Returns the different levels that can be represented within this
     * categorical column.
     * 
     * @return the different levels that can be represented within this
     *         categorical column.
     */
    public String[] levels();

    @Override
    public default String contentSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("categorical");
        int nLevels = levels().length;
        sb.append(" with " + nLevels + " levels");

        return sb.toString();
    }
}
