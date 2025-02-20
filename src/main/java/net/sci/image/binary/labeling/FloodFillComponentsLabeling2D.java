/**
 * 
 */
package net.sci.image.binary.labeling;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt16Array;
import net.sci.image.Connectivity2D;
import net.sci.image.morphology.FloodFill;

/**
 * Computes the labels of the connected components in a binary image. The type
 * of result is controlled by the bitDepth option.
 * 
 * Uses a Flood-fill type algorithm. The image pixels are iterated, and each
 * time a foreground pixel not yet associated with a label is encountered, its
 * connected component is associated with a new label.
 *
 * @see net.sci.image.morphology.FloodFill2D
 * @see FloodFillComponentsLabeling3D
 * 
 * @author dlegland
 *
 */
public class FloodFillComponentsLabeling2D extends AlgoStub implements ComponentsLabeling
{
    // ==============================================================
    // Class variables

    /**
     * The connectivity of the components, either 4 (default) or 8.
     */
    Connectivity2D connectivity = Connectivity2D.C4;

    /**
     * The factory of IntArray for creating new label maps.
     */
    IntArray.Factory<?> factory = UInt16Array.defaultFactory;

    
    // ==============================================================
    // Constructors

    /**
     * Constructor with default connectivity 4 and default output bitdepth equal
     * to 16.
     */
    public FloodFillComponentsLabeling2D()
    {
    }

    /**
     * Constructor specifying the connectivity and using default output bitdepth
     * equal to 16.
     * 
     * @param connectivity
     *            the connectivity of connected components (4 or 8)
     */
    public FloodFillComponentsLabeling2D(Connectivity2D connectivity)
    {
        this.connectivity = connectivity;
        checkConnectivity();
    }
    
    /**
     * Constructor specifying the connectivity and the factory for creating new
     * empty label maps.
     * 
     * @param connectivity
     *            the connectivity of connected components (4 or 8)
     * @param labelMapFactory
     *            the factory used to create new label maps.
     */
    public FloodFillComponentsLabeling2D(Connectivity2D connectivity, IntArray.Factory<?> labelMapFactory)
    {
        this(connectivity);
        this.factory = labelMapFactory;
    }
    
    /**
     * Constructor specifying the connectivity and using default output bit
     * depth equal to 16.
     * 
     * @param connectivity
     *            the integer value for connectivity of connected components (4
     *            or 8)
     */
    public FloodFillComponentsLabeling2D(int connectivity)
    {
        this(Connectivity2D.fromValue(connectivity));
    }

    /**
     * Throw an exception if connectivity is not 4 or 8 (necessary for FloodFill
     * algorithms).
     */
    private void checkConnectivity()
    {
        if (connectivity != Connectivity2D.C4 && connectivity != Connectivity2D.C8)
        {
            throw new IllegalArgumentException("Connectivity must be either 4 or 8, not " + connectivity);
        }
	}
  

    // ==============================================================
    // Processing methods

    public IntArray2D<?> processBinary2d(BinaryArray2D array)
    {
        IntArray2D<?> labels = createEmptyLabelMap(array);
        processBinary2d(array, labels);
        return labels;
    }

    public int processBinary2d(BinaryArray2D array, IntArray2D<?> labelMap)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int maxLabel = labelMap.sampleElement().typeMax().intValue();

        // the label counter
        int nLabels = 0;

        // iterate on image pixels to find new regions
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                if (!array.getBoolean(x, y)) continue;
                if (labelMap.getInt(x, y) > 0) continue;

                // a new label is found: check current label number
                if (nLabels == maxLabel)
                {
                    throw new RuntimeException("Max number of label reached (" + maxLabel + ")");
                }

                // increment label index, and propagate
                nLabels++;
                FloodFill.floodFillInt(array, x, y, labelMap, nLabels, this.connectivity);
            }
        }
        this.fireProgressChanged(this, 1, 1);

        return nLabels;
    }

    
    // ==============================================================
    // Implementation of the ComponentsLabeling interface

    @Override
    public int processBinary(BinaryArray array, IntArray<?> labelMap)
    {
        if (array.dimensionality() != 2)
        {
            throw new IllegalArgumentException("Requires a BinaryArray of dimensionality 2");
        }
        if (labelMap.dimensionality() != 2)
        {
            throw new IllegalArgumentException("Requires a Label Map of dimensionality 2");
        }
        if (!Arrays.isSameSize(array, labelMap))
        {
            throw new IllegalArgumentException("Input and Output arrays must have same dimensions");
        }
        return processBinary2d(BinaryArray2D.wrap(array), IntArray2D.wrap(labelMap));
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
    public IntArray2D<?> createEmptyLabelMap(Array<?> array)
    {
        return IntArray2D.wrap(this.factory.create(array.size()));
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
        if (array.dimensionality() != 2)
        {
            throw new IllegalArgumentException("Requires a BinaryArray of dimensionality 2");
        }
        IntArray<?> result = createEmptyLabelMap(array);
        processBinary2d(BinaryArray2D.wrap((BinaryArray) array), IntArray2D.wrap(result));
        return result;
    }
    
    public boolean canProcess(Array<?> array)
    {
        if (!(array instanceof BinaryArray)) 
            return false;
        if (array.dimensionality() != 2)
            return false;
        return true;
    }
}
