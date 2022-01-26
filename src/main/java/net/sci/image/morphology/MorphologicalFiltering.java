/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Array2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.vector.VectorArray2D;
import net.sci.image.morphology.strel.Strel2D;

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
 * Array2D<?> array = ...
 * Strel se = SquareStrel.fromDiameter(5);
 * Array2D<?> grad = MorphologicalFiltering.gradient(array, se);
 * Image res = new Image(grad);
 * res.show(); 
 *  }</pre>
 *
 * <p>
 * Example of use with 3D array (stack):
 * <pre>
 * {@code
 * Array3D<?> array = ...
 * Strel3D se = CubeStrel.fromDiameter(3);
 * Array2D<?>  grad = MorphologicalFiltering.gradient(array, se);
 * ImagePlus res = new ImagePlus("Gradient3D", grad);
 * res.show(); 
 * }</pre>
 * 
 * @deprecated replaced by MorphologicalFilters utility class
 * 
 * @author David Legland
 *
 */
@Deprecated
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
	 * // Apply the operation on the current array
	 * Array2D<?> array = IJ.getImage().getProcessor();
	 * op.apply(array, SquareStrel.fromRadius(2));
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
		/** Morphological internal gradient (difference of dilation with original array) */
		INTERNAL_GRADIENT("Internal Gradient"), 
		/** Morphological internal gradient (difference of original array with erosion) */
		EXTERNAL_GRADIENT("External Gradient");
		
		private final String label;
		
		private Operation(String label) 
		{
			this.label = label;
		}
		
		/**
		 * Applies the current operator to the input array.
		 * 
		 * @param array
		 *            the array to process
		 * @param strel
		 *            the structuring element to use
		 * @return the result of morphological operation applied to array
		 */
		public Array2D<?> apply(Array2D<?> array, Strel2D strel) 
		{
            if (this == DILATION) return dilation(array, strel);
            if (this == EROSION) return erosion(array, strel);
            if (this == CLOSING) return closing(array, strel);
            if (this == OPENING) return opening(array, strel);
            if (this == TOPHAT) return whiteTopHat(array, strel);
            if (this == BOTTOMHAT) return blackTopHat(array, strel);
            if (this == GRADIENT) return gradient(array, strel);
            if (this == LAPLACIAN) return laplacian(array, strel);
            if (this == INTERNAL_GRADIENT) return internalGradient(array, strel);
            if (this == EXTERNAL_GRADIENT) return externalGradient(array, strel);

            throw new RuntimeException("Unable to process the " + this + " morphological operation");
		}
		
