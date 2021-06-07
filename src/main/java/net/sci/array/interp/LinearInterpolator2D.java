/**
 * 
 */
package net.sci.array.interp;

import net.sci.array.scalar.ScalarArray2D;


/**
 * Evaluates values within a 2D scalar array using bi-linear interpolation.
 * 
 * This implementation allows to specify the value that will be returned when
 * evaluating outside of array bounds.
 * 
 * Example:<pre>{@code
    // Create a sample array with a single value at position (5,5)
    Float32Array2D array = Float32Array2D.create(10, 10);
    array.setValue(5, 5, 100.0);
    // Create interpolator for input array
    LinearInterpolator2D interp = new LinearInterpolator2D(array);
    // evaluate value close to the defined value
    double value = interp.evaluate(4.6, 4.6);
    // should obtain 36.0 (equal to 100 * 0.60 * 0.60)
 * }</pre>
 * 
 * @see NearestNeighborInterpolator2D
 * @see LinearInterpolator3D
 * 
 * @author dlegland
 *
 */
public class LinearInterpolator2D implements ScalarFunction2D
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
     * Creates a new linear interpolator for a 2D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     */
	public LinearInterpolator2D(ScalarArray2D<?> array)
	{
		this.array = array;
	}
	
    /**
     * Creates a new linear interpolator for a 2D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     * @param padValue
     *            the value returned when interpolating outside of array bounds.
     *            Default value is 0.0.
     */
	public LinearInterpolator2D(ScalarArray2D<?> array, double padValue)
	{
		this.array = array;
		this.padValue = padValue;
	}
	

	// ===================================================================
	// implementation of the BivariateFunction interface

    /**
     * Evaluates value within a 2D array. Returns stored pad value if
     * evaluation is outside image bounds.
     * 
     * @param x
     *            the x-coordinate of the position to evaluate
     * @param y
     *            the y-coordinate of the position to evaluate
     * @return the value evaluated at the (x,y) position
     */
	public double evaluate(double x, double y)
	{
		// select points located inside interpolation area
		// (smaller than image size)
		int[] dims = this.array.size();
		boolean isInside = x >= 0 && y >= 0 && x < (dims[0]-1) && y < (dims[1]-1);
		if (!isInside)
		{
			return this.padValue;
		}
		
		// compute indices
		int i = (int) Math.floor(x);
		int j = (int) Math.floor(y);
		
		// compute distances to lower-left corner of pixel
		double dx = (x - i);
		double dy = (y - j);
		
		// values of the 4 pixels around each current point
		double val11 = this.array.getValue(i,   j) 	 * (1-dx) * (1-dy);
		double val12 = this.array.getValue(i+1, j) 	 *    dx  * (1-dy);
		double val21 = this.array.getValue(i,   j+1) * (1-dx) *    dy;
		double val22 = this.array.getValue(i+1, j+1) *    dx  *    dy;
		
		// compute result values
		double val = val11 + val12 + val21 + val22;

		return val;
	}
}
