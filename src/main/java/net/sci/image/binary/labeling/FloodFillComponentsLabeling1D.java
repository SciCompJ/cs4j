/**
 * 
 */
package net.sci.image.binary.labeling;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray1D;
import net.sci.array.scalar.Int32Array;
import net.sci.array.scalar.Int32Array1D;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray1D;
import net.sci.array.scalar.ScalarArray1D;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt16Array1D;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array1D;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;
import net.sci.image.ImageType;

/**
 * Computes the labels of the connected components in a binary image. The type
 * of result is controlled by the bitDepth option.
 * 
 * Uses a Flood-fill type algorithm. The image pixels are iterated, and each
 * time a foreground pixel not yet associated with a label is encountered, its
 * connected component is associated with a new label.
 *
 * @see net.sci.image.morphology.FloodFill
 * 
 * @author dlegland
 *
 */
public class FloodFillComponentsLabeling1D extends AlgoStub implements ImageArrayOperator
{
    // ==============================================================
    // Class variables
    
	/**
	 * The number of bits for representing the result label image. Can be 8, 16
	 * (default), or 32.
	 */
	int bitDepth = 16;
	

	// ==============================================================
    // Constructors
    
	/**
	 * Constructor with default output bitdepth equal to 16.  
	 */
	public FloodFillComponentsLabeling1D()
	{
	}
	
	/**
     * Constructor specifying the bitdepth of result label image
     * 
     * @param bitDepth
     *            the bit depth of the result (8, 16, or 32)
     */
	public FloodFillComponentsLabeling1D(int bitDepth)
	{
		this.bitDepth = bitDepth;
		checkBitDepth();
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
    
	public IntArray1D<?> processBinary1d(BinaryArray1D image)
	{
		// get image size
		int sizeX = image.size(0);
		int maxLabel;

		// Depending on bitDepth, create result image, and choose max label 
		// number
		IntArray1D<?> labels;
		switch (this.bitDepth) {
		case 8: 
			labels = UInt8Array1D.create(sizeX);
			maxLabel = 255;
			break; 
		case 16: 
			labels = UInt16Array1D.create(sizeX);
			maxLabel = 65535;
			break;
		case 32:
			labels = Int32Array1D.create(sizeX);
			maxLabel = 0x01 << 31 - 1;
			break;
		default:
			throw new IllegalArgumentException(
					"Bit Depth should be 8, 16 or 32.");
		}

		// the label counter
		int nLabels = 0;

		// iterate on image pixels to find new regions
		for (int x = 0; x < sizeX; x++) 
		{
		    this.fireProgressChanged(this, x, sizeX);
		    if (!image.getBoolean(x))
		        continue;
		    if (labels.getInt(x) > 0)
		        continue;

		    // a new label is found: check current label number  
		    if (nLabels == maxLabel)
		    {
		        throw new RuntimeException("Max number of label reached (" + maxLabel + ")");
		    }

		    // increment label index, and propagate
		    nLabels++;
		    floodFillInt(image, x, labels, nLabels);
		}
		this.fireProgressChanged(this, 1, 1);

//		labels.setMinAndMax(0, nLabels);
		return labels;
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
		result.setType(ImageType.LABEL);
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
	    return new Image(result, ImageType.LABEL, image);
	}
	
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
