/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.Algo;
import net.sci.array.Array2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.image.morphology.Strel;

/**
 * Interface for planar structuring elements. 
 * 
 * <pre>
 * {@code
    // Creates a 5x5 square structuring element
    Strel2D strel = Strel2D.Shape.SQUARE.fromDiameter(5);
    
    // Creates a simple array with white dot in the middle
    UInt8Array2D array = UInt8Array2D.create(9, 9);
    array.setValue(4, 4, 255);
    
    // applies dilation on array
    ScalarArray2D<?> dilated = strel.dilation(array);
    
    // display result
    dilated.print(System.out);
 *  
 * }
 * </pre>
 * @author David Legland
 *
 */
public interface Strel2D extends Strel, Algo
{
    // ===================================================================
    // Enumeration
    
	/**
	 * An enumeration of the different possible structuring element shapes. 
	 * Each item of the enumeration can create Strel instances of specific
	 * class and of given size.
	 */
	public enum Shape
	{
		/**
		 * Disk of a given radius
		 * @see NaiveDiskStrel 
		 */
		DISK("Disk"),

		/** 
		 * Square of a given side
		 * @see SquareStrel 
		 */
		SQUARE("Square"),
		
		/** 
		 * Diamond of a given diameter
		 * @see DiamondStrel
		 * @see Cross3x3Strel 
		 */
		DIAMOND("Diamond"),
		
		/** 
		 * Octagon of a given diameter
		 * @see OctagonStrel
		 */
		OCTAGON("Octagon"),
		
		/**
		 * Horizontal line of a given length 
		 * @see LinearHorizontalStrel
		 */
		LINE_HORIZ("Horizontal Line"),
		
		/** 
		 * Vertical line of a given length 
		 * @see LinearVerticalStrel
		 */
		LINE_VERT("Vertical Line"),
		
		/**
		 * Diagonal line of a given length 
		 * @see LinearDiagUpStrel
		 */
		LINE_DIAG_UP("Line 45 degrees"),
		
		/** 
		 * Diagonal line of a given length 
		 * @see LinearDiagDownStrel
		 */
		LINE_DIAG_DOWN("Line 135 degrees");
		
		private final String label;
		
		private Shape(String label) 
		{
			this.label = label;
		}
		
		/**
		 * @return the label associated to this shape.
		 */
		public String toString()
		{
			return this.label;
		}
		
		/**
		 * Creates a structuring element of the given type and with the
		 * specified radius. The final size is given by 2 * radius + 1, to
		 * take into account the central pixel.
		 * 
		 * @param radius the radius of the structuring element, in pixels
		 * @return a new structuring element
		 * 
		 */
		public Strel2D fromRadius(int radius)
		{
			if (this == DISK) 
				return new NaiveDiskStrel(radius);
			return fromDiameter(2 * radius + 1);
		}
		
		/**
		 * Creates a structuring element of the given type and with the
		 * specified diameter.
		 * 
		 * @param diam
		 *            the orthogonal diameter of the structuring element (max of
		 *            x and y sizes), in pixels
		 * @return a new structuring element
		 */
		public Strel2D fromDiameter(int diam) 
		{
			if (this == DISK) 
				return new NaiveDiskStrel((diam - 1.0) * 0.5);
			if (this == SQUARE) 
				return new SquareStrel(diam);
			if (this == DIAMOND) {
				if (diam == 3)
					return new Cross3x3Strel();
				return new DiamondStrel(diam);
			}
			if (this == OCTAGON) 
				return new OctagonStrel(diam);
			if (this == LINE_HORIZ) 
				return new LinearHorizontalStrel(diam);
			if (this == LINE_VERT) 
				return new LinearVerticalStrel(diam);
			if (this == LINE_DIAG_UP) 
				return new LinearDiagUpStrel(diam);
			if (this == LINE_DIAG_DOWN) 
				return new LinearDiagDownStrel(diam);
			
			throw new IllegalArgumentException("No default method for creating element of type " + this.label);
		}
		
		/**
		 * Returns a set of labels for most of classical structuring elements.
		 * 
		 * @return a list of labels
		 */
		public static String[] getAllLabels()
		{
			// array of all Strel types
			Shape[] values = Shape.values();
			int n = values.length;
			
			// keep all values but the last one ("Custom")
			String[] result = new String[n];
			for (int i = 0; i < n; i++)
				result[i] = values[i].label;
			
			return result;
		}
		
