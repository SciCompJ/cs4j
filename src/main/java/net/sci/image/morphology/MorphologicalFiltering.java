/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.scalar.UInt8Array;

/**
 * <p>
 * Collection of static methods for morphological filters,
 * as well as an enumeration of available methods.
 * </p>
 * 
 * <p>
 * Example of use:
 * <pre>
 * {@code
 * Array2D<?> image = IJ.getImage().getProcessor();
 * Strel se = SquareStrel.fromDiameter(5);
 * Array2D<?> grad = Morphology.gradient(image, se);
 * ImagePlus res = new ImagePlus("Gradient", grad);
 * res.show(); 
 *  }</pre>
 *
 * <p>
 * Example of use with 3D image (stack):
 * <pre>
 * {@code
 * ImageStack image = IJ.getImage().getStack();
 * Strel3D se = CubeStrel.fromDiameter(3);
 * ImageStack grad = Morphology.gradient(image, se);
 * ImagePlus res = new ImagePlus("Gradient3D", grad);
 * res.show(); 
 * }</pre>
 * @author David Legland
 *
 */
public class MorphologicalFiltering 
{
	// =======================================================================
	// Enumeration for operations
	
	/**
	 * A pre-defined set of basis morphological operations, that can be easily 
	 * used with a GenericDialog. 
	 * Example:
	 * <pre>
	 * {@code
 	 * // Use a generic dialog to define an operator 
	 * GenericDialog gd = new GenericDialog();
	 * gd.addChoice("Operation", Operation.getAllLabels();
	 * gd.showDialog();
	 * Operation op = Operation.fromLabel(gd.getNextChoice());
	 * // Apply the operation on the current image
	 * Array2D<?> image = IJ.getImage().getProcessor();
	 * op.apply(image, SquareStrel.fromRadius(2));
	 * }</pre>
	 */
	public enum Operation 
	{
		/** Morphological erosion (local minima)*/
		EROSION("Erosion"),
		/** Morphological dilation (local maxima)*/
		DILATION("Dilation"),
		/** Morphological opening (erosion followed by dilation)*/
		OPENING("Opening"),
		/** Morphological closing (dilation followed by erosion)*/
		CLOSING("Closing"), 
		/** White Top-Hat */
		TOPHAT("White Top Hat"),
		/** Black Top-Hat */
		BOTTOMHAT("Black Top Hat"),
		/** Morphological gradient (difference of dilation with erosion) */
		GRADIENT("Gradient"), 
		/** Morphological laplacian (difference of external gradient with internal gradient) */
		LAPLACIAN("Laplacian"), 
		/** Morphological internal gradient (difference of dilation with original image) */
		INTERNAL_GRADIENT("Internal Gradient"), 
		/** Morphological internal gradient (difference of original image with erosion) */
		EXTERNAL_GRADIENT("External Gradient");
		
		private final String label;
		
		private Operation(String label) 
		{
			this.label = label;
		}
		
		/**
		 * Applies the current operator to the input image.
		 * 
		 * @param image
		 *            the image to process
		 * @param strel
		 *            the structuring element to use
		 * @return the result of morphological operation applied to image
		 */
		public Array2D<?> apply(Array2D<?> image, Strel2D strel) 
		{
			if (this == DILATION)
				return dilation(image, strel);
			if (this == EROSION)
				return erosion(image, strel);
			if (this == CLOSING)
				return closing(image, strel);
			if (this == OPENING)
				return opening(image, strel);
			if (this == TOPHAT)
				return whiteTopHat(image, strel);
			if (this == BOTTOMHAT)
				return blackTopHat(image, strel);
			if (this == GRADIENT)
				return gradient(image, strel);
			if (this == LAPLACIAN)
				return laplacian(image, strel);
			if (this == INTERNAL_GRADIENT)
				return internalGradient(image, strel);
			if (this == EXTERNAL_GRADIENT)
				return externalGradient(image, strel);
			
			throw new RuntimeException(
					"Unable to process the " + this + " morphological operation");
		}
		
//		/**
//		 * Applies the current operator to the input 3D image.
//		 * 
//		 * @param image
//		 *            the image to process
//		 * @param strel
//		 *            the structuring element to use
//		 * @return the result of morphological operation applied to image
//		 */
//		public ImageStack apply(ImageStack image, Strel3D strel)
//		{
//			if (this == DILATION)
//				return dilation(image, strel);
//			if (this == EROSION)
//				return erosion(image, strel);
//			if (this == CLOSING)
//				return closing(image, strel);
//			if (this == OPENING)
//				return opening(image, strel);
//			if (this == TOPHAT)
//				return whiteTopHat(image, strel);
//			if (this == BOTTOMHAT)
//				return blackTopHat(image, strel);
//			if (this == GRADIENT)
//				return gradient(image, strel);
//			if (this == LAPLACIAN)
//				return laplacian(image, strel);
//			if (this == INTERNAL_GRADIENT)
//				return internalGradient(image, strel);
//			if (this == EXTERNAL_GRADIENT)
//				return externalGradient(image, strel);
//			
//			throw new RuntimeException(
//					"Unable to process the " + this + " morphological operation");
//		}
		
		public String toString() 
		{
			return this.label;
		}
		
		public static String[] getAllLabels()
		{
			int n = Operation.values().length;
			String[] result = new String[n];
			
			int i = 0;
			for (Operation op : Operation.values())
				result[i++] = op.label;
			
			return result;
		}
		
		/**
		 * Determines the operation type from its label.
		 * 
		 * @param opLabel
		 *            the label of the operation
		 * @return the parsed Operation
		 * @throws IllegalArgumentException
		 *             if label is not recognized.
		 */
		public static Operation fromLabel(String opLabel)
		{
			if (opLabel != null)
				opLabel = opLabel.toLowerCase();
			for (Operation op : Operation.values()) 
			{
				String cmp = op.label.toLowerCase();
				if (cmp.equals(opLabel))
					return op;
			}
			throw new IllegalArgumentException("Unable to parse Operation with label: " + opLabel);
		}
	};
	
	/**
	 * Makes the default constructor private to avoid creation of instances.
	 */
	private MorphologicalFiltering() 
	{
	}

	
	// =======================================================================
	// Main morphological operations
	
	/**
	 * Performs morphological dilation on the input image.
	 * 
	 * Dilation is obtained by extracting the maximum value among pixels in the
	 * neighborhood given by the structuring element.
	 * 
	 * This methods is mainly a wrapper to the dilation method of the strel
	 * object.
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for dilation
	 * @return the result of the dilation
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see Strel2D#dilation(Array2D)
	 */
	public static Array2D<?> dilation(Array2D<?> image, Strel2D strel)
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return dilationRGB(image, strel);
		return strel.dilation(image);
	}

//	/**
//	 * Performs morphological dilation on each channel, and reconstitutes the
//	 * resulting color image.
//	 * 
//	 * @param image
//	 *            the input RGB image
//	 * @param strel
//	 *            the structuring element used for dilation
//	 * @return the result of the dilation
//	 */
//	private static Array2D<?> dilationRGB(Array2D<?> image, Strel strel) 
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"}) 
//		{
//			strel.setChannelName(name);
//			res.add(strel.dilation(channels.get(name)));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}

//	/**
//	 * Performs morphological dilation on the input 3D image.
//	 * 
//	 * Dilation is obtained by extracting the maximum value among voxels in the
//	 * neighborhood given by the 3D structuring element.
//	 * 
//	 * @param image
//	 *            the input 3D image to process (grayscale or RGB)
//	 * @param strel
//	 *            the structuring element used for dilation
//	 * @return the result of the dilation
//	 */
//	public static ImageStack dilation(ImageStack image, Strel3D strel)
//	{
//		checkImageType(image);
//		return strel.dilation(image);
//	}
	
	/**
	 * Performs morphological erosion on the input image. Erosion is obtained by
	 * extracting the minimum value among pixels in the neighborhood given by
	 * the structuring element.
	 * 
	 * This methods is mainly a wrapper to the erosion method of the strel
	 * object.
	 * 
	 * @see #dilation(Array2D, Strel2D)
	 * @see Strel2D#erosion(Array2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for erosion
	 * @return the result of the erosion
	 */
	public static Array2D<?> erosion(Array2D<?> image, Strel2D strel)
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return erosionRGB(image, strel);

		return strel.erosion(image);
	}

//	/**
//	 * Performs morphological erosion on each channel, and reconstitutes the
//	 * resulting color image.
//	 * 
//	 * @param image
//	 *            the input image to process (RGB)
//	 * @param strel
//	 *            the structuring element used for erosion
//	 * @return the result of the erosion
//	 */
//	private static Array2D<?> erosionRGB(Array2D<?> image, Strel strel)
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"}) 
//		{
//			strel.setChannelName(name);
//			res.add(strel.erosion(channels.get(name)));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}
	
