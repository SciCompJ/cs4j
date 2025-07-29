/**
 * 
 */
package net.sci.table.impl;

import net.sci.table.NumericColumn;

/**
 * An utility class used as implementation basis for NumericColumn classes.
 * Simply adds the management of unit name.
 */
public abstract class NumericColumnStub extends ColumnStub implements NumericColumn
{
    String unitName;
    
    protected NumericColumnStub(String name)
    {
        super(name);
    }
    
    public String getUnitName()
    {
        return unitName;
    }
    
    public void setUnitName(String name)
    {
        this.unitName = name;
    }

}
