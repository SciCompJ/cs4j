/**
 * 
 */
package net.sci.image.morphology.filtering;

import java.util.function.BiFunction;

import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.image.morphology.strel.IndexedRowsStrel2D;

/**
 * A buffer of binary rows, each binary row being associated to a series of
 * filter results.
 * 
 * @author dlegland
 *
 */
public class FilteredBinaryRowBuffer
{
    // =============================================================
    // Static methods
    
    public static final FilteredBinaryRowBuffer create(RunLengthBinaryArray2D array, IndexedRowsStrel2D strel, BiFunction<BinaryRow, BinaryRow, BinaryRow> operator)
    {
        // retrieve size and offset data
        int strelSizeY = strel.size()[1];
        int strelOffsetY = strel.offset()[1];
        
        // create buffer
        FilteredBinaryRowBuffer buffer = new FilteredBinaryRowBuffer(strelSizeY, strel.rows, operator);
        
        // initialize buffer with upper slices of array
        for (int iSlice = 0; iSlice < strelOffsetY + 2; iSlice++)
        {
            buffer.update(array.getRow(0));
        }
        for (int iSlice = 1 ; iSlice < strelSizeY - strelOffsetY - 1; iSlice++)
        {
            buffer.update(array.getRow(iSlice));
        }
        return buffer;
    }
    
    
    // =============================================================
    // Class variables
    
    BinaryRow[] filters;
    
    int nFilters;
    
    BiFunction<BinaryRow, BinaryRow, BinaryRow> operator;
    
    /**
     * A double buffer: first index corresponds to row index (within strel),
     * second index correspond to filter type.
     */
    BinaryRow[][] buffer;

    
    // =============================================================
    // Constructor
    
    public FilteredBinaryRowBuffer(int bufferSize, BinaryRow[] filters, BiFunction<BinaryRow, BinaryRow, BinaryRow> operator)
    {
        // keep processing information
        this.filters = filters;
        this.nFilters = filters.length;
        this.operator = operator;
        
        // allocate buffer
        this.buffer = new BinaryRow[bufferSize][nFilters];
    }
    
    
    // =============================================================
    // Methods
    
    public BinaryRow getFilteredRow(int iRow, int iFilter)
    {
        return buffer[iRow][iFilter];
    }
    
    /**
     * Updates this buffer by applying the different filters to the specified
     * row, and pushing the result at the end of the buffer.
     * 
     * @param row
     *            the row to use for computing the filters
     */
    public void update(BinaryRow row)
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