//	/**
//	 * Performs morphological erosion on the input 3D image.
//	 * 
//	 * Erosion is obtained by extracting the minimum value among voxels in the
//	 * neighborhood given by the 3D structuring element.
//	 * 
//	 * @param image
//	 *            the input image to process (grayscale or RGB)
//	 * @param strel
//	 *            the structuring element used for erosion
//	 * @return the result of the erosion
//	 */
//	public static ImageStack erosion(ImageStack image, Strel3D strel) 
//	{
//		checkImageType(image);
//		return strel.erosion(image);
//	}

	/**
	 * Performs morphological opening on the input image.
	 * 
	 * The opening is obtained by performing an erosion followed by an dilation
	 * with the reversed structuring element.
	 * 
	 * This methods is mainly a wrapper to the opening method of the strel object.
	 * 
	 * @see #closing(Array2D, Strel2D)
	 * @see Strel2D#opening(Array2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for opening
	 * @return the result of the morphological opening
	 */
	public static Array2D<?> opening(Array2D<?> image, Strel2D strel)
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return openingRGB(image, strel);

		return strel.opening(image);
	}

//	/**
//	 * Performs morphological opening on each channel, and reconstitutes the
//	 * resulting color image.
//	 */
//	private static Array2D<?> openingRGB(Array2D<?> image, Strel strel)
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"}) 
//		{
//			strel.setChannelName(name);
//			res.add(strel.opening(channels.get(name)));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}
	
//	/**
//	 * Performs morphological opening on the input 3D image.
//	 * 
//	 * The 3D opening is obtained by performing a 3D erosion followed by a 3D
//	 * dilation with the reversed structuring element.
//	 * 
//	 * @see #closing(ImageStack, Strel3D)
//	 * @see Strel#opening(ImageStack)
//	 * 
//	 * @param image
//	 *            the input 3D image to process
//	 * @param strel
//	 *            the structuring element used for opening
//	 * @return the result of the 3D morphological opening
//	 */
//	public static ImageStack opening(ImageStack image, Strel3D strel) 
//	{
//		checkImageType(image);
//		return strel.opening(image);
//	}


	/**
	 * Performs closing on the input image.
	 * The closing is obtained by performing a dilation followed by an erosion
	 * with the reversed structuring element.
	 *  
	 * This methods is mainly a wrapper to the opening method of the strel object.
	 * @see #opening(Array2D, Strel2D)
	 * @see Strel2D#closing(Array2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for closing
	 * @return the result of the morphological closing
	 */
	public static Array2D<?> closing(Array2D<?> image, Strel2D strel) 
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return closingRGB(image, strel);

		return strel.closing(image);
	}

//	/**
//	 * Performs morphological closing on each channel, and reconstitutes the
//	 * resulting color image.
//	 */
//	private static Array2D<?> closingRGB(Array2D<?> image, Strel strel)
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"})
//		{
//			strel.setChannelName(name);
//			res.add(strel.closing(channels.get(name)));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}
	
//	/**
//	 * Performs morphological closing on the input 3D image.
//	 * 
//	 * The 3D closing is obtained by performing a 3D dilation followed by a 3D
//	 * erosion with the reversed structuring element.
//	 * 
//	 * @see #opening(ImageStack, Strel3D)
//	 * @see Strel#opening(ImageStack)
//	 * 
//	 * @param image
//	 *            the input 3D image to process
//	 * @param strel
//	 *            the structuring element used for closing
//	 * @return the result of the 3D morphological closing
//	 */
//	public static ImageStack closing(ImageStack image, Strel3D strel) 
//	{
//		checkImageType(image);
//		return strel.closing(image);
//	}


	/**
	 * Computes white top hat of the original image.
	 * The white top hat is obtained by subtracting the result of an opening 
	 * from the original image.
	 *  
	 * The white top hat enhances light structures smaller than the structuring element.
	 * 
	 * @see #blackTopHat(Array2D, Strel2D)
	 * @see #opening(Array2D, Strel2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for white top-hat
	 * @return the result of the white top-hat
	 */
	public static Array2D<?> whiteTopHat(Array2D<?> image, Strel2D strel) 
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return whiteTopHatRGB(image, strel);

		// First performs closing
		Array2D<?> result = strel.opening(image);
		
		// Compute subtraction of result from original image
		Array.Iterator<?> iter1 = image.iterator();
		Array.Iterator<?> iter2 = result.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			double val = iter1.nextValue() - iter2.nextValue();
			iter2.setValue(val);
		}

		return result;
	}
	
