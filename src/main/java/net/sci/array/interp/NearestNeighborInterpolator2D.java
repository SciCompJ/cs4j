/**
 * 
 */
package net.sci.array.interp;

import net.sci.array.numeric.ScalarArray2D;


/**
 * Evaluates values within a 2D scalar array using nearest-neighbor
 * interpolation.
 * 
 * This implementation allows to specify the value that will be returned when
 * evaluating outside of array bounds.
 * 
 * Example:<pre>{@code
    // Create a sample array with a single value at position (5,5)
    Float32Array2D array = Float32Array2D.create(10, 10);
    array.setValue(5, 5, 100.0);
    // Create interpolator for input array
    NearestNeighborInterpolator2D interp = new NearestNeighborInterpolator2D(array);
    // evaluate value close to the defined value
    double value = interp.evaluate(4.6, 4.6);
    // should obtain 100.0, the value of the nearest neighbor
 * }</pre>
 * 
 * @see NearestNeighborInterpolator3D
 * @see LinearInterpolator2D
 * 
 * @author dlegland
 *
 */
public class NearestNeighborInterpolator2D implements ScalarFunction2D
{
	// ===================================================================
	// class variables

    /**
     * The array containing values to interpolate.
     */
	ScalarArray2D<?> array;
	
	/**
	 * The state returned when sampling point is outside image bounds.
	 */
	double padValue = 0;
	
	
	// ===================================================================
	// constructors

    /**
     * Creates a new nearest-neighbor interpolator for a 2D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     */
	public NearestNeighborInterpolator2D(ScalarArray2D<?> array)
	{
		this.array = array;
	}
	
    /**
     * Creates a new nearest-neighbor interpolator for a 2D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     * @param padValue
     *            the value returned when interpolating outside of array bounds.
     *            Default value is 0.0.
     */
	public NearestNeighborInterpolator2D(ScalarArray2D<?> array, double padValue)
	{
		this.array = array;
		this.padValue = padValue;
	}

	
	// ===================================================================
	// implementation of the BivariateFunction interface

    /**
     * Evaluates value within the 2D scalar array. Returns pad value if position
     * is outside array bound.
     * 
     * @param x
     *            the x-coordinate of the position to evaluate
     * @param y
     *            the y-coordinate of the position to evaluate
     * @return the value evaluated at the (x,y) position
     */
	public double evaluate(double x, double y)
	{
		// compute indices
		int i = (int) Math.round(x);
		int j = (int) Math.round(y);
		
		// check if point is located within interpolation area
		int[] dims = this.array.size();
		boolean isInside = i >= 0 && j >= 0 && i < dims[0] && j < dims[1];
		if (!isInside)
		{
			return this.padValue;
		}
		
		// Returns the state of the closest image point
		double val = this.array.getValue(i, j);

		return val;
	}
}
