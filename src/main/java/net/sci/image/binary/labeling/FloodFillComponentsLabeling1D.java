/**
 * 
 */
package net.sci.image.binary.labeling;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray1D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray1D;
import net.sci.array.numeric.ScalarArray1D;
import net.sci.array.numeric.UInt16Array;

/**
 * Computes the labels of the connected components in a binary image. The type
 * of result is controlled by the bitDepth option.
 * 
 * Uses a Flood-fill type algorithm. The image pixels are iterated, and each
 * time a foreground pixel not yet associated with a label is encountered, its
 * connected component is associated with a new label.
 *
 * @see net.sci.image.morphology.FloodFill
 * @see FloodFillComponentsLabeling2D
 * 
 * @author dlegland
 *
 */
public class FloodFillComponentsLabeling1D extends AlgoStub implements ComponentsLabeling
{
    // ==============================================================
    // Class variables

    /**
     * The factory of IntArray for creating new label maps.
     */
    IntArray.Factory<?> factory = UInt16Array.defaultFactory;
    

    // ==============================================================
    // Constructors

    /**
     * Constructor with default output bitdepth equal to 16.
     */
    public FloodFillComponentsLabeling1D()
    {
    }

    /**
     * Constructor specifying the factory for creating new empty label maps.
     * 
     * @param labelMapFactory
     *            the factory used to create new label maps.
     */
    public FloodFillComponentsLabeling1D(IntArray.Factory<?> labelMapFactory)
    {
        this.factory = labelMapFactory;
    }


    // ==============================================================
    // Processing methods

    public IntArray1D<?> processBinary1d(BinaryArray1D array)
    {
        IntArray1D<?> labelMap = IntArray1D.wrap(this.factory.create(array.size()));
        processBinary1d(array, labelMap);
        return labelMap;
    }

    public int processBinary1d(BinaryArray1D array, IntArray1D<?> labelMap)
    {
        // get image size
        int sizeX = array.size(0);
        int maxLabel = labelMap.sampleElement().typeMax().intValue();

        // the label counter
        int nLabels = 0;

        // iterate on image pixels to find new regions
        for (int x = 0; x < sizeX; x++)
        {
            this.fireProgressChanged(this, x, sizeX);
            if (!array.getBoolean(x)) continue;
            if (labelMap.getInt(x) > 0) continue;

            // a new label is found: check current label number
            if (nLabels == maxLabel)
            {
                throw new RuntimeException("Max number of label reached (" + maxLabel + ")");
            }

            // increment label index, and propagate
            nLabels++;
            floodFillInt(array, x, labelMap, nLabels);
        }
        this.fireProgressChanged(this, 1, 1);

        return nLabels;
    }

    /**
     * Assigns in <code>labelImage</code> all the neighbor pixels of (x) that
     * have the same value in <code>image</code>, the specified new label value
     * (<code>value</code>), using the specified connectivity.
     * 
     * @param input
     *            original image to read the pixel values from
     * @param x0
     *            x-coordinate of the seed pixel
     * @param output
     *            the binary array to fill in
     * @param value
     *            filling value
     */
    private final static void floodFillInt(ScalarArray1D<?> input, int x0,
            IntArray1D<?> output, int value)
    {
        // retrieve array length
        int sizeX = input.size(0);
        
        // get start value
        double oldValue = input.getValue(x0);
        
        // find start of scan-line
        int x1 = x0; 
        while (x1 > 0 && input.getValue(x1-1) == oldValue)
            x1--;

        // find end of scan-line
        int x2 = x0;
        while (x2 < sizeX - 1 && input.getValue(x2+1) == oldValue)
            x2++;

        // fill current output range
        for (int x = x1; x <= x2; x++)
        {
            output.setInt(x, value);
        }
    }


    // ==============================================================
    // Implementation of the ComponentsLabeling interface

    @Override
    public int processBinary(BinaryArray array, IntArray<?> labelMap)
    {
        if (array.dimensionality() != 1)
        {
            throw new IllegalArgumentException("Requires a BinaryArray of dimensionality 1");
        }
        if (labelMap.dimensionality() != 1)
        {
            throw new IllegalArgumentException("Requires a Label Map of dimensionality 1");
        }
        if (!Arrays.isSameSize(array, labelMap))
        {
            throw new IllegalArgumentException("Input and Output arrays must have same dimensions");
        }
        return processBinary1d(BinaryArray1D.wrap(array), IntArray1D.wrap(labelMap));
    }

    /**
     * Creates a new array that can be used as output for processing the given
     * input array.
     * 
     * @param array
     *            the reference array
     * @return a new instance of Array that can be used for processing input
     *         array.
     */
    @Override
    public IntArray1D<?> createEmptyLabelMap(Array<?> array)
    {
        return IntArray1D.wrap(this.factory.create(array.size()));
    }


    // ==============================================================
    // Implementation of the ArrayOperator interface

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array instanceof BinaryArray))
        {
            throw new IllegalArgumentException("Requires a BinaryArray instance");
        }
        if (array.dimensionality() != 1)
        {
            throw new IllegalArgumentException("Requires a BinaryArray of dimensionality 1");
        }
        return processBinary1d(BinaryArray1D.wrap((BinaryArray) array));
    }
    
    public boolean canProcess(Array<?> array)
    {
        if (!(array instanceof BinaryArray)) 
            return false;
        if (array.dimensionality() != 1)
            return false;
        return true;
    }
}