//	/**
//	 * Performs morphological closing on each channel, and reconstitutes the
//	 * resulting color image.
//	 */
//	private static Array2D<?> whiteTopHatRGB(Array2D<?> image, Strel strel) 
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"}) 
//		{
//			strel.setChannelName(name);
//			res.add(whiteTopHat(channels.get(name), strel));
//		}
//
//		// create new color image
//		return RGB8Array.mergeChannels(res);
//	}
	
//	/**
//	 * Computes 3D white top hat of the original image.
//	 * 
//	 * The white top hat is obtained by subtracting the result of an opening 
//	 * from the original image.
//	 *  
//	 * The white top hat enhances light structures smaller than the structuring element.
//	 * 
//	 * @see #blackTopHat(ImageStack, Strel3D)
//	 * @see #opening(ImageStack, Strel3D)
//	 * 
//	 * @param image
//	 *            the input 3D image to process 
//	 * @param strel
//	 *            the structuring element used for white top-hat
//	 * @return the result of the 3D white top-hat
//	 */
//	public static ImageStack whiteTopHat(ImageStack image, Strel3D strel)
//	{
//		checkImageType(image);
//		
//		// First performs opening
//		ImageStack result = strel.opening(image);
//		
//		// compute max possible value
//		double maxVal = getMaxPossibleValue(image);
//		
//		// Compute subtraction of result from original image
//		int nx = image.getWidth();
//		int ny = image.getHeight();
//		int nz = image.getSize();
//		for (int z = 0; z < nz; z++)
//		{
//			for (int y = 0; y < ny; y++)
//			{
//				for (int x = 0; x < nx; x++) 
//				{
//					double v1 = image.getVoxel(x, y, z);
//					double v2 = result.getVoxel(x, y, z);
//					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
//				}
//			}
//		}
//		
//		return result;
//	}

	/**
	 * Computes black top hat (or "bottom hat") of the original image.
	 * The black top hat is obtained by subtracting the original image from
	 * the result of a closing.
	 *  
	 * The black top hat enhances dark structures smaller than the structuring element.
	 * 
	 * @see #whiteTopHat(Array2D, Strel2D)
	 * @see #closing(Array2D, Strel2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for black top-hat
	 * @return the result of the black top-hat
	 */
	public static Array2D<?> blackTopHat(Array2D<?> image, Strel2D strel)
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return blackTopHatRGB(image, strel);

		// First performs closing
		Array2D<?> result = strel.closing(image);
		
		// Compute subtraction of result from original image
		Array.Iterator<?> iter1 = image.iterator();
		Array.Iterator<?> iter2 = result.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			double val = iter2.nextValue() - iter1.nextValue();
			iter2.setValue(val);
		}

		return result;
	}
	
//	/**
//	 * Performs morphological black top hat on each channel, and reconstitutes
//	 * the resulting color image.
//	 */
//	private static Array2D<?> blackTopHatRGB(Array2D<?> image, Strel strel)
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"})
//		{
//			strel.setChannelName(name);
//			res.add(blackTopHat(channels.get(name), strel));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}
	
