/**
 * 
 */
package net.sci.image.regionfeatures;

import net.sci.table.Table;

/**
 * Abstract class for a feature that can generate a data table with as many rows
 * as the number of regions to analyze.
 */
public interface RegionTabularFeature extends Feature
{
    /**
     * Creates a new table containing the numerical data representing this
     * feature.
     * 
     * @param data
     *            the class containing all the computed features.
     * @return a new table containing concatenation of the measures for all
     *         computed features
     */
    public default Table createTable(RegionFeatures data)
    {
        Table table = data.initializeRegionTable();
        updateTable(table, data);
        return table;
    }
    
    /**
     * Updates the specified result table with the result of this feature.
     * Depending on the feature implementation, this method may populate one or
     * several columns.
     * 
     * @param table
     *            the table to populate
     * @param data
     *            the class containing all the computed features.
     */
    public abstract void updateTable(Table table, RegionFeatures data);
}
