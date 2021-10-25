/**
 * 
 */
package net.sci.image.binary;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Int32Array;
import net.sci.array.scalar.Int32Array2D;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;
import net.sci.image.data.Connectivity2D;
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
 * @author dlegland
 *
 */
public class FloodFillComponentsLabeling2D extends AlgoStub implements ImageArrayOperator
{
    // ==============================================================
    // Class variables
    
	/** 
	 * The connectivity of the components, either 4 (default) or 8.
	 */
	Connectivity2D connectivity = Connectivity2D.C4;

	/**
	 * The number of bits for representing the result label image. Can be 8, 16
	 * (default), or 32.
	 */
	int bitDepth = 16;
	

	// ==============================================================
    // Constructors
    
	/**
	 * Constructor with default connectivity 4 and default output bitdepth equal to 16.  
	 */
	public FloodFillComponentsLabeling2D()
	{
	}
	
    /**
     * Constructor specifying the connectivity and using default output bitdepth equal to 16.  
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
     * Constructor specifying the connectivity and the bitdepth.  
     * 
     * @param connectivity
     *            the connectivity of connected components (4 or 8)
     * @param bitDepth
     *            the bit depth of the result (8, 16, or 32)
     */
    public FloodFillComponentsLabeling2D(Connectivity2D connectivity, int bitDepth)
    {
        this(connectivity);
        this.bitDepth = bitDepth;
        checkBitDepth();
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
	 * Constructor specifying the connectivity and the bitdepth of result label
	 * image
	 * 
	 * @param connectivity
	 *            the integer value for connectivity of connected components (4 or 8)
	 * @param bitDepth
	 *            the bit depth of the result (8, 16, or 32)
	 */
	public FloodFillComponentsLabeling2D(int connectivity, int bitDepth)
	{
		this(connectivity);
		this.bitDepth = bitDepth;

		checkBitDepth();
	}
	
	/**
	 * Throw an exception if connectivity is not 4 or 8 (necessary for FloodFill algorithms). 
	 */
	private void checkConnectivity()
	{
	    if (connectivity != Connectivity2D.C4 && connectivity != Connectivity2D.C8)
        {
            throw new IllegalArgumentException("Connectivity must be either 4 or 8, not " + connectivity);
        }
	}
  
    /**
     * Throw an exception if bit depth is different from 8, 16 or 32. 
     */
    private void checkBitDepth()
    {
        if (bitDepth != 8 && bitDepth != 16 && bitDepth != 32)
        {
            throw new IllegalArgumentException("Bit depth must be 8, 16 or 32, not " + bitDepth);
        }
    }

    // ==============================================================
    // Processing methods
    
	public IntArray2D<?> processBinary2d(BinaryArray2D image)
	{
		// get image size
		int sizeX = image.size(0);
		int sizeY = image.size(1);
		int maxLabel;

		// Depending on bitDepth, create result image, and choose max label 
		// number
		IntArray2D<?> labels;
		switch (this.bitDepth) {
		case 8: 
			labels = UInt8Array2D.create(sizeX, sizeY);
			maxLabel = 255;
			break; 
		case 16: 
			labels = UInt16Array2D.create(sizeX, sizeY);
			maxLabel = 65535;
			break;
		case 32:
			labels = Int32Array2D.create(sizeX, sizeY);
			maxLabel = 0x01 << 31 - 1;
			break;
		default:
			throw new IllegalArgumentException(
					"Bit Depth should be 8, 16 or 32.");
		}

		// the label counter
		int nLabels = 0;

		// iterate on image pixels to find new regions
		for (int y = 0; y < sizeY; y++) 
		{
			this.fireProgressChanged(this, y, sizeY);
			for (int x = 0; x < sizeX; x++) 
			{
				if (!image.getBoolean(x,y))
					continue;
				if (labels.getInt(x, y) > 0)
					continue;

				// a new label is found: check current label number  
				if (nLabels == maxLabel)
				{
					throw new RuntimeException("Max number of label reached (" + maxLabel + ")");
				}
				
				// increment label index, and propagate
				nLabels++;
				FloodFill.floodFill(image, x, y, labels, nLabels, this.connectivity);
			}
		}
		this.fireProgressChanged(this, 1, 1);

//		labels.setMinAndMax(0, nLabels);
		return labels;
	}
	
	public void processBinary2d(BinaryArray2D source, IntArray2D<?> target)
	{
		// get image size
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int maxLabel;

		// Depending on bitDepth, create result image, and choose max label 
		// number
		switch (this.bitDepth) {
		case 8: 
			maxLabel = 255;
			break; 
		case 16: 
			maxLabel = 65535;
			break;
		case 32:
			maxLabel = (0x01 << 31) - 1;
			break;
		default:
			throw new IllegalArgumentException(
					"Bit Depth should be 8, 16 or 32.");
		}

		// the label counter
		int nLabels = 0;

		// iterate on image pixels to find new regions
		for (int y = 0; y < sizeY; y++) 
		{
			this.fireProgressChanged(this, y, sizeY);
			for (int x = 0; x < sizeX; x++) 
			{
				if (!source.getBoolean(x,y))
					continue;
				if (target.getInt(x, y) > 0)
					continue;

				// a new label is found: check current label number  
				if (nLabels == maxLabel)
				{
					throw new RuntimeException("Max number of label reached (" + maxLabel + ")");
				}
				
				// increment label index, and propagate
				nLabels++;
				FloodFill.floodFillInt(source, x, y, target, nLabels, this.connectivity);
			}
		}
		this.fireProgressChanged(this, 1, 1);
	}

	/**
	 * Calls the "createEmptyOutputArray" methods from ArrayOperator interface
	 * for creating the result array, and put the result in a new label image.
	 * 
	 * @param image
	 *            the reference image
	 * @return a new instance of Image that can be used for processing input
	 *         image.
	 */
	public Image createEmptyOutputImage(Image image)
	{
		Array<?> array = image.getData();
		Array<?> newArray = createEmptyOutputArray(array);
		Image result = new Image(newArray, image);
		result.setType(Image.Type.LABEL);
		return result;
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
	public IntArray<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.size();
		switch (this.bitDepth) {
		case 8: 
			return UInt8Array.create(dims);
		case 16: 
			return UInt16Array.create(dims);
		case 32:
			return Int32Array.create(dims);
		default:
			throw new IllegalArgumentException(
					"Bit Depth should be 8, 16 or 32.");
		}
	}

	@Override
	public Image process(Image image)
	{
	    Array<?> result = process(image.getData());
	    return new Image(result, Image.Type.LABEL, image);
	}
	
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
        IntArray<?> result = createEmptyOutputArray(array);
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
