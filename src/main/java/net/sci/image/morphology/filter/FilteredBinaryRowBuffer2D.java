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
public class FilteredBinaryRowBuffer2D
{
    /**
     * the size of the buffer: first the size along y, then the size along z.
     */
    int[] size;
    
    BinaryRow[] filters;
    int nFilters;
    
    /**
     * A double buffer: first two indices corresponds to z and y indices (within
     * buffer), third index correspond to filter type.
     */
    BinaryRow[][][] buffer;

    
    public FilteredBinaryRowBuffer2D(int[] size, BinaryRow[] filters)
    {
        // keep processing information
        this.size = size;
        this.filters = filters;
        this.nFilters = filters.length;
        
        // allocate buffer
        this.buffer = new BinaryRow[size[1]][size[0]][nFilters];
    }
    
    public BinaryRow getFilteredRow(int y, int z, int iStrel)
    {
        return buffer[z][y][iStrel];
    }
    
    public void update(BinaryRow[] slice, BiFunction<BinaryRow, BinaryRow, BinaryRow> operator)
    {
        // keep first row
        BinaryRow[][] slice0 = this.buffer[0];
        
        // shift all row arrays 
        int lastRow = this.buffer.length-1;
        for (int iRow = 0; iRow < lastRow; iRow++)
        {
            this.buffer[iRow] = this.buffer[iRow+1];
        }
        
        // iterate over rows of the slice
        for (int iRow = 0; iRow < slice.length; iRow++)
        {
            BinaryRow row = slice[iRow];
            if (row == null)
            {
                for (int iFilt = 0; iFilt < nFilters; iFilt++)
                {
                    slice0[iRow][iFilt] = new BinaryRow(); // TODO could avoid creation
                }
            }
            else
            {
                for (int iFilt = 0; iFilt < nFilters; iFilt++)
                {
                    slice0[iRow][iFilt] = operator.apply(row, filters[iFilt]);
                }
            }
        }
        
        // append new array with the selected filters 
        this.buffer[lastRow] = slice0;
    }
}
