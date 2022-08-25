/**
 * 
 */
package net.sci.image.morphology.filter;

import java.util.function.BiFunction;

import net.sci.array.binary.BinaryRow;

/**
 * A buffer of binary rows, each binary row being associated to a series of
 * filter results.
 * 
 * @author dlegland
 *
 */
public class FilteredBinaryRowBuffer
{
    BinaryRow[] filters;
    
    int nFilters;
    
    /**
     * A double buffer: first index corresponds to row index (within strel),
     * second index correspond to filter type.
     */
    BinaryRow[][] buffer;

    
    public FilteredBinaryRowBuffer(int bufferSize, BinaryRow[] filters)
    {
        // keep processing information
        this.filters = filters;
        this.nFilters = filters.length;
        
        // allocate buffer
        this.buffer = new BinaryRow[bufferSize][nFilters];
    }
    
    public BinaryRow getFilteredRow(int iRow, int iFilter)
    {
        return buffer[iRow][iFilter];
    }
        
    public void update(BinaryRow row, BiFunction<BinaryRow, BinaryRow, BinaryRow> operator)
    {
        // keep first row
        BinaryRow[] rows = this.buffer[0];
        
        // shift all row arrays 
        int lastRow = this.buffer.length-1;
        for (int iRow = 0; iRow < lastRow; iRow++)
        {
            this.buffer[iRow] = this.buffer[iRow+1];
        }
        
        // update first row
        if (row == null)
        {
            for (int i = 0; i < nFilters; i++)
            {
                rows[i] = new BinaryRow(); // TODO could avoid creation
            }
        }
        else
        {
            for (int i = 0; i < nFilters; i++)
            {
                rows[i] = operator.apply(row, filters[i]);
            }
        }
        
        // append new array with the selected filters 
        this.buffer[lastRow] = rows;
    }
}
