/**
 * 
 */
package net.sci.image.binary.distmap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Chamfer masks defines a series of weights associated to neighbors of current
 * pixels.
 *
 * The interface declares methods to access forward and backward offsets, used
 * in classical two-passes algorithms.
 * 
 * The weights may be defined either as integers or as floating point values.
 * 
 * @see ChamferMasks2D
 * @see ChamferMask3D
 * @see ChamferDistanceTransform2D
 * 
 * @author dlegland
 */
public abstract class ChamferMask2D
{
	// ==================================================
	// Public constants
	
	/** Use weight equal to 1 for all neighbors. */
	public final static ChamferMask2D CHESSBOARD = new ChamferMask2DW2(1, 1);
	
	/**
	 * Use weights 1 for orthogonal neighbors and 2 for diagonal neighbors.
	 */
	public final static ChamferMask2D CITY_BLOCK = new ChamferMask2DW2(1, 2);
	
	/**
	 * Use weights 1 for orthogonal neighbors and sqrt(2) for diagonal neighbors.
	 * Integer weights are 10 and 14.
	 */
	public final static ChamferMask2D QUASI_EUCLIDEAN = new ChamferMask2DW2Float( 
			new short[] { 10, 14 }, 
			new float[] {1, (float) Math.sqrt(2) });

	/**
	 * Use weights 3 for orthogonal neighbors and 4 for diagonal neighbors
	 * (best approximation for 3-by-3 masks).
	 */
	public final static ChamferMask2D BORGEFORS = new ChamferMask2DW2(3, 4);
	
	/**
	 * Use weights 5 for orthogonal neighbors, 7 for diagonal neighbors, and 11
	 * for chess-knight moves (recommended approximation for 5-by-5 masks).
	 */
	public final static ChamferMask2D CHESSKNIGHT = new ChamferMask2DW3(5, 7, 11);

	/**
     * Chamfer mask in the 7-by-7 neighborhood defined using four weights.
     * Weights are 12, 17, 27, and 38. The "fifth" one (shift 3,2) is
     * approximated by weights 17+27=44, very close from the value proposed by
     * Borgefors (1986).
     */
	public final static ChamferMask2D VERWER = new ChamferMask2DW4(12, 17, 27, 38);
	
	
	// ==================================================
	// Static factories
	
	/**
	 * Creates a new Chamfer mask from a list of weights.
	 * 
	 * The size of the chamfer mask depends on the length of the array:
	 * <ul>
	 * <li>single element: chamfer mask is 3-by-3, the element value corresponds
	 * to the weight of orthogonal offsets</li>
	 * <li>two elements: chamfer mask is 3-by-3, the second element corresponds
	 * to the weight of diagonal offsets</li>
	 * <li>three elements: chamfer mask is 5-by-5, the third element corresponds
	 * to the weight of chess-knight move offsets</li>
	 * </ul>
	 * 
	 * @param weights
	 *            the list of weights used for building the chamfer mask
	 * @return the chamfer mask corresponding to the weights
	 */
	public static final ChamferMask2D fromWeights(int[] weights)
	{
		if (weights.length == 1)
		{
			int a = weights[0];
			return new ChamferMask2DW2(a, 2 * a);
		}
		else if (weights.length == 2)
		{
			int a = weights[0];
			int b = weights[1];
			return new ChamferMask2DW2(a, b);
		}
		else if (weights.length == 3)
		{
			int a = weights[0];
			int b = weights[1];
			int c = weights[2];
			return new ChamferMask2DW3(a, b, c);
		}
		else
		{
			throw new RuntimeException("Can not create chamfer mask with the given number of weights: " + weights.length);
		}
	}
	
	/**
	 * Creates a new Chamfer mask from a list of weights.
	 * 
	 * The size of the chamfer mask depends on the length of the array:
	 * <ul>
	 * <li>single element: chamfer mask is 3-by-3, the element value corresponds
	 * to the weight of orthogonal offsets</li>
	 * <li>two elements: chamfer mask is 3-by-3, the second element corresponds
	 * to the weight of diagonal offsets</li>
	 * <li>three elements: chamfer mask is 5-by-5, the third element corresponds
	 * to the weight of chess-knight move offsets</li>
	 * </ul>
	 * 
	 * @param weights
	 *            the list of weights used for building the chamfer mask
	 * @return the chamfer mask corresponding to the weights
	 */
	public static final ChamferMask2D fromWeights(short[] weights)
	{
		if (weights.length == 1)
		{
			int a = weights[0];
			return new ChamferMask2DW2(a, 2 * a);
		}
		else if (weights.length == 2)
		{
			int a = weights[0];
			int b = weights[1];
			return new ChamferMask2DW2(a, b);
		}
		else if (weights.length == 3)
		{
			int a = weights[0];
			int b = weights[1];
			int c = weights[2];
			return new ChamferMask2DW3(a, b, c);
		}
		else
		{
			throw new RuntimeException("Can not create chamfer mask with the given number of weights: " + weights.length);
		}
	}
	
