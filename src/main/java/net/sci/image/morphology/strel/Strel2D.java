/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.morphology.Strel;

/**
 * Interface for 2D structuring elements.
 * 
 * Example of use:
 * {@snippet lang="java":
 * // Creates a 5x5 square structuring element
 * Strel2D strel = Strel2D.Shape.SQUARE.fromDiameter(5);
 * 
 * // Creates a simple array with white dot in the middle
 * UInt8Array2D array = UInt8Array2D.create(9, 9);
 * array.setValue(4, 4, 255);
 * 
 * // applies morphological dilation on array
 * ScalarArray2D<?> dilated = strel.dilation(array);
 * 
 * // display result
 * dilated.printContent(System.out);
 * }
 *
 * @see Strel3D
 * 
 * @author David Legland
 */
public interface Strel2D extends Strel
{
    // ===================================================================
    // Enumeration
    
    /**
     * An enumeration of the different possible structuring element shapes. Each
     * item of the enumeration can create Strel instances of specific class and
     * of given size.
     */
    public enum Shape
    {
        /**
         * Disk of a given radius
         * 
         * @see NaiveDiskStrel
         */
        DISK("Disk", "Disk"),
        
        /**
         * Square of a given side
         * 
         * @see SquareStrel
         */
        SQUARE("Square", "Sqr"),
        
        /**
         * Diamond of a given diameter
         * 
         * @see DiamondStrel
         * @see Cross3x3Strel
         */
        DIAMOND("Diamond", "Dmd"),
        
        /**
         * Octagon of a given diameter
         * 
         * @see OctagonStrel
         */
        OCTAGON("Octagon", "Oct"),
        
        /**
         * Horizontal line of a given length
         * 
         * @see LinearHorizontalStrel
         */
        LINE_HORIZ("Horizontal Line", "LineH"),
        
        /**
         * Vertical line of a given length
         * 
         * @see LinearVerticalStrel
         */
        LINE_VERT("Vertical Line", "LineV"),
        
        /**
         * Diagonal line of a given length
         * 
         * @see LinearDiagUpStrel
         */
        LINE_DIAG_UP("Line 45 degrees", "Line045"),
        
        /**
         * Diagonal line of a given length
         * 
         * @see LinearDiagDownStrel
         */
        LINE_DIAG_DOWN("Line 135 degrees", "Line135");
        
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
            if (label != null) label = label.toLowerCase();
            for (Shape s : Shape.values())
            {
                if (s.label.toLowerCase().equals(label)) return s;
            }
            throw new IllegalArgumentException("Unable to parse Strel.Shape with label: " + label);
        }
        
        private final String label;
        
        private final String suffix;
        
        private Shape(String label, String suffix)
        {
            this.label = label;
            this.suffix = suffix;
        }
        
        /**
         * Creates a structuring element of the given type and with the
         * specified radius. The final size is given by 2 * radius + 1, to take
         * into account the central pixel.
         * 
         * @param radius
         *            the radius of the structuring element, in pixels
         * @return a new structuring element
         *            
         */
        public Strel2D fromRadius(int radius)
        {
            if (this == DISK) return new SlidingDiskStrel(radius);
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
            return switch (this)
            {
                case DISK -> new SlidingDiskStrel((diam - 1.0) * 0.5);
                case SQUARE ->  new SquareStrel(diam);
                case DIAMOND -> (diam == 3) ? new Cross3x3Strel() : new DiamondStrel(diam);
                case OCTAGON -> new OctagonStrel(diam);
                case LINE_HORIZ -> new LinearHorizontalStrel(diam);
                case LINE_VERT -> new LinearVerticalStrel(diam);
                case LINE_DIAG_UP -> new LinearDiagUpStrel(diam);
                case LINE_DIAG_DOWN -> new LinearDiagDownStrel(diam);
                
                default -> throw new IllegalArgumentException(
                        "No default method for creating element of type " + this.label);
            };
        }
        
        /**
         * Returns the suffix associated to this Strel shape. The suffix is a
         * short (2-3 characters) string used to identify the shape and that can
         * be used to create the name of result images.
         * 
         * @return the suffix associated to this Strel shape.
         */
        public String suffix()
        {
            return suffix;
        }
        
		/**
         * @return the label associated to this shape.
         */
        public String toString()
        {
        	return this.label;
        }
    }
	
    
    // ===================================================================
    // Static methods
    
    /**
     * Ensures the specified structuring element is seen as an instance of
     * Strel2D.
     * 
     * @param strel
     *            a structuring element
     * @return the instance of Strel2D that corresponds to the specified
     *         structuring element
     */
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
     * @see #erosion(ScalarArray2D)
     * @see #closing(ScalarArray2D)
     * @see #opening(ScalarArray2D)
     */
    public ScalarArray2D<?> dilation(ScalarArray2D<?> array);

    /**
     * Performs an morphological erosion of the input image with this
     * structuring element, and returns the result in a new Array2.
     * 
     * @param array
     *            the input image
     * @return the result of erosion with this structuring element
     * @see #dilation(ScalarArray2D)
     * @see #closing(ScalarArray2D)
     * @see #opening(ScalarArray2D)
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
     * @see #dilation(ScalarArray2D)
     * @see #erosion(ScalarArray2D)
     * @see #opening(ScalarArray2D)
     * @see #reverse()
     */
    public default ScalarArray2D<?> closing(ScalarArray2D<?> array)
    {
        return reverse().erosion(dilation(array));
    }

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
     * @see #dilation(ScalarArray2D)
     * @see #erosion(ScalarArray2D)
     * @see #closing(ScalarArray2D)
     * @see #reverse()
     */
    public default ScalarArray2D<?> opening(ScalarArray2D<?> array)
    {
        return reverse().dilation(erosion(array));
    }

    /**
     * Returns a reversed (i.e. symmetric wrt the origin) version of this
     * structuring element. Implementations can return more specialized type
     * depending on the implemented interfaces.
     * 
     * @return the reversed structuring element
     */
    @Override
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
    @Override
    public int[] size();
    
    /**
     * Returns the structuring element as a binary array. The position of the
     * reference element within the mask can be obtained by the
     * <code>maskOffset()</code> method.
     * 
     * @return the mask of the structuring element
     */
    @Override
    public BinaryArray2D binaryMask();
    
    /**
     * Returns the offset in the mask. The first value corresponds to the shift
     * in the x direction.
     * 
     * @return the offset in the mask
     */
    @Override
    public int[] maskOffset();
    
    /**
     * Returns the structuring element as a set of shifts. The size of the
     * result is N-by-2, where N is the number of elements of the structuring
     * element. For each shift, the first value corresponds to the shift in the
     * x direction.
     * 
     * @return a set of shifts
     */
    @Override
    public int[][] shifts();
    
    /**
     * Returns a dimensionality equals to 2.
     * 
     * @returns the value 2.
     */
    @Override
    public default int dimensionality()
    {
        return 2;
    }
}
