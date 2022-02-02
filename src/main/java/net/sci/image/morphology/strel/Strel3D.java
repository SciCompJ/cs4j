/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.morphology.Strel;

/**
 * Structuring element for processing 3D arrays.
 * 
 * @author David Legland
 *
 */
public interface Strel3D extends Strel
{
    // ===================================================================
    // Static methods
    
    public static Strel3D wrap(Strel strel)
    {
        if (strel instanceof Strel3D)
        {
            return (Strel3D) strel;
        }
        if (strel instanceof Strel2D)
        {
            return new Strel2DWrapper((Strel2D) strel);
        }
        throw new RuntimeException("Unable to wrap a strel with class: " + strel.getClass());
    }
    
    /**
     * An enumeration of the different possible structuring element shapes. 
     * Each item of the enumeration can create Strel instances of specific
     * class and of given size.
     */
    public enum Shape
    {
        /**
         * Ball of a given radius
         * @see SlidingBallStrel3D 
         */
        BALL("Ball"),

        /** 
         * Cube of a given side length.
         * @see CubeStrel3D 
         */
        CUBE("Cube"),
        ;
        
//        
//        /** 
//         * Diamond of a given diameter
//         * @see DiamondStrel
//         * @see Cross3x3Strel 
//         */
//        DIAMOND("Diamond"),
//        
//        /** 
//         * Octagon of a given diameter
//         * @see OctagonStrel
//         */
//        OCTAGON("Octagon"),
//        
//        /**
//         * Horizontal line of a given length 
//         * @see LinearHorizontalStrel
//         */
//        LINE_HORIZ("Horizontal Line"),
//        
//        /** 
//         * Vertical line of a given length 
//         * @see LinearVerticalStrel
//         */
//        LINE_VERT("Vertical Line"),
//        
//        /**
//         * Diagonal line of a given length 
//         * @see LinearDiagUpStrel
//         */
//        LINE_DIAG_UP("Line 45 degrees"),
//        
//        /** 
//         * Diagonal line of a given length 
//         * @see LinearDiagDownStrel
//         */
//        LINE_DIAG_DOWN("Line 135 degrees");
        
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
        public Strel3D fromRadius(int radius)
        {
            if (this == BALL) 
                return new SlidingBallStrel3D(radius);
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
        public Strel3D fromDiameter(int diam) 
        {
            if (this == BALL) 
                return new SlidingBallStrel3D((diam - 1.0) * 0.5);
            if (this == CUBE) 
                return new CubeStrel3D(diam);
            
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
    // High-level operations
    
	/**
     * Performs a morphological dilation of the input image with this
     * structuring element, and returns the result in a new Array3D.
     * 
     * @param array
     *            the input array
     * @return the result of dilation with this structuring element
     * @see #erosion(net.sci.array.ScalarArray3D)
     * @see #closing(net.sci.array.ScalarArray3D)
     * @see #opening(net.sci.array.ScalarArray3D)
     */
    public ScalarArray3D<?> dilation(ScalarArray3D<?> array);

    /**
     * Performs an morphological erosion of the input image with this
     * structuring element, and returns the result in a new Array3D.
     * 
     * @param array
     *            the input array
     * @return the result of erosion with this structuring element
     * @see #dilation(net.sci.array.ScalarArray3D)
     * @see #closing(net.sci.array.ScalarArray3D)
     * @see #opening(net.sci.array.ScalarArray3D)
     */
    public ScalarArray3D<?> erosion(ScalarArray3D<?> array);

    /**
     * Performs a morphological closing of the input image with this structuring
     * element, and returns the result in a new Array3D.
     *  
     * The closing is equivalent in performing a dilation followed by an
     * erosion with the reversed structuring element.
     * 
     * @param array
     *            the input array
     * @return the result of closing with this structuring element
     * @see #dilation(net.sci.array.ScalarArray3D)
     * @see #erosion(net.sci.array.ScalarArray3D)
     * @see #opening(net.sci.array.ScalarArray3D)
     * @see #reverse()
     */
    public default ScalarArray3D<?> closing(ScalarArray3D<?> array)
    {
        return reverse().erosion(dilation(array));
    }

    /**
     * Performs a morphological opening of the input image with this structuring
     * element, and returns the result in a new Array3D.
     * 
     * The opening is equivalent in performing an erosion followed by a
     * dilation with the reversed structuring element.
     * 
     * @param array
     *            the input array
     * @return the result of opening with this structuring element
     * @see #dilation(net.sci.array.ScalarArray3D)
     * @see #erosion(net.sci.array.ScalarArray3D)
     * @see #closing(net.sci.array.ScalarArray3D)
     * @see #reverse()
     */
    public default ScalarArray3D<?> opening(ScalarArray3D<?> array)
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
    public Strel3D reverse();
    

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
	 * Returns the structuring element as a binary array. The position of
     * the reference element within the mask can be obtained by the getOffset()
     * method.
	 * 
	 * @return the mask of the structuring element
	 */
	public BinaryArray3D getMask();

	/**
	 * Returns the offset in the mask for each direction. 
	 * The first value corresponds to the shift in the x direction.
	 * 
	 * @return the offset in the mask
	 */
	public int[] getOffset();

	/**
	 * Returns the structuring element as a set of shifts. The size of the
	 * result is N-by-3, where N is the number of elements of the structuring
	 * element. The first value corresponds to the shift in the x direction.
	 * 
	 * @return a set of shifts
	 */
	public int[][] getShifts();

    public default int dimensionality()
    {
        return 3;
    }
}