//	/**
//	 * Computes black top hat (or "bottom hat") of the original image.
//	 * The black top hat is obtained by subtracting the original image from
//	 * the result of a closing.
//	 *  
//	 * The black top hat enhances dark structures smaller than the structuring element.
//	 * 
//	 * @see #whiteTopHat(ImageStack, Strel3D)
//	 * @see #closing(ImageStack, Strel3D)
//	 * 
//	 * @param image
//	 *            the input 3D image to process
//	 * @param strel
//	 *            the structuring element used for black top-hat
//	 * @return the result of the 3D black top-hat
//	 */
//	public static ImageStack blackTopHat(ImageStack image, Strel3D strel)
//	{
//		checkImageType(image);
//		
//		// First performs closing
//		ImageStack result = strel.closing(image);
//		
//		// Compute subtraction of result from original image
//		int nx = image.getWidth();
//		int ny = image.getHeight();
//		int nz = image.getSize();
//		for (int z = 0; z < nz; z++) {
//			for (int y = 0; y < ny; y++) {
//				for (int x = 0; x < nx; x++) {
//					double v1 = result.getVoxel(x, y, z);
//					double v2 = image.getVoxel(x, y, z);
//					result.setVoxel(x, y, z, min(max(v1 - v2, 0), 255));
//				}
//			}
//		}
//		
//		return result;
//	}

	
	/**
	 * Computes the morphological gradient of the input image.
	 * The morphological gradient is obtained by from the difference of image 
	 * dilation and image erosion computed with the same structuring element. 
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see #dilation(Array2D, Strel2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological gradient
	 * @return the result of the morphological gradient
	 */
	public static Array2D<?> gradient(Array2D<?> image, Strel2D strel)
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return gradientRGB(image, strel);

		// First performs dilation and erosion
		Array2D<?> result = strel.dilation(image);
		Array2D<?> eroded = strel.erosion(image);

		// Compute subtraction of result from original image
		Array.Iterator<?> iter1 = result.iterator();
		Array.Iterator<?> iter2 = eroded.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			double val = iter1.nextValue() - iter2.nextValue();
			iter1.setValue(val);
		}

		// free memory
		eroded = null;
		
		// return gradient
		return result;
	}

//	/**
//	 * Performs morphological gradient on each channel, and reconstitutes
//	 * the resulting color image.
//	 */
//	private static Array2D<?> gradientRGB(Array2D<?> image, Strel strel)
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"})
//		{
//			strel.setChannelName(name);
//			res.add(gradient(channels.get(name), strel));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}

//	/**
//	 * Computes the morphological gradient of the input 3D image.
//	 * The morphological gradient is obtained by from the difference of image 
//	 * dilation and image erosion computed with the same structuring element. 
//	 * 
//	 * @see #erosion(ImageStack, Strel3D)
//	 * @see #dilation(ImageStack, Strel3D)
//	 * 
//	 * @param image
//	 *            the input 3D image to process
//	 * @param strel
//	 *            the structuring element used for morphological gradient
//	 * @return the result of the 3D morphological gradient
//	 */
//	public static ImageStack gradient(ImageStack image, Strel3D strel)
//	{
//		checkImageType(image);
//		
//		// First performs dilation and erosion
//		ImageStack result = strel.dilation(image);
//		ImageStack eroded = strel.erosion(image);
//		
//		// Determine max possible value from bit depth
//		double maxVal = getMaxPossibleValue(image);
//
//		// Compute subtraction of result from original image
//		int nx = image.getWidth();
//		int ny = image.getHeight();
//		int nz = image.getSize();
//		for (int z = 0; z < nz; z++) 
//		{
//			for (int y = 0; y < ny; y++) 
//			{
//				for (int x = 0; x < nx; x++) 
//				{
//					double v1 = result.getVoxel(x, y, z);
//					double v2 = eroded.getVoxel(x, y, z);
//					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
//				}
//			}
//		}
//		
//		return result;
//	}


	/**
	 * Computes the morphological Laplacian of the input image. The
	 * morphological gradient is obtained from the difference of the external
	 * gradient with the internal gradient, both computed with the same
	 * structuring element.
	 * 
	 * Homogeneous regions appear as gray.
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see #dilation(Array2D, Strel2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological laplacian
	 * @return the result of the morphological laplacian
	 */
	public static Array2D<?> laplacian(Array2D<?> image, Strel2D strel) 
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return laplacianRGB(image, strel);

		// First performs dilation and erosion
		Array2D<?> outer = externalGradient(image, strel);
		Array2D<?> inner = internalGradient(image, strel);
		
		// Subtract inner gradient from outer gradient
		Array2D<?> result = image.duplicate();

		double shift = 0;
		if (image instanceof UInt8Array)
		{
			shift = 128;
		}
		
		// Compute subtraction of result from original image
		Array.Iterator<?> iter1 = outer.iterator();
		Array.Iterator<?> iter2 = inner.iterator();
		Array.Iterator<?> resultIter = result.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			double val = iter1.nextValue() - iter2.nextValue();
			resultIter.forward();
			resultIter.setValue(val + shift);
		}

		// free memory
		outer = null;
		inner = null;
		
		// return gradient
		return result;
	}

//	/**
//	 * Performs morphological Laplacian on each channel, and reconstitutes
//	 * the resulting color image.
//	 * 
//	 * Homogeneous regions appear as gray.
//	 */
//	private static Array2D<?> laplacianRGB(Array2D<?> image, Strel strel) 
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"}) 
//		{
//			strel.setChannelName(name);
//			res.add(laplacian(channels.get(name), strel));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}

