/**
 * 
 */
package net.sci.array.interp;

import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.ScalarArray2D;


/**
 * Evaluates 2D array. 
 * 
 * @author dlegland
 *
 */
public class LinearInterpolator2D implements ScalarFunction2D
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

	public LinearInterpolator2D(ScalarArray2D<?> image)
	{
		this.array = image;
	}
	
	public LinearInterpolator2D(ScalarArray2D<?> image, double padValue)
	{
		this.array = image;
		this.padValue = padValue;
	}
	

	// ===================================================================
	// implementation of the BivariateFunction interface

	/**
	 * Evaluates position within a 2D array. Returns stored pad value if evaluation is outside image bounds.
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
		double val11 = this.array.getValue(i, j) 	* (1-dx) * (1-dy);
		double val12 = this.array.getValue(i+1, j) 	* dx * (1-dy);
		double val21 = this.array.getValue(i, j+1) 	* (1-dx) * dy;
		double val22 = this.array.getValue(i+1, j+1) * dx * dy;
		
		// compute result values
		double val = val11 + val12 + val21 + val22;

		return val;
	}
	
	
	// ===================================================================
	// local main method for testing

	public static final void main(String[] args)
	{
		// Create a demo image
		Float32Array2D image = Float32Array2D.create(10, 10);
		for (int y = 2; y < 8; y++)
		{
			for (int x = 2; x < 8; x++)
			{
				image.setValue(100, x, y);
			}
		}
		
		// Create interpolator for input image
		LinearInterpolator2D interp = new LinearInterpolator2D(image);
		
		// allocate memory for output image
		Float32Array2D image2 = Float32Array2D.create(10, 10);
		
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
				image2.setValue(x, y, val);
			}
		}
		
		// Display result of interpolation
		System.out.println("Interpolated image:");
		for (int y = 0; y < 10; y++)
		{
			for (int x = 0; x < 10; x++)
			{
				System.out.print(String.format("%4.0f ", image2.getValue(x, y)));
			}
			System.out.println("");
		}

		
	}
}
