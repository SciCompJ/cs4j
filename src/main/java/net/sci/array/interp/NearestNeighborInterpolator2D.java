/**
 * 
 */
package net.sci.array.interp;

import net.sci.array.interp.NearestNeighborInterpolator2D;
import net.sci.array.interp.ScalarFunction2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.ScalarArray2D;


/**
 * Evaluates 2D array. 
 * 
 * @author dlegland
 *
 */
public class NearestNeighborInterpolator2D implements ScalarFunction2D
{
	// ===================================================================
	// class variables

	ScalarArray2D<?> array;
	
	/**
	 * The state returned when sampling point is outside image bounds.
	 */
	double padValue = 0;
	
	
	// ===================================================================
	// constructors

	public NearestNeighborInterpolator2D(ScalarArray2D<?> array)
	{
		this.array = array;
	}
	
	public NearestNeighborInterpolator2D(ScalarArray2D<?> array, double padValue)
	{
		this.array = array;
		this.padValue = padValue;
	}

	
	// ===================================================================
	// implementation of the BivariateFunction interface

	/**
	 * Evaluates 2D image. returns NaN if evaluation is outside image bounds.
	 */
	public double evaluate(double x, double y)
	{
		// compute indices
		int i = (int) Math.round(x);
		int j = (int) Math.round(y);
		
		// check if point is located within interpolation area
		int[] dims = this.array.getSize();
		boolean isInside = i >= 0 && j >= 0 && i < dims[0] && j < dims[1];
		if (!isInside)
		{
			return this.padValue;
		}
		
		// Returns the state of the closest image point
		double val = this.array.getValue(i, j);

		return val;
	}
	

	// ===================================================================
	// local main method for testing

	public static final void main(String[] args)
	{
		// Create a demo image
		Float32Array2D array = Float32Array2D.create(10, 10);
		for (int y = 2; y < 8; y++)
		{
			for (int x = 2; x < 8; x++)
			{
				array.setValue(x, y, 100);
			}
		}
		
		// Create interpolator for input array
		NearestNeighborInterpolator2D interp = new NearestNeighborInterpolator2D(array);
		
		// allocate memory for output array
		Float32Array2D array2 = Float32Array2D.create(10, 10);
		
		// compute transform parameters
		double angle = Math.toRadians(20);
		double cosTheta = Math.cos(angle);
		double sinTheta = Math.sin(angle);
		
		// compute interpolated transformed image
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				double xc = x - 4.5;
				double yc = y - 4.5;
				double x2 = xc * cosTheta + yc * sinTheta + 4.5;
				double y2 = -xc * sinTheta + yc * cosTheta + 4.5;
				
				double val = interp.evaluate(x2, y2); 
				array2.setValue(x, y, val);
			}
		}
		
		// Display result of interpolation
		System.out.println("Interpolated array:");
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				System.out.print(String.format("%4.0f ", array2.getValue(x, y)));
			}
			System.out.println("");
		}
	}
}