		/**
		 * Determines the strel shape from its label.
		 * 
		 * @param label
		 *            the shape name of the structuring element
		 * @return a new Shape instance that can be used to create structuring
		 *         elements
		 * @throws IllegalArgumentException
		 *             if label is not recognized.
		 */
		public static Shape fromLabel(String label)
		{
			if (label != null)
				label = label.toLowerCase();
			for (Shape s : Shape.values()) 
			{
				if (s.label.toLowerCase().equals(label))
					return s;
			}
			throw new IllegalArgumentException("Unable to parse Strel.Shape with label: " + label);
		}
	}
	
    // ===================================================================
    // Static methods
    
    public static Strel2D wrap(Strel strel)
    {
        if (strel instanceof Strel2D)
        {
            return (Strel2D) strel;
        }
        throw new RuntimeException("Unable to wrap a strel with class: " + strel.getClass());
    }
	
    // ===================================================================
    // High-level operations
    
	/**
     * Performs a morphological dilation of the input image with this
     * structuring element, and returns the result in a new Array2D.
     * 
     * @param array
     *            the input image
     * @return the result of dilation with this structuring element
     * @see #erosion(Array2D)
     * @see #closing(Array2D)
     * @see #opening(Array2D)
     */
    public ScalarArray2D<?> dilation(ScalarArray2D<?> array);

    /**
     * Performs an morphological erosion of the input image with this
     * structuring element, and returns the result in a new Array2.
     * 
     * @param array
     *            the input image
     * @return the result of erosion with this structuring element
     * @see #dilation(Array2D)
     * @see #closing(Array2D)
     * @see #opening(Array2D)
     */
    public ScalarArray2D<?> erosion(ScalarArray2D<?> array);

    /**
     * Performs a morphological closing of the input image with this structuring
     * element, and returns the result in a new Array2D.
     *  
     * The closing is equivalent in performing a dilation followed by an
     * erosion with the reversed structuring element.
     * 
     * @param array
     *            the input image
     * @return the result of closing with this structuring element
     * @see #dilation(Array2D)
     * @see #erosion(Array2D)
     * @see #opening(Array2D)
     * @see #reverse()
     */
    public ScalarArray2D<?> closing(ScalarArray2D<?> array);

    /**
     * Performs a morphological opening of the input image with this structuring
     * element, and returns the result in a new Array2D.
     * 
     * The opening is equivalent in performing an erosion followed by a
     * dilation with the reversed structuring element.
     * 
     * @param array
     *            the input image
     * @return the result of opening with this structuring element
     * @see #dilation(Array2D)
     * @see #erosion(Array2D)
     * @see #closing(Array2D)
     * @see #reverse()
     */
    public ScalarArray2D<?> opening(ScalarArray2D<?> array);

    /**
     * Returns a reversed (i.e. symmetric wrt the origin) version of this
     * structuring element. Implementations can return more specialized type
     * depending on the implemented interfaces.
     * 
     * @return the reversed structuring element
     */
    public Strel2D reverse();


    // ===================================================================
    // Low-level operations
    
    /**
	 * Returns the size of the structuring element, as an array of size in each
	 * direction. The first index corresponds to the number of pixels in the x
	 * direction.
	 * 
	 * @return the size of the structuring element
	 */
	public int[] size();

	/**
	 * Returns the structuring element as a mask. Each value is either 0 or 255.
	 * The first index corresponds to the y position, and the second index to
	 * the x direction.
	 * 
	 * @return the mask of the structuring element
	 */
	public int[][] getMask();

	/**
	 * Returns the offset in the mask. The first value corresponds to the shift
	 * in the x direction.
	 * 
	 * @return the offset in the mask
	 */
	public int[] getOffset();

	/**
	 * Returns the structuring element as a set of shifts. The size of the
	 * result is N-by-2, where N is the number of elements of the structuring
	 * element. The first value corresponds to the shift in the x direction.
	 * 
	 * @return a set of shifts
	 */
	public int[][] getShifts();
	
	public default int dimensionality()
	{
	    return 2;
	}
}