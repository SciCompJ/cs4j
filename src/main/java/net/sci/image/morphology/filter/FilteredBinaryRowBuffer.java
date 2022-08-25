/**
 * 
 */
package net.sci.image.morphology.filter;

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
    int[] indices;
    BinaryRow[] filters;
    
    int nFilters;
    
    /**
     * A double buffer: first index corresponds to row index (within strel),
     * second index correspond to filter type.
     */
    BinaryRow[][] buffer;

    
    public FilteredBinaryRowBuffer(int[] indices, BinaryRow[] filters)
    {
        // keep processing information
        this.filters = filters;
        this.indices = indices;
        this.nFilters = filters.length;
        
        // allocate buffer
        this.buffer = new BinaryRow[indices.length][nFilters];
    }
    
    public BinaryRow getFilteredRow(int iRow)
    {
        return buffer[iRow][indices[iRow]];
    }
    
    public BinaryRow get(int iRow, int iFilter)
    {
        return buffer[iRow][iFilter];
    }
    
    public void shiftAndAdd(BinaryRow[] filteredRows)
    {
        this.buffer[0] = null;
        
        // shift all row arrays 
        int lastRow = this.buffer.length-1;
        for (int iRow = 0; iRow < lastRow; iRow++)
        {
            this.buffer[iRow] = this.buffer[iRow+1];
        }
        
        // append new array with the selected filters 
        this.buffer[lastRow] = filteredRows;
    }
}
