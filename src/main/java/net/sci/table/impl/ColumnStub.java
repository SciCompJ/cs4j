/**
 * 
 */
package net.sci.table.impl;

import net.sci.table.Column;

/**
 * A stub class for implementing columns.
 * 
 * Simply manages the name of the column.
 */
public abstract class ColumnStub implements Column
{
    String name;
    
    protected ColumnStub(String name)
    {
        this.name = name;
    }
   
    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String newName)
    {
        this.name = newName;
    }

}
