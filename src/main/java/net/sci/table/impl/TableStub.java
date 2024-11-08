/**
 * 
 */
package net.sci.table.impl;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.Table;

/**
 * Base class for Table implementations.
 */
public abstract class TableStub implements Table
{
    // =============================================================
    // Class variables

    String name = "";

    /**
     * The categorical axis containing meta-data for rows.
     */
    CategoricalAxis rowAxis = null;

    
    // =============================================================
    // General methods

    /**
     * Returns the dimensions of this table: first the number of rows, then the
     * number of columns.
     * 
     * @return an array of integers containing the dimensions of this table
     */
    @Override
    public int[] size()
    {
        return new int[]{rowCount(), columnCount()};
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    // =============================================================
    // Management of rows
    
    @Override
    public Axis rowAxis()
    {
        return this.rowAxis;
    }

    public void setRowAxis(Axis axis)
    {
        if (axis != null)
        {
            if (!(axis instanceof CategoricalAxis))
            {
                throw new RuntimeException("Row axis must be an instance of CategoricalAxis");
            }
        }
        this.rowAxis = (CategoricalAxis) axis;
    }

    public String[] getRowNames()
    {
        return this.rowAxis != null ? rowAxis.itemNames() : null;
    }

    public void setRowNames(String[] names)
    {
        if (names != null && names.length != column(0).length())
        {
            throw new IllegalArgumentException(
                    "String array must have same length as the number of rows.");
        }
        
        if (this.rowAxis == null)
        {
            this.rowAxis = new CategoricalAxis("", names);
        }
        else
        {
            this.rowAxis = new CategoricalAxis(this.rowAxis.getName(), names);
        }
    }

    public String getRowName(int rowIndex)
    {
        return this.rowAxis != null ? rowAxis.itemName(rowIndex) : null;
    }

    @Override
    public void setRowName(int rowIndex, String name)
    {
        if (this.rowAxis == null)
        {
            this.rowAxis = new CategoricalAxis("", new String[column(0).length()]);
        }
        this.rowAxis.setItemName(rowIndex, name);
    }
}
