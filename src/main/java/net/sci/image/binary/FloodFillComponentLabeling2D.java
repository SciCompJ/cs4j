/**
 * 
 */
package net.sci.image.binary;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.data.Int32Array;
import net.sci.array.data.IntArray;
import net.sci.array.data.UInt16Array;
import net.sci.array.data.UInt8Array;
import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar2d.Int32Array2D;
import net.sci.array.data.scalar2d.IntArray2D;
import net.sci.array.data.scalar2d.UInt16Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.ArrayToArrayImageOperator;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.FloodFill2D;

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
public class FloodFillComponentLabeling2D extends AlgoStub implements ArrayToArrayImageOperator
{
	/** 
	 * The connectivity of the components, either 4 (default) or 8.
	 */
	Connectivity2D connectivity = Connectivity2D.C4;

	/**
	 * The number of bits for representing the result label image. Can be 8, 16
	 * (default), or 32.
	 */
	int bitDepth = 16;
	
	/**
	 * Constructor with default connectivity 4 and default output bitdepth equal to 16.  
	 */
	public FloodFillComponentLabeling2D()
	{
	}
	
	/**
	 * Constructor specifying the connectivity and using default output bitdepth equal to 16.  
	 * 
	 * @param connectivity
	 *            the connectivity of connected components (4 or 8)
	 */
	public FloodFillComponentLabeling2D(Connectivity2D connectivity)
	{
		this.connectivity = connectivity;

		// check validity of input argument
		if (connectivity != Connectivity2D.C4 && connectivity != Connectivity2D.C8)
		{
			throw new IllegalArgumentException("Connectivity must be either 4 or 8, not " + connectivity);
		}
	}
	
	/**
	 * Constructor specifying the connectivity and using default output bitdepth equal to 16.  
	 * 
	 * @param connectivity
	 *            the connectivity of connected components (4 or 8)
	 */
	public FloodFillComponentLabeling2D(int connectivity)
	{
		this.connectivity = Connectivity2D.fromValue(connectivity);

		// check validity of input argument
		if (connectivity != 4 && connectivity != 8)
		{
			throw new IllegalArgumentException("Connectivity must be either 4 or 8, not " + connectivity);
		}
	}
	
	/**
	 * Constructor specifying the connectivity and the bitdepth of result label
	 * image
	 * 
	 * @param connectivity
	 *            the connectivity of connected components (4 or 8)
	 * @param bitDepth
	 *            the bit depth of the result (8, 16, or 32)
	 */
	public FloodFillComponentLabeling2D(int connectivity, int bitDepth)
	{
		this(connectivity);
		this.bitDepth = bitDepth;

		// check validity of input argument
		if (bitDepth != 8 && bitDepth != 16 && bitDepth != 32)
		{
			throw new IllegalArgumentException("Bit depth must be 8, 16 or 32, not " + bitDepth);
		}
	}
	

	public IntArray2D<?> process(BooleanArray2D image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);
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
				if (!image.getState(x,y))
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
				FloodFill2D.floodFillFloat(image, x, y, labels, nLabels, this.connectivity);
			}
		}
		this.fireProgressChanged(this, 1, 1);

//		labels.setMinAndMax(0, nLabels);
		return labels;
	}

	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (source instanceof BooleanArray2D && target instanceof IntArray2D)
		{
			process2d((BooleanArray2D) source, (IntArray2D<?>) target);
		}
		else
		{
			throw new RuntimeException("Can not process input of class " + source.getClass() + " with output of class " + target.getClass());
		}
	}
	
	public void process2d(BooleanArray2D source, IntArray2D<?> target)
	{
		// get image size
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
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
				if (!source.getState(x,y))
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
				FloodFill2D.floodFillFloat(source, x, y, target, nLabels, this.connectivity);
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
	@Override
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
		int[] dims = array.getSize();
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
}