//	/**
//	 * Computes the morphological Laplacian of the 3D input image. The
//	 * morphological gradient is obtained from the difference of the external
//	 * gradient with the internal gradient, both computed with the same
//	 * structuring element.
//	 * 
//	 * Homogeneous regions appear as gray.
//	 * 
//	 * @see #externalGradient(ImageStack, Strel3D)
//	 * @see #internalGradient(ImageStack, Strel3D)
//	 * 
//	 * @param image
//	 *            the input 3D image to process 
//	 * @param strel
//	 *            the structuring element used for morphological laplacian
//	 * @return the result of the 3D morphological laplacian
//	 */
//	public static ImageStack laplacian(ImageStack image, Strel3D strel)
//	{
//		checkImageType(image);
//		
//		// First performs dilation and erosion
//		ImageStack outer = externalGradient(image, strel);
//		ImageStack inner = internalGradient(image, strel);
//		
//		// Determine max possible value from bit depth
//		double maxVal = getMaxPossibleValue(image);
//		double midVal = maxVal / 2;
//		
//		// Compute subtraction of result from original image
//		int nx = image.getWidth();
//		int ny = image.getHeight();
//		int nz = image.getSize();
//		for (int z = 0; z < nz; z++) 
//		{
//			for (int y = 0; y < ny; y++)
//			{
//				for (int x = 0; x < nx; x++)
//				{
//					double v1 = outer.getVoxel(x, y, z);
//					double v2 = inner.getVoxel(x, y, z);
//					outer.setVoxel(x, y, z, min(max(v1 - v2 + midVal, 0), maxVal));
//				}
//			}
//		}
//		
//		return outer;
//	}

	/** 
	 * Computes the morphological internal gradient of the input image.
	 * The morphological internal gradient is obtained by from the difference 
	 * of original image with the result of an erosion.
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see #gradient(Array2D, Strel2D)
	 * @see #externalGradient(Array2D, Strel2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological internal gradient
	 * @return the result of the morphological internal gradient
	 */
	public static Array2D<?> internalGradient(Array2D<?> image, Strel2D strel) 
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return internalGradientRGB(image, strel);

		// First performs erosion
		Array2D<?> result = strel.erosion(image);

		// Subtract erosion result from original image
		Array.Iterator<?> iter1 = image.iterator();
		Array.Iterator<?> iter2 = result.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			double val = iter1.nextValue() - iter2.nextValue();
			iter2.setValue(val);
		}

		// return gradient
		return result;
	}

//	private static Array2D<?> internalGradientRGB(Array2D<?> image, Strel strel) 
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"})
//		{
//			strel.setChannelName(name);
//			res.add(internalGradient(channels.get(name), strel));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}

//	/** 
//	 * Computes the morphological internal gradient of the 3D input image.
//	 * The morphological internal gradient is obtained by from the difference 
//	 * of original image with the result of an erosion.
//	 * 
//	 * @see #erosion(ImageStack, Strel3D)
//	 * @see #gradient(ImageStack, Strel3D)
//	 * @see #externalGradient(ImageStack, Strel3D)
//	 * 
//	 * @param image
//	 *            the input image to process
//	 * @param strel
//	 *            the structuring element used for morphological internal gradient
//	 * @return the result of the 3D morphological internal gradient
//	 */
//	public static ImageStack internalGradient(ImageStack image, Strel3D strel)
//	{
//		checkImageType(image);
//		
//		// First performs erosion
//		ImageStack result = strel.erosion(image);
//		
//		// Determine max possible value from bit depth
//		double maxVal = getMaxPossibleValue(image);
//
//		// Compute subtraction of result from original image
//		int nx = image.getWidth();
//		int ny = image.getHeight();
//		int nz = image.getSize();
//		for (int z = 0; z < nz; z++) 
//		{
//			for (int y = 0; y < ny; y++) 
//			{
//				for (int x = 0; x < nx; x++) 
//				{
//					double v1 = image.getVoxel(x, y, z);
//					double v2 = result.getVoxel(x, y, z);
//					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
//				}
//			}
//		}
//		
//		return result;
//	}

	/** 
	 * Computes the morphological external gradient of the input image.
	 * The morphological external gradient is obtained by from the difference 
	 * of the result of a dilation and of the original image .
	 * 
	 * @see #dilation(Array2D, Strel2D)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological external gradient
	 * @return the result of the morphological external gradient
	 */
	public static Array2D<?> externalGradient(Array2D<?> image, Strel2D strel) 
	{
		checkImageType(image);
//		if (image instanceof RGB8Array2D)
//			return externalGradientRGB(image, strel);

		// First performs dilation
		Array2D<?> result = strel.dilation(image);

		// Subtract erosion result from original image
		Array.Iterator<?> iter1 = result.iterator();
		Array.Iterator<?> iter2 = image.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			double val = iter1.nextValue() - iter2.nextValue();
			iter1.setValue(val);
		}

		// return gradient
		return result;
	}