//		/**
//		 * Applies the current operator to the input 3D array.
//		 * 
//		 * @param array
//		 *            the array to process
//		 * @param strel
//		 *            the structuring element to use
//		 * @return the result of morphological operation applied to array
//		 */
//		public ImageStack apply(ImageStack array, Strel3D strel)
//		{
//			if (this == DILATION)
//				return dilation(array, strel);
//			if (this == EROSION)
//				return erosion(array, strel);
//			if (this == CLOSING)
//				return closing(array, strel);
//			if (this == OPENING)
//				return opening(array, strel);
//			if (this == TOPHAT)
//				return whiteTopHat(array, strel);
//			if (this == BOTTOMHAT)
//				return blackTopHat(array, strel);
//			if (this == GRADIENT)
//				return gradient(array, strel);
//			if (this == LAPLACIAN)
//				return laplacian(array, strel);
//			if (this == INTERNAL_GRADIENT)
//				return internalGradient(array, strel);
//			if (this == EXTERNAL_GRADIENT)
//				return externalGradient(array, strel);
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
	 * Performs morphological dilation on the input array.
	 * 
	 * Dilation is obtained by extracting the maximum value among pixels in the
	 * neighborhood given by the structuring element.
	 * 
	 * This methods is mainly a wrapper to the dilation method of the strel
	 * object.
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for dilation
	 * @return the result of the dilation
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see Strel2D#dilation(Array2D)
	 */
	public static Array2D<?> dilation(Array2D<?> array, Strel2D strel)
	{
	    if (array instanceof ScalarArray2D)
	    {
	        return strel.dilation((ScalarArray2D<?>) array);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return dilation_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
	        throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
	    }
	}

    /**
     * Performs morphological dilation on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for dilation
     * @return the result of the dilation
     */
	private static VectorArray2D<?> dilation_vector2d(VectorArray2D<?> array, Strel2D strel)
	{
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(strel.dilation(array.channel(c)), res, c);
        }
        return res;
	}
	
	/**
	 * Performs morphological erosion on the input array. Erosion is obtained by
	 * extracting the minimum value among pixels in the neighborhood given by
	 * the structuring element.
	 * 
	 * This methods is mainly a wrapper to the erosion method of the strel
	 * object.
	 * 
	 * @see #dilation(Array2D, Strel2D)
	 * @see Strel2D#erosion(Array2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for erosion
	 * @return the result of the erosion
	 */
	public static Array2D<?> erosion(Array2D<?> array, Strel2D strel)
	{
        if (array instanceof ScalarArray2D)
        {
            return strel.erosion((ScalarArray2D<?>) array);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return erosion_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
	}

    /**
     * Performs morphological erosion on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for erosion
     * @return the result of the erosion
     */
    private static VectorArray2D<?> erosion_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(strel.erosion(array.channel(c)), res, c);
        }
        return res;
    }

	/**
	 * Performs morphological opening on the input array.
	 * 
	 * The opening is obtained by performing an erosion followed by an dilation
	 * with the reversed structuring element.
	 * 
	 * This methods is mainly a wrapper to the opening method of the strel object.
	 * 
	 * @see #closing(Array2D, Strel2D)
	 * @see Strel2D#opening(Array2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for opening
	 * @return the result of the morphological opening
	 */
	public static Array2D<?> opening(Array2D<?> array, Strel2D strel)
	{
        if (array instanceof ScalarArray2D)
        {
            return strel.opening((ScalarArray2D<?>) array);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return opening_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
	}

    /**
     * Performs morphological opening on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for opening
     * @return the result of the opening
     */
    private static VectorArray2D<?> opening_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(strel.opening(array.channel(c)), res, c);
        }
        return res;
    }


	/**
	 * Performs closing on the input array.
	 * The closing is obtained by performing a dilation followed by an erosion
	 * with the reversed structuring element.
	 *  
	 * This methods is mainly a wrapper to the opening method of the strel object.
	 * @see #opening(Array2D, Strel2D)
	 * @see Strel2D#closing(Array2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for closing
	 * @return the result of the morphological closing
	 */
	public static Array2D<?> closing(Array2D<?> array, Strel2D strel) 
	{
        if (array instanceof ScalarArray2D)
        {
            return strel.closing((ScalarArray2D<?>) array);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return closing_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
	}

    /**
     * Performs morphological closing on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for closing
     * @return the result of the closing
     */
    private static VectorArray2D<?> closing_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(strel.closing(array.channel(c)), res, c);
        }
        return res;
    }

	/**
	 * Computes white top hat of the original array.
	 * The white top hat is obtained by subtracting the result of an opening 
	 * from the original array.
	 *  
	 * The white top hat enhances light structures smaller than the structuring element.
	 * 
	 * @see #blackTopHat(Array2D, Strel2D)
	 * @see #opening(Array2D, Strel2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for computing white top-hat
	 * @return the result of the white top-hat
	 */
    public static Array2D<?> whiteTopHat(Array2D<?> array, Strel2D strel)
    {
        if (array instanceof ScalarArray2D)
        {
            return whiteTopHat_scalar2d((ScalarArray2D<?>) array, strel);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return whiteTopHat_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
    }
    
	private static ScalarArray2D<?> whiteTopHat_scalar2d(ScalarArray2D<?> array, Strel2D strel) 
	{
        // First performs opening
        ScalarArray2D<?> result = strel.opening(array);
		
		// Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = array.getValue(x, y) - result.getValue(x, y);
                result.setValue(x, y, val);
            }
        }

		return result;
	}
	
    /**
     * Computes white top hat on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for computing white top hat
     * @return the result of the white top hat
     */
    private static VectorArray2D<?> whiteTopHat_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(whiteTopHat_scalar2d(array.channel(c), strel), res, c);
        }
        return res;
    }

	/**
	 * Computes black top hat (or "bottom hat") of the original array.
	 * The black top hat is obtained by subtracting the original array from
	 * the result of a closing.
	 *  
	 * The black top hat enhances dark structures smaller than the structuring element.
	 * 
	 * @see #whiteTopHat(Array2D, Strel2D)
	 * @see #closing(Array2D, Strel2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for black top-hat
	 * @return the result of the black top-hat
	 */
	public static Array2D<?> blackTopHat(Array2D<?> array, Strel2D strel)
	{
        if (array instanceof ScalarArray2D)
        {
            return blackTopHat_scalar2d((ScalarArray2D<?>) array, strel);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return blackTopHat_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
	}
	
    private static ScalarArray2D<?> blackTopHat_scalar2d(ScalarArray2D<?> array, Strel2D strel) 
    {
        // First performs closing
        ScalarArray2D<?> result = strel.closing(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = result.getValue(x, y) - array.getValue(x, y);
                result.setValue(x, y, val);
            }
        }

        return result;
    }
    
    /**
     * Computes black top hat on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for computing black top hat
     * @return the result of the black top hat
     */
    private static VectorArray2D<?> blackTopHat_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(blackTopHat_scalar2d(array.channel(c), strel), res, c);
        }
        return res;
    }
	
	/**
	 * Computes the morphological gradient of the input array.
	 * The morphological gradient is obtained by from the difference of array 
	 * dilation and array erosion computed with the same structuring element. 
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see #dilation(Array2D, Strel2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological gradient
	 * @return the result of the morphological gradient
	 */
    public static Array2D<?> gradient(Array2D<?> array, Strel2D strel)
    {
        if (array instanceof ScalarArray2D)
        {
            return gradient_scalar2d((ScalarArray2D<?>) array, strel);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return gradient_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
    }
    
	private static ScalarArray2D<?> gradient_scalar2d(ScalarArray2D<?> array, Strel2D strel)
	{
        // First performs closing
        ScalarArray2D<?> result = strel.dilation(array);
        ScalarArray2D<?> eroded = strel.erosion(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = result.getValue(x, y) - eroded.getValue(x, y);
                result.setValue(x, y, val);
            }
        }
        
        return result;
	}

    /**
     * Computes morphological gradient on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for gradient
     * @return the result of the morphological gradient
     */
    private static VectorArray2D<?> gradient_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(gradient_scalar2d(array.channel(c), strel), res, c);
        }
        return res;
    }

	/**
	 * Computes the morphological Laplacian of the input array. The
	 * morphological gradient is obtained from the difference of the external
	 * gradient with the internal gradient, both computed with the same
	 * structuring element.
	 * 
	 * Homogeneous regions appear as gray.
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see #dilation(Array2D, Strel2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological laplacian
	 * @return the result of the morphological laplacian
	 */
    public static Array2D<?> laplacian(Array2D<?> array, Strel2D strel) 
    {
        if (array instanceof ScalarArray2D)
        {
            return laplacian_scalar2d((ScalarArray2D<?>) array, strel);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return laplacian_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
    }
    
    private static ScalarArray2D<?> laplacian_scalar2d(ScalarArray2D<?> array, Strel2D strel) 
	{
        // computes gradients
        ScalarArray2D<?> dil = strel.dilation(array);
        ScalarArray2D<?> ero = strel.erosion(array);
        
        double shift = 0;
        if (array instanceof UInt8Array)
        {
            shift = 128;
        }

        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = (dil.getValue(x, y) + ero.getValue(x, y)) / 2 - array.getValue(x, y);
                dil.setValue(x, y, val + shift);
            }
        }

		// return laplacian
		return dil;
	}
    
    /**
     * Computes morphological laplacian on each channel of a vector array, and
     * reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for laplacian
     * @return the result of the morphological laplacian
     */
    private static VectorArray2D<?> laplacian_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(laplacian_scalar2d(array.channel(c), strel), res, c);
        }
        return res;
    }


	/** 
	 * Computes the morphological internal gradient of the input array.
	 * The morphological internal gradient is obtained by from the difference 
	 * of original array with the result of an erosion.
	 * 
	 * @see #erosion(Array2D, Strel2D)
	 * @see #gradient(Array2D, Strel2D)
	 * @see #externalGradient(Array2D, Strel2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological internal gradient
	 * @return the result of the morphological internal gradient
	 */
    public static Array2D<?> internalGradient(Array2D<?> array, Strel2D strel)
    {
        if (array instanceof ScalarArray2D)
        {
            return internalGradient_scalar2d((ScalarArray2D<?>) array, strel);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return internalGradient_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
    }
    
    private static ScalarArray2D<?> internalGradient_scalar2d(ScalarArray2D<?> array, Strel2D strel) 
	{
        // First performs closing
        ScalarArray2D<?> result = strel.erosion(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = array.getValue(x, y) - result.getValue(x, y);
                result.setValue(x, y, val);
            }
        }
        
        return result;
	}

    /**
     * Computes internal morphological gradient on each channel of a vector
     * array, and reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for gradient
     * @return the result of the morphological gradient
     */
    private static VectorArray2D<?> internalGradient_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(internalGradient_scalar2d(array.channel(c), strel), res, c);
        }
        return res;
    }


	/** 
	 * Computes the morphological external gradient of the input array.
	 * The morphological external gradient is obtained by from the difference 
	 * of the result of a dilation and of the original array .
	 * 
	 * @see #dilation(Array2D, Strel2D)
	 * 
	 * @param array
	 *            the input array to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological external gradient
	 * @return the result of the morphological external gradient
	 */
	public static Array2D<?> externalGradient(Array2D<?> array, Strel2D strel) 
	{
        if (array instanceof ScalarArray2D)
        {
            return externalGradient_scalar2d((ScalarArray2D<?>) array, strel);
        }
        else if (array instanceof VectorArray2D<?>)
        {
            return externalGradient_vector2d((VectorArray2D<?>) array, strel);
        }
        else
        {
            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
        }
	}
    
    private static ScalarArray2D<?> externalGradient_scalar2d(ScalarArray2D<?> array, Strel2D strel) 
    {
        // First performs closing
        ScalarArray2D<?> result = strel.dilation(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = result.getValue(x, y) - array.getValue(x, y);
                result.setValue(x, y, val);
            }
        }
        
        return result;
    }

    /**
     * Computes external morphological gradient on each channel of a vector
     * array, and reconstitutes the resulting vector array.
     * 
     * @param array
     *            the input vector array
     * @param strel
     *            the structuring element used for gradient
     * @return the result of the morphological gradient
     */
    private static VectorArray2D<?> externalGradient_vector2d(VectorArray2D<?> array, Strel2D strel)
    {
        // allocate memory for result
        VectorArray2D<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            copyChannel(externalGradient_scalar2d(array.channel(c), strel), res, c);
        }
        return res;
    }

	
	private static void copyChannel(ScalarArray2D<?> channel, VectorArray2D<?> array, int channelIndex)
	{
        // copy into result
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                array.setValue(x, y, channelIndex, channel.getValue(x, y));
            }
        }
	}
}
