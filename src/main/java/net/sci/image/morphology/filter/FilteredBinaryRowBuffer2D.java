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
public class FilteredBinaryRowBuffer2D
{
    /**
     * the size of the buffer: first the size along y, then the size along z.
     */
    int[] size;
    
    int nFilters;
    
    /**
     * A double buffer: first two indices corresponds to z and y indices (within
     * buffer), third index correspond to filter type.
     */
    BinaryRow[][][] buffer;

    
    public FilteredBinaryRowBuffer2D(int[] size, int nFilters)
    {
        // keep processing information
        this.size = size;
        this.nFilters = nFilters;
        
        // allocate buffer
        this.buffer = new BinaryRow[size[1]][size[0]][nFilters];
    }
    
    public BinaryRow getFilteredRow(int y, int z, int iStrel)
    {
        return buffer[z][y][iStrel];
    }
    
   
    public void shiftAndAdd(BinaryRow[][] filteredSliceRows)
    {
        this.buffer[0] = null;
        
        // shift all slice arrays 
        int lastRow = this.buffer.length - 1;
        for (int iRow = 0; iRow < lastRow; iRow++)
        {
            this.buffer[iRow] = this.buffer[iRow+1];
        }
        
        // append new array with the selected filters 
        this.buffer[lastRow] = filteredSliceRows;
    }
}
