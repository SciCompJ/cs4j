/**
 * 
 */
package net.sci.image.process.shape;

import java.util.Arrays;
import java.util.Collection;

import net.sci.array.Array2D;

/**
 * Creates a 2D array corresponding to the montage of a collection of arrays.
 *  
 * @author dlegland
 *
 */
public class Montage
{
    /**
     * Creates a 2D array corresponding to the montage of a collection of arrays
     * into a 2D matrix.
     * 
     * @param <T>
     *            the type of the arrays to process
     * @param nCols
     *            the number of arrays to use as columns.
     * @param nRows
     *            the number of arrays to use as rows.
     * @param arrays
     *            the arrays that will populate the result array
     * @return a montage of the input arrays organized as a 2D matrix
     */
    public static final <T> Array2D<T> create(int nCols, int nRows, Collection<? extends Array2D<T>> arrays)
    {
        int nArrays = arrays.size();
        if (nArrays < 1)
        {
            throw new IllegalArgumentException("Requires at least one array as input");
        }
        
        // retrieve size of each array
        int[][] dims = new int[nArrays][2];
        int i = 0;
        for (Array2D<T> array : arrays)
        {
            dims[i++] = array.size();
        }
        
        // compute maximum size of arrays along each row and each column
        int[] maxSizeInCols = new int[nCols];
        int[] maxSizeInRows = new int[nRows];
        i = 0;
        for (Array2D<T> array : arrays)
        {
            if (i >= nRows * nCols)
            {
                break;
            }
            int iCol = i % nCols;
            maxSizeInCols[iCol] = Math.max(maxSizeInCols[iCol], array.size(0));
            int iRow = Math.floorDiv(i, nCols);
            maxSizeInRows[iRow] = Math.max(maxSizeInRows[iRow], array.size(1));
            i++;
        }
        
        // compute output size in x and offset in x of each column
        int outputSizeX = maxSizeInCols[0];
        int[] xOffsets = new int[nCols];
        xOffsets[0] = 0;
        for (int iCol = 1; iCol < nCols; iCol++)
        {
            outputSizeX += maxSizeInCols[iCol];
            xOffsets[iCol] = xOffsets[iCol - 1] + maxSizeInCols[iCol - 1];
        }
        
        // compute output size in y and offset in y of each row
        int outputSizeY = maxSizeInRows[0];
        int[] yOffsets = new int[nRows];
        yOffsets[0] = 0;
        for (int iRow = 1; iRow < nRows; iRow++)
        {
            outputSizeY += maxSizeInRows[iRow];
            yOffsets[iRow] = yOffsets[iRow - 1] + maxSizeInRows[iRow - 1];
        }
        
        // allocate memory
        Array2D<T> res = Array2D.wrap(arrays.iterator().next().newInstance(outputSizeX, outputSizeY));
        
        // iterate over arrays
        i = 0;
        for (Array2D<T> array : arrays)
        {
            if (i >= nRows * nCols)
            {
                break;
            }
            
            // retrieve row and column indices
            int iCol = i % nCols;
            int iRow = Math.floorDiv(i, nCols);
            
            // array size
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            // iterate over x and y within current array
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.set(xOffsets[iCol] + x, yOffsets[iRow] + y, array.get(x, y)); 
                }
            }
            i++;
        }
        
        return res;
    }
    
    /**
     * Creates a 2D array corresponding to the montage of a collection of arrays
     * into a 2D matrix.
     * 
     * @param <T>
     *            the type of the arrays to process
     * @param nCols
     *            the number of arrays to use as columns
     * @param nRows
     *            the number of arrays to use as columns
     * @param arrays
     *            the arrays that will populate the result array
     * @return a montage of the input arrays organized as a 2D matrix
     */
    public static final <T> Array2D<T> create(int nCols, int nRows, @SuppressWarnings("unchecked") Array2D<T>... arrays)
    {
        return create(nCols, nRows, Arrays.asList(arrays));
    }
}