	/**
	 * Creates a new Chamfer mask from a list of weights.
	 * 
	 * The size of the chamfer mask depends on the length of the array:
	 * <ul>
	 * <li>single element: chamfer mask is 3-by-3, the element value corresponds
	 * to the weight of orthogonal offsets</li>
	 * <li>two elements: chamfer mask is 3-by-3, the second element corresponds
	 * to the weight of diagonal offsets</li>
	 * <li>three elements: chamfer mask is 5-by-5, the third element corresponds
	 * to the weight of chess-knight move offsets</li>
	 * </ul>
	 * 
	 * @param weights
	 *            the list of weights used for building the chamfer mask
	 * @return the chamfer mask corresponding to the weights
	 */
	public static final ChamferMask2D fromWeights(float[] weights)
	{
		// compute integer version of floating point weights
		// (multiply by 10 to reduce rounding effect)
		short[] intWeights = new short[weights.length];
		for (int i = 0; i < weights.length; i++)
		{
			intWeights[i] = (short) Math.round(weights[i] * 10.0);
		}
		
		if (weights.length == 2)
		{
			return new ChamferMask2DW2Float(intWeights, weights);
		}
		else
		{
			throw new RuntimeException("Can not create chamfer mask with the given number of weights: " + weights.length);
		}
	}
	

	// ==================================================
	// Global methods
	
	/**
	 * @return the whole collection of offsets defined by this ChamferMask2D.
	 */
	public Collection<Offset> getOffsets()
	{
		ArrayList<Offset> offsets = new ArrayList<Offset>();
		offsets.addAll(getForwardOffsets());
		offsets.addAll(getBackwardOffsets());
		return offsets;
	}
	

	// ==================================================
	// Declaration of abstract methods
	
	/**
	 * @return the set of offsets defined by this ChamferMask2D for forward
	 *         iteration.
	 */
	public abstract Collection<Offset> getForwardOffsets();

	/**
	 * @return the set of offsets defined by this ChamferMask2D for backward
	 *         iteration.
	 */
	public abstract Collection<Offset> getBackwardOffsets();
	
	/**
	 * @return the weight associated to orthogonal offsets, that can be used to
	 *         normalize the distance map.
	 */
	public double getNormalizationWeight()
	{
		return getIntegerNormalizationWeight();
	}

	/**
	 * @return the weight associated to orthogonal offsets, that can be used to
	 *         normalize the distance map.
	 */
	public abstract int getIntegerNormalizationWeight();

	
	// ==================================================
	// Inner classes declaration
	
    /**
     * The shift to a neighbor of a reference pixel, as a pair (dx,dy),
     * and the associated weights given both as an int and as a float.
     */
    public static class Offset
    {
        /** The offset along the X-axis */
        public final int dx;
        /** The offset along the Y-axis */
        public final int dy;
        
        /** The weight associated to this offset, as floating-point value. */
        public final double weight;
        
        /** The weight associated to this offset, as integer value. */
        public final int intWeight;

        /**
         * Creates a new Offset using a 16-bits integer weight.
         * 
         * @param dx
         *            the offset along the X-axis
         * @param dy
         *            the offset along the Y-axis
         * @param weight
         *            the weight of the offset
         */
        public Offset(int dx, int dy, int weight)
        {
            this.dx = dx;
            this.dy = dy;
            this.weight = weight;
            this.intWeight = weight;
        }
        
        /**
         * Creates a new Offset using a 16-bits integer weight.
         * 
         * @param dx
         *            the offset along the X-axis
         * @param dy
         *            the offset along the Y-axis
         * @param weight
         *            the weight of the offset
         */
        public Offset(int dx, int dy, double weight)
        {
            this.dx = dx;
            this.dy = dy;
            this.weight = weight;
            this.intWeight = (int) weight;
        }
        
        /**
         * Creates a new Offset using a 16-bits integer weight.
         * 
         * @param dx
         *            the offset along the X-axis
         * @param dy
         *            the offset along the Y-axis
         * @param weight
         *            the weight of the offset
         * @param intWeight
         *            the integer approximation of the weight of the offset
         */
        public Offset(int dx, int dy, double weight, int intWeight)
        {
            this.dx = dx;
            this.dy = dy;
            this.weight = weight;
            this.intWeight = intWeight;
        }
    }
}
