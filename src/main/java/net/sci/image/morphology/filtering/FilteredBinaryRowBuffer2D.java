/**
 * 
 */
package net.sci.image.morphology.filtering;

import java.util.function.BiFunction;

import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.morphology.strel.IndexedRowsStrel3D;

/**
 * A buffer of binary rows, each binary row being associated to a series of
 * filter results.
 * 
 * @author dlegland
 *
 */
public class FilteredBinaryRowBuffer2D
{
    // =============================================================
    // Static methods
    
    public static final FilteredBinaryRowBuffer2D create(RunLengthBinaryArray3D array, IndexedRowsStrel3D strel, BiFunction<BinaryRow, BinaryRow, BinaryRow> operator)
    {
        // retrieve size and offset data
        int sizeY = array.size(1);
        int strelSizeZ = strel.size()[2];
        int strelOffsetZ = strel.offset()[2];
        
        // create buffer
        int[] bufferSize2 = new int[] {sizeY, strelSizeZ};
        FilteredBinaryRowBuffer2D buffer = new FilteredBinaryRowBuffer2D(bufferSize2, strel.rows, operator);
        
        // initialize buffer with upper slices of array
        for (int iSlice = 0; iSlice < strelOffsetZ + 2; iSlice++)
        {
            buffer.update(array, 0);
        }
        for (int iSlice = 1 ; iSlice < strelSizeZ - strelOffsetZ - 1; iSlice++)
        {
            buffer.update(array, iSlice);
        }
        return buffer;
    }
    
    private static final void copySliceRows(RunLengthBinaryArray3D array, int sliceIndex, BinaryRow[] rows)
    {
        int sizeY = array.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            rows[y] = array.getRow(y, sliceIndex);
        }
    }
    
    
    // =============================================================
    // Class variables
    
    /**
     * the size of the buffer: first the size along y, then the size along z.
     */
    int[] size;
    
    BinaryRow[] filters;
    int nFilters;
    
    BiFunction<BinaryRow, BinaryRow, BinaryRow> operator;
    
    /**
     * A double buffer: first two indices corresponds to z and y indices (within
     * buffer), third index correspond to filter type.
     */
    BinaryRow[][][] buffer;

    /**
     * Keep reference to current slice, to avoid creating array at each update.
     */
    BinaryRow[] sliceRows;
    
    
    // =============================================================
    // Constructor
    
    public FilteredBinaryRowBuffer2D(int[] size, BinaryRow[] filters, BiFunction<BinaryRow, BinaryRow, BinaryRow> operator)
    {
        // keep processing information
        this.size = size;
        this.filters = filters;
        this.nFilters = filters.length;
        this.operator = operator;
        
        // allocate buffer
        this.buffer = new BinaryRow[size[1]][size[0]][nFilters];
        this.sliceRows = new BinaryRow[size[0]];
    }
    
    
    // =============================================================
    // Methods
    
    public BinaryRow getFilteredRow(int y, int z, int iStrel)
    {
        return buffer[z][y][iStrel];
    }
    
    /**
     * Updates this buffer by applying the different filters to the rows of the
     * specified slice of the array, and pushing the result at the end of the
     * buffer.
     * 
     * @param array
     *            the array containing the row
     * @param sliceIndex
     *            the slice index
     */
    public void update(RunLengthBinaryArray3D array, int sliceIndex)
    {
        // keep first row
        BinaryRow[][] slice0 = this.buffer[0];
        
        // shift all row arrays 
        int lastRow = this.buffer.length-1;
        for (int iRow = 0; iRow < lastRow; iRow++)
        {
            this.buffer[iRow] = this.buffer[iRow+1];
        }
        
        // retrieve rows of current slice
        copySliceRows(array, sliceIndex, sliceRows);

        // iterate over rows of the slice
        for (int iRow = 0; iRow < sliceRows.length; iRow++)
        {
            BinaryRow row = sliceRows[iRow];
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
        
        // append updated array to the buffer 
        this.buffer[lastRow] = slice0;    
    }
}