//	private static Array2D<?> externalGradientRGB(Array2D<?> image, Strel strel)
//	{
//		// extract channels and allocate memory for result
//		Map<String, UInt8Array2D> channels = RGB8Array.mapChannels(image);
//		Collection<Array2D<?>> res = new ArrayList<Array2D<?>>(channels.size());
//		
//		// Process each channel individually
//		for (String name : new String[]{"red", "green", "blue"}) 
//		{
//			strel.setChannelName(name);
//			res.add(externalGradient(channels.get(name), strel));
//		}
//		
//		return RGB8Array.mergeChannels(res);
//	}

//	/** 
//	 * Computes the morphological external gradient of the input 3D image.
//	 * The morphological external gradient is obtained by from the difference 
//	 * of the result of a dilation and of the original image .
//	 * 
//	 * @see #dilation(ImageStack, Strel3D)
//	 * @see #internalGradient(ImageStack, Strel3D)
//	 * 
//	 * @param image
//	 *            the input image to process 
//	 * @param strel
//	 *            the structuring element used for morphological external gradient
//	 * @return the result of the 3D morphological external gradient
//	 */
//	public static ImageStack externalGradient(ImageStack image, Strel3D strel) 
//	{
//		checkImageType(image);
//		
//		// First performs dilation
//		ImageStack result = strel.dilation(image);
//		
//		// Determine max possible value from bit depth
//		double maxVal = getMaxPossibleValue(image);
//		
//		// Compute subtraction of result from original image
//		int nx = image.getWidth();
//		int ny = image.getHeight();
//		int nz = image.getSize();
//		for (int z = 0; z < nz; z++)
//		{
//			for (int y = 0; y < ny; y++) 
//			{
//				for (int x = 0; x < nx; x++)
//				{
//					double v1 = result.getVoxel(x, y, z);
//					double v2 = image.getVoxel(x, y, z);
//					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
//				}
//			}
//		}
//		
//		return result;
//	}

	// =======================================================================
	// Private utilitary functions
	
	/**
	 * Check that input image can be processed for classical algorithms, and throw an
	 * exception if not the case.
	 * In the current version, accepts all image types.
	 */
	private final static void checkImageType(Array2D<?> image)
	{
//		if ((image instanceof FloatProcessor)
//				|| (image instanceof ShortProcessor)) {
//			throw new IllegalArgumentException(
//					"Input image must be a UInt8Array2D or a RGB8Array2D");
//		}
	}

//	/**
//	 * Check that input image can be processed for classical algorithms, and throw an
//	 * exception if not the case.
//	 * In the current version, accepts all image types.
//	 */
//	private final static void checkImageType(ImageStack stack)
//	{
////		Array2D<?> image = stack.getProcessor(1);
////		if ((image instanceof FloatProcessor) || (image instanceof ShortProcessor)) {
////			throw new IllegalArgumentException("Input image must be a UInt8Array2D or a RGB8Array2D");
////		}
//	}

//	/**
//	 * Determine max possible value from bit depth.
//	 *  8 bits -> 255
//	 * 16 bits -> 65535
//	 * 32 bits -> Float.MAX_VALUE
//	 */
//	private static final double getMaxPossibleValue(ImageStack stack)
//	{
//		double maxVal = 255;
//		int bitDepth = stack.getBitDepth(); 
//		if (bitDepth == 16)
//		{
//			maxVal = 65535;
//		}
//		else if (bitDepth == 32)
//		{
//			maxVal = Float.MAX_VALUE;
//		}
//		return maxVal;
//	}
//	
//	private final static int clamp(int value, int min, int max) 
//	{
//		return Math.min(Math.max(value, min), max);
//	}

}
