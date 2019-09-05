/**
 * 
 */
package net.sci.image.morphology;

import net.sci.algo.Algo;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.morphology.filter.Strel2DWrapper;

/**
 * Structuring element for processing 3D arrays.
 * 
 * @author David Legland
 *
 */
public interface Strel3D extends Algo 
{
    public static Strel3D wrap(Strel2D strel)
    {
        return new Strel2DWrapper(strel);
    }
    
//	/**
//	 * An enumeration of the different possible structuring element shapes. 
//	 * Each item of the enumeration can create Strel instances of specific
//	 * class and of given size.
//	 */
//	public enum Shape {
//		/** 
//		 * Ball of a given radius
//		 * @see SquareStrel 
//		 */
//		BALL("Ball"),
//		
//		/** 
//		 * Cube of a given side
//		 * @see SquareStrel 
//		 */
//		CUBE("Cube"),
//		
//		/** 
//		 * Square of a given side
//		 * @see SquareStrel 
//		 */
//		SQUARE("Square"),
//		
//		/** 
//		 * Diamond of a given diameter
//		 * @see DiamondStrel
//		 * @see Cross3x3Strel 
//		 */
//		DIAMOND("Diamond"),
//		
//		/** 
//		 * Octagon of a given diameter
//		 * @see OctagonStrel
//		 */
//		OCTAGON("Octagon"),
//		
//		/**
//		 * Horizontal line of a given length 
//		 * @see LinearHorizontalStrel
//		 */
//		LINE_HORIZ("Horizontal Line"),
//		
//		/** 
//		 * Vertical line of a given length 
//		 * @see LinearVerticalStrel
//		 */
//		LINE_VERT("Vertical Line"),
//		
//		/**
//		 * Diagonal line of a given length 
//		 * @see LinearDiagUpStrel
//		 */
//		LINE_DIAG_UP("Line 45 degrees"),
//		
//		/** 
//		 * Diagonal line of a given length 
//		 * @see LinearDiagDownStrel
//		 */
//		LINE_DIAG_DOWN("Line 135 degrees");
//		
//		private final String label;
//		
//		private Shape(String label) {
//			this.label = label;
//		}
//		
//		/**
//		 * Returns the label associated to this shape.
//		 */
//		public String toString() {
//			return this.label;
//		}
//		
//		/**
//		 * Creates a structuring element of the given type and with the
//		 * specified radius. The final size is given by 2 * radius + 1, to
//		 * take into account the central pixel.
//		 * 
//		 * @param radius the radius of the structuring element, in pixels
//		 * @return a new structuring element
//		 * 
//		 */
//		public Strel3D fromRadius(int radius) {
//			return fromDiameter(2 * radius + 1);
//		}
//		
//		/**
//		 * Creates a structuring element of the given type and with the
//		 * specified radius for each dimension. The final size is given for each
//		 * dimension by 2 * radius + 1, to take into account the central pixel.
//		 * 
//		 * @param radiusX
//		 *            the radius of the structuring element in the x-direction, in pixels
//		 * @param radiusY
//		 *            the radius of the structuring element in the y-direction, in pixels
//		 * @param radiusZ
//		 *            the radius of the structuring element in the z-direction, in pixels
//		 * @return a new structuring element
//		 * 
//		 */
//		public Strel3D fromRadiusList(int radiusX, int radiusY, int radiusZ) 
//		{
//			if (this == BALL) 
//				return EllipsoidStrel.fromRadiusList(radiusX, radiusY, radiusZ);
//			if (this == CUBE) 
//				return CuboidStrel.fromRadiusList(radiusX, radiusY, radiusZ);
//			
//			if (radiusX != radiusY)
//			{
//				throw new IllegalArgumentException("For 2D structuring elements, radiusX and radiusY must be equal");
//			}
//			
//			// create the planar shape with specified size in largest direction
//			Strel.Shape shape2d = Strel.Shape.fromLabel(this.label);
//			Strel strel2d = shape2d.fromRadius(radiusX);
//			
//			int diamZ = 2 * radiusZ + 1;
//			return new ExtrudedStrel(strel2d, diamZ, radiusZ);
//		}
//		
//		/**
//		 * Creates a structuring element of the given type and with the
//		 * specified diameter.
//		 * @param diam the orthogonal diameter of the structuring element (max of x and y sizes), in pixels
//		 * @return a new structuring element
//		 */
//		public Strel3D fromDiameter(int diam) 
//		{
//			if (this == BALL) 
//				return BallStrel.fromDiameter(diam);
//			if (this == CUBE) 
//				return CubeStrel.fromDiameter(diam);
//			if (this == SQUARE) 
//				return new SquareStrel(diam);
//			if (this == DIAMOND) {
//				if (diam == 3)
//					return new Cross3x3Strel();
//				return new DiamondStrel(diam);
//			}
//			if (this == OCTAGON) 
//				return new OctagonStrel(diam);
//			if (this == LINE_HORIZ) 
//				return new LinearHorizontalStrel(diam);
//			if (this == LINE_VERT) 
//				return new LinearVerticalStrel(diam);
//			if (this == LINE_DIAG_UP) 
//				return new LinearDiagUpStrel(diam);
//			if (this == LINE_DIAG_DOWN) 
//				return new LinearDiagDownStrel(diam);
//			
//			throw new IllegalArgumentException("No default method for creating element of type " + this.label);
//		}
//		
//		/**
//		 * Creates a structuring element of the given type and with the
//		 * specified diameter.
//		 * 
//		 * @param diamX
//		 *            the diameter of the structuring element in the X direction
//		 * @param diamY
//		 *            the diameter of the structuring element in the Y direction
//		 * @param diamZ
//		 *            the diameter of the structuring element in the Z direction
//		 * @return a new structuring element
//		 */
//		public Strel3D fromDiameterList(int diamX, int diamY, int diamZ) 
//		{
//			if (this == BALL) 
//				return EllipsoidStrel.fromDiameterList(diamX, diamY, diamZ);
//			if (this == CUBE) 
//				return CuboidStrel.fromDiameterList(diamX, diamY, diamZ);
//			
//			if (diamX != diamY)
//			{
//				throw new IllegalArgumentException("For 2D structuring elements, radiusX and radiusY must be equal");
//			}
//			
//			// create the planar shape with specified size in largest direction
//			Strel.Shape shape2d = Strel.Shape.fromLabel(this.label);
//			Strel strel2d = shape2d.fromDiameter(diamX);
//			
//			return new ExtrudedStrel(strel2d, diamZ, (diamZ - 1) / 2);
//		}
//		
//		/**
//		 * Returns a set of labels for most of classical structuring elements.
//		 * @return a list of labels
//		 */
//		public static String[] getAllLabels(){
//			// array of all Strel types
//			Shape[] values = Shape.values();
//			int n = values.length;
//			
//			// keep all values but the last one ("Custom")
//			String[] result = new String[n];
//			for (int i = 0; i < n; i++)
//				result[i] = values[i].label;
//			
//			return result;
//		}
//		
//		/**
//		 * Determines the strel shape from its label.
//		 * 
//		 * @param label
//		 *            the name of the structuring element
//		 * @return a Shape object that can be used to instantiate new
//		 *         structuring elements
//		 * @throws IllegalArgumentException
//		 *             if label is not recognized.
//		 */
//		public static Shape fromLabel(String label) {
//			if (label != null)
//				label = label.toLowerCase();
//			for (Shape type : Shape.values()) {
//				if (type.label.toLowerCase().equals(label))
//					return type;
//			}
//			throw new IllegalArgumentException("Unable to parse Strel.Shape with label: " + label);
//		}
//	}
	
	/**
	 * Returns the size of the structuring element, as an array of size in each
	 * direction. The first index corresponds to the number of pixels in the x
	 * direction.
	 * 
	 * @return the size of the structuring element
	 */
	public int[] getSize();

	/**
	 * Returns the structuring element as a mask. Each value is either 0 or 255.
	 * The first index corresponds to the z position, the second index to
	 * the y direction, and the third one to the x position.
	 * 
	 * @return the mask of the structuring element
	 */
	public int[][][] getMask3D();

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
	public int[][] getShifts3D();

	/**
	 * Returns a reversed (i.e. symmetric wrt the origin) version of this
	 * structuring element. Implementations can return more specialized type
	 * depending on the implemented interfaces.
	 * 
	 * @return the reversed structuring element
	 */
	public Strel3D reverse();

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
	    return dilation(erosion(array));
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
        return erosion(dilation(array));
	}

}
