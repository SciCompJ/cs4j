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
import net.sci.array.data.scalar3d.BooleanArray3D;
import net.sci.array.data.scalar3d.IntArray3D;
import net.sci.image.Image;
import net.sci.image.ImageArrayToArrayOperator;
import net.sci.image.data.Connectivity3D;
import net.sci.image.morphology.FloodFill3D;

/**
 * Computes the labels of the connected components in a 3D binary image. The
 * type of result is controlled by the bitDepth option.
 * 
 * Uses a Flood-fill type algorithm. The image voxels are iterated, and each
 * time a foreground voxel not yet associated with a label is encountered, its
 * connected component is associated with a new label.
 * 
 * @see inra.ijpb.morphology.FloodFill3D
 * 
 * @author dlegland
 */
public class FloodFillComponentsLabeling3D extends AlgoStub implements ImageArrayToArrayOperator
{
	/** 
	 * The connectivity of the components, either Connectivity3D.C6 (default) or Connectivity3D.C26.
	 */
	Connectivity3D connectivity = Connectivity3D.C6;
	
	/**
	 * The number of bits for representing the result label image. Can be 8, 16
	 * (default), or 32.
	 */
	int bitDepth = 16;
	
	/**
	 * Constructor with default connectivity 6 and default output bitdepth equal to 16.  
	 */
	public FloodFillComponentsLabeling3D()
	{
	}
	
	/**
	 * Constructor specifying the connectivity and using default output bitdepth equal to 16.  
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
	
	/**
	 * Constructor specifying the connectivity and the bitdepth of result label
	 * image
	 * 
	 * @param connectivity
	 *            the connectivity of connected components (6 or 26)
	 * @param bitDepth
	 *            the bit depth of the result (8, 16, or 32)
	 */
	public FloodFillComponentsLabeling3D(Connectivity3D connectivity, int bitDepth)
	{
		this(connectivity);
		this.bitDepth = bitDepth;

		// check validity of input argument
		if (bitDepth != 8 && bitDepth != 16 && bitDepth != 32)
		{
			throw new IllegalArgumentException("Bit depth must be 8, 16 or 32, not " + bitDepth);
		}
	}
	
	/**
	 * Constructor specifying the connectivity and the bitdepth of result label
	 * image
	 * 
	 * @param connectivity
	 *            the connectivity of connected components (6 or 26)
	 * @param bitDepth
	 *            the bit depth of the result (8, 16, or 32)
	 */
	public FloodFillComponentsLabeling3D(int connectivity, int bitDepth)
	{
		this(connectivity);
		this.bitDepth = bitDepth;

		// check validity of input argument
		if (bitDepth != 8 && bitDepth != 16 && bitDepth != 32)
		{
			throw new IllegalArgumentException("Bit depth must be 8, 16 or 32, not " + bitDepth);
		}
	}
	
	/* (non-Javadoc)
	 * @see inra.ijpb.binary.conncomp.ConnectedComponentsLabeling3D#computeLabels(ij.ImageStack)
	 */
	public void process3d(BooleanArray3D image, IntArray3D<?> labels)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);
		int sizeZ = image.getSize(2);
	
		// identify the maximum label index
		int maxLabel;
		switch (this.bitDepth) {
		case 8: 
			maxLabel = 255;
			break; 
		case 16: 
			maxLabel = 65535;
			break;
		case 32:
			maxLabel = 0x01 << 23;
			break;
		default:
			throw new IllegalArgumentException(
					"Bit Depth should be 8, 16 or 32.");
		}
	
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
					if (!image.getState(x, y, z))
						continue;
	
					// Do not process voxels already labeled
					if (labels.getInt(x, y, z) > 0)
						continue;
	
					// a new label is found: check current label number  
					if (nLabels == maxLabel)
					{
						throw new RuntimeException("Max number of label reached (" + maxLabel + ")");
					}
					
					// increment label index, and propagate
					nLabels++;
					FloodFill3D.floodFillInt(image, x, y, z, labels, nLabels, this.connectivity);
				}
			}
		}
		
		fireStatusChanged(this, "");
		fireProgressChanged(this, 1, 1);
	}

	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (source instanceof BooleanArray3D && target instanceof IntArray3D)
		{
			process3d((BooleanArray3D) source, (IntArray3D<?>) target);
		}
		else
		{
			throw new RuntimeException("Can not process input of class " + source.getClass() + " with output of class " + target.getClass());
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
	 * @return a new instance of Array<?> that can be used for processing input
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