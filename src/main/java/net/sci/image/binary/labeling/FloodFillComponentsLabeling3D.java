/**
 * 
 */
package net.sci.image.binary.labeling;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.UInt16Array;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.morphology.FloodFill;

/**
 * Computes the labels of the connected components in a 3D binary image. The
 * type of result is controlled by the bitDepth option.
 * 
 * Uses a Flood-fill type algorithm. The image voxels are iterated, and each
 * time a foreground voxel not yet associated with a label is encountered, its
 * connected component is associated with a new label.
 * 
 * @see net.sci.image.morphology.FloodFill
 * @see FloodFillComponentsLabeling2D
 * 
 * @author dlegland
 */
public class FloodFillComponentsLabeling3D extends AlgoStub implements ComponentsLabeling
{
    // ==============================================================
    // Class variables

    /**
     * The connectivity of the components, either Connectivity3D.C6 (default) or
     * Connectivity3D.C26.
     */
    Connectivity3D connectivity = Connectivity3D.C6;

    /**
     * The factory of IntArray for creating new label maps.
     */
    IntArray.Factory<?> factory = UInt16Array.defaultFactory;

    
    // ==============================================================
    // Constructors

    /**
     * Constructor with default connectivity 6 and default output bitdepth equal
     * to 16.
     */
    public FloodFillComponentsLabeling3D()
    {
    }

    /**
     * Constructor specifying the connectivity and using default output bitdepth
     * equal to 16.
     * 
     * @param connectivity
     *            the connectivity of connected components (6 or 26)
     */
    public FloodFillComponentsLabeling3D(Connectivity3D connectivity)
    {
        this.connectivity = connectivity;

        // check validity of input argument
        if (connectivity != Connectivity3D.C6 && connectivity != Connectivity3D.C26)
        {
            throw new IllegalArgumentException("Connectivity must be either 6 or 26, not " + connectivity);
        }
    }
    
    /**
     * Constructor specifying the connectivity and the factory for creating new
     * empty label maps.
     * 
     * @param connectivity
     *            the connectivity of connected components (6 or 26)
     * @param labelMapFactory
     *            the factory used to create new label maps.
     */
    public FloodFillComponentsLabeling3D(Connectivity3D connectivity, IntArray.Factory<?> labelMapFactory)
    {
        this(connectivity);
        this.factory = labelMapFactory;
    }
    
	/**
     * Constructor specifying the connectivity and using default output bitdepth equal to 16.  
     * 
     * @param connectivity
     *            the connectivity of connected components (6 or 26)
     */
    public FloodFillComponentsLabeling3D(int connectivity)
    {
    	this.connectivity = Connectivity3D.fromValue(connectivity);
    
    	// check validity of input argument
    	if (connectivity != 6 && connectivity != 26)
    	{
    		throw new IllegalArgumentException("Connectivity must be either 6 or 26, not " + connectivity);
        }
    }
    

    // ==============================================================
    // Processing methods

    public IntArray3D<?> processBinary3d(BinaryArray3D array)
    {
        IntArray3D<?> labels = createEmptyLabelMap(array);
        processBinary3d(array, labels);
        return labels;
    }

    public int processBinary3d(BinaryArray3D image, IntArray3D<?> labelMap)
    {
        // get image size
        int sizeX = image.size(0);
        int sizeY = image.size(1);
        int sizeZ = image.size(2);

        // identify the maximum label index
        int maxLabel = labelMap.sampleElement().typeMax().intValue();

        fireStatusChanged(this, "Compute Labels...");

        // Iterate over image voxels.
        // Each time a white voxel not yet associated
        // with a label is encountered, uses flood-fill to associate its
        // connected component to a new label
        int nLabels = 0;
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // Do not process background voxels
                    if (!image.getBoolean(x, y, z)) continue;

                    // Do not process voxels already labeled
                    if (labelMap.getInt(x, y, z) > 0) continue;

                    // a new label is found: check current label number
                    if (nLabels == maxLabel)
                    {
                        throw new RuntimeException(
                                "Max number of label reached (" + maxLabel + ")");
                    }

                    // increment label index, and propagate
                    nLabels++;
                    FloodFill.floodFillInt(image, x, y, z, labelMap, nLabels, this.connectivity);
                }
            }
        }

        fireStatusChanged(this, "");
        fireProgressChanged(this, 1, 1);
        return nLabels;
    }

    public void process(Array<?> source, Array<?> target)
    {
        if (source instanceof BinaryArray3D && target instanceof IntArray3D)
        {
            processBinary3d((BinaryArray3D) source, (IntArray3D<?>) target);
        }
        else
        {
            throw new RuntimeException("Can not process input of class " + source.getClass()
                    + " with output of class " + target.getClass());
        }
    }

    
    // ==============================================================
    // Implementation of the ComponentsLabeling interface

    @Override
    public int processBinary(BinaryArray array, IntArray<?> labelMap)
    {
        if (array.dimensionality() != 3)
        {
            throw new IllegalArgumentException("Requires a BinaryArray of dimensionality 3");
        }
        if (labelMap.dimensionality() != 3)
        {
            throw new IllegalArgumentException("Requires a Label Map of dimensionality 3");
        }
        if (!Arrays.isSameSize(array, labelMap))
        {
            throw new IllegalArgumentException("Input and Output arrays must have same dimensions");
        }
        return processBinary3d(BinaryArray3D.wrap(array), IntArray3D.wrap(labelMap));
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
    public IntArray3D<?> createEmptyLabelMap(Array<?> array)
    {
        return IntArray3D.wrap(this.factory.create(array.size()));
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
        if (array.dimensionality() != 3)
        {
            throw new IllegalArgumentException("Requires a BinaryArray of dimensionality 3");
        }

        return processBinary3d(BinaryArray3D.wrap((BinaryArray) array));
    }

    @Override
    public boolean canProcess(Array<?> array)
    {
        if (!(array instanceof BinaryArray))
            return false;
        return array.dimensionality() == 3;
    }
}
