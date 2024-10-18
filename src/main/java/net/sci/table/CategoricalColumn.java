/**
 * 
 */
package net.sci.table;

/**
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
    
    public String[] getLevels();
}
