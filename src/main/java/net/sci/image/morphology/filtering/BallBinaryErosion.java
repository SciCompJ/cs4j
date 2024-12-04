/**
 * 
 */
package net.sci.image.morphology.filtering;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.morphology.strel.IndexedRowsStrel2D;
import net.sci.image.morphology.strel.IndexedRowsStrel3D;

/**
 * Computes erosion of a binary array.
 * 
 * Converts the input into run-length encoded arrays when necessary.
 * 
 * @see BinaryDilation
 * @see BinaryOpening
 * @see BinaryClosing
 * @see Erosion
 * 
 * @author dlegland
 */
public class BallBinaryErosion extends AlgoStub implements ArrayOperator
{
    double radius;
    boolean padding = true;
            
    public BallBinaryErosion(double radius)
    {
        this(radius, true);
    }
    
    public BallBinaryErosion(double radius, boolean padding)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("Requires a positive radius");
        }
        this.radius = radius;
        this.padding = padding;
    }
    
    /**
     * Performs morphological erosion on a 2D binary array. The input is
     * converted into an instance of <code>RunLengthBinaryArray2D</code> when
     * necessary.
     * 
     * @see #processBinary3d(BinaryArray3D)
     * @see net.sci.array.binary.RunLengthBinaryArray2D
     * 
     * @param array
     *            the array on which erosion should be applied
     * @return the result of erosion as a new array
     */
    public BinaryArray2D processBinary2d(BinaryArray2D array)
    {
        // ensure input array uses RLE representation (if not already the case)
        fireStatusChanged(this, "Prepare input image");
        RunLengthBinaryArray2D rleArray = RunLengthBinaryArray2D.convert(array);
        
        // create data structure for retrieving rows from row index (0 -> N-1)
        IndexedRowsStrel2D strel = IndexedRowsStrel2D.createDisk(radius);
        
        // Initialize data related to strel
        int intRadius = (int) Math.floor(this.radius + 0.5);
        int strelSizeY = 2 * intRadius + 1;
        int strelOffsetY = intRadius;
        
        // Optionally expand the binary rows to better manage borders
        if (this.padding)
        {
            fireStatusChanged(this, "Pad array");
            rleArray = BinaryErosion.padArray2d(rleArray, intRadius, intRadius);
        }
        
        // initialize a buffer of filtered rows
        FilteredBinaryRowBuffer buffer = FilteredBinaryRowBuffer.create(rleArray, strel, BinaryErosion::erosion);
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        
        // create result array
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // iterate over rows of result array
        fireStatusChanged(this, "Iterate over rows");
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // shift buffer by using row at index y+sizeY-1-offset
            int index = Math.min(y + strelSizeY - 1 - strelOffsetY, sizeY - 1);
            buffer.update(rleArray.getRow(index));
            
            // initialize full result row
            BinaryRow resRow = new BinaryRow(new Run(0, sizeX - 1));
            
            // iterate over rows of structuring element
            for (int yStrel = 0; yStrel < strelSizeY; yStrel++)
            {
                int y2 = y + yStrel - strelOffsetY;
                // check current row is within the input array
                if (y2 < 0 || y2 > sizeY - 1)
                {
                    continue;
                }
                // if any of the rows is empty, result of dilation is empty
                if (rleArray.isEmptyRow(y2))
                {
                    resRow = new BinaryRow();
                    break;
                }
                
                // retrieve the result of row erosion with appropriate strel row
                int strelRowIndex = strel.indices[yStrel];
                BinaryRow row = buffer.getFilteredRow(yStrel, strelRowIndex);
                
                // update result
                resRow = BinaryRow.intersection(resRow, row);
            }
            
            if (!resRow.isEmpty())
            {
                res.setRow(y, resRow.crop(0, sizeX - 1));
            }
        }

        fireProgressChanged(this, 1, 1);
        return res;
    }
    
    /**
     * Performs morphological erosion on a 3D binary array. The input is
     * converted into an instance of <code>RunLengthBinaryArray3D</code> when
     * necessary.
     * 
     * @see #processBinary2d(BinaryArray2D)
     * @see net.sci.array.binary.RunLengthBinaryArray3D
     * 
     * @param array
     *            the array on which erosion should be applied
     * @return the result of erosion as a new array
     */
    public BinaryArray3D processBinary3d(BinaryArray3D array)
    {
        // ensure input array uses RLE representation (if not already the case)
        this.fireStatusChanged(this, "Prepare input image");
        RunLengthBinaryArray3D rleArray = RunLengthBinaryArray3D.convert(array);
        
        // Create data structure to index strel rows
        IndexedRowsStrel3D strel = IndexedRowsStrel3D.createBall(radius);

        // Initialize data related to strel
        int intRadius = (int) Math.floor(this.radius + 0.5);
        int strelSizeZ = 2 * intRadius + 1;
        int strelOffsetZ = intRadius;

        // Optionally expand the binary rows to better manage borders
        if (this.padding)
        {
            fireStatusChanged(this, "Pad array");
            rleArray = BinaryErosion.padArray3d(rleArray, intRadius, intRadius);
        }
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        int sizeZ = rleArray.size(2);
        
        // create buffer
        FilteredBinaryRowBuffer2D buffer = FilteredBinaryRowBuffer2D.create(rleArray, strel, BinaryErosion::erosion);
        
        // create a map of slices for storing result
        HashMap<Integer, HashMap<Integer, BinaryRow>> slices = new HashMap<>();
        
        // iterate over rows of result array
        this.fireStatusChanged(this, "Iterate over z-slices");
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            HashMap<Integer, BinaryRow> currentSlice = new HashMap<>();
            
            // shift buffer by using row at index z+sizeZ-1-offsetZ
            int zNextSlice = Math.min(z + strelSizeZ - 1 - strelOffsetZ, sizeZ - 1);
            buffer.update(rleArray, zNextSlice);
            
            for (int y = 0; y < sizeY; y++)
            {
                // initialize full result row
                BinaryRow resRow = new BinaryRow(new Run(0, sizeX - 1));

                // iterate over rows of structuring element
                for (int zStrel : strel.indices.keySet())
                {
                    int z2 = z + zStrel;
                    // check current slice is within the input array
                    if (z2 < 0 || z2 > sizeZ - 1)
                    {
                        continue;
                    }
                    
                    HashMap<Integer, Integer> strelSlice = strel.indices.get(zStrel); 
                    for (int yStrel : strelSlice.keySet())
                    {
                        int y2 = y + yStrel;
                        if (y2 < 0 || y2 > sizeY - 1)
                        {
                            continue;
                        }
                        
                        // if input row is empty, result of row erosion is empty, and
                        // hence the result of intersection with previous result
                        if (rleArray.isEmptyRow(y2, z2))
                        {
                            resRow = new BinaryRow();
                            break;
                        }
                        else
                        {
                            // retrieve the result of row dilation with appropriate strel row
                            int index = strelSlice.get(yStrel);
                            int zBuffer = zStrel + intRadius;
                            BinaryRow row = buffer.getFilteredRow(y2, zBuffer, index);
                            
                            // update result
                            resRow = BinaryRow.intersection(resRow, row);
                        }
                    }
                }
                
                if (!resRow.isEmpty())
                {
                    currentSlice.put(y, resRow.crop(0, sizeX - 1));
                }
            }
            
            if (!currentSlice.isEmpty())
            {
                slices.put(z, currentSlice);
            }
        }
        
        fireProgressChanged(this, 1, 1);
        
        // create result array
        return new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ, slices);
    }
    
    public BinaryArray processBinary(BinaryArray array)
    {
        int nd = array.dimensionality();
        switch (nd)
        {
            case 2: return processBinary2d(BinaryArray2D.wrap(array));
            case 3: return processBinary3d(BinaryArray3D.wrap(array));
            default:
                throw new IllegalArgumentException("Requires an array of dimensionality 2, not " + nd);
        }
    }
    /**
     * Default implementation that case the input array to a BinaryArray, and
     * calls the processBinary method.
     * 
     * @param array
     *            the array to process
     * @throws RuntimeException
     *             if the input array is not an instance of BinaryArray
     */
    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        if (array instanceof BinaryArray)
        {
            return processBinary((BinaryArray) array);
        }
        else
        {
            throw new RuntimeException("Requires an instance of BinaryArray");
        }
    }
}
