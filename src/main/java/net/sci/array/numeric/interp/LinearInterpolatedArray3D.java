/**
 * 
 */
package net.sci.array.numeric.interp;

import net.sci.array.numeric.ScalarArray3D;


/**
 * Evaluates values within a 3D scalar array using tri-linear interpolation.
 * 
 * This implementation allows to specify the value that will be returned when
 * evaluating outside of array bounds.
 * 
 * Example:<pre>{@code
    // Create a sample array with a single value at position (5,5)
    Float32Array3D array = Float32Array3D.create(10, 10, 10);
    array.setValue(5, 5, 5, 100.0);
    // Create interpolator for input array
    LinearInterpolator3D interp = new LinearInterpolator3D(array);
    // evaluate value close to the defined value
    double value = interp.evaluate(4.6, 4.6, 4.6);
    // should obtain 21.60 (equal to 100 * 0.60 * 0.60 * 0.60)
    assertEquals(21.60, value, 0.01);
 * }</pre>
 * 
 * @see NearestNeighborInterpolatedArray3D
 * @see LinearInterpolatedArray2D
 * 
 * @author dlegland
 *
 */
public class LinearInterpolatedArray3D implements ScalarFunction3D
{
    // ===================================================================
    // class variables
    
    /**
     * The array containing values to interpolate.
     */
    ScalarArray3D<?> array;
    
    /**
     * The state returned when sampling point is outside image bounds.
     */
    double padValue = 0;
    
    
    // ===================================================================
    // constructors
    
    /**
     * Creates a new linear interpolator for a 3D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     */
    public LinearInterpolatedArray3D(ScalarArray3D<?> array)
    {
        this.array = array;
    }
    
    /**
     * Creates a new linear interpolator for a 3D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     * @param padValue
     *            the value returned when interpolating outside of array bounds.
     *            Default value is 0.0.
     */
    public LinearInterpolatedArray3D(ScalarArray3D<?> array, double padValue)
    {
        this.array = array;
        this.padValue = padValue;
    }
    
    
    // ===================================================================
    // implementation of the ScalarFunction3D interface
    
    /**
     * Evaluates value within a 3D array. Returns stored pad value if evaluation
     * is outside image bounds.
     * 
     * @param x
     *            the x-coordinate of the position to evaluate
     * @param y
     *            the y-coordinate of the position to evaluate
     * @param z
     *            the z-coordinate of the position to evaluate
     * @return the value evaluated at the (x,y) position
     */
    public double evaluate(double x, double y, double z)
    {
        // compute indices
        int i = (int) Math.floor(x);
        int j = (int) Math.floor(y);
        int k = (int) Math.floor(z);
        
        // check if point is located within interpolation area
        // (smaller than image size)
        int[] dims = this.array.size();
        if (i < 0 || i >= (dims[0] - 1)) return this.padValue;
        if (j < 0 || j >= (dims[1] - 1)) return this.padValue;
        if (k < 0 || k >= (dims[2] - 1)) return this.padValue;
        
        // compute distances to lower-left corner of pixel
        double dx = (x - i);
        double dy = (y - j);
        double dz = (z - k);
        
        // values of the 8 voxels around each current point
        // (use val index in Z-Y-X order)
        double val111 = this.array.getValue(i, j, k) * (1 - dx) * (1 - dy) * (1 - dz);
        double val112 = this.array.getValue(i + 1, j, k) * dx * (1 - dy) * (1 - dz);
        double val121 = this.array.getValue(i, j + 1, k) * (1 - dx) * dy * (1 - dz);
        double val122 = this.array.getValue(i + 1, j + 1, k) * dx * dy * (1 - dz);
        double val211 = this.array.getValue(i, j, k + 1) * (1 - dx) * (1 - dy) * dz;
        double val212 = this.array.getValue(i + 1, j, k + 1) * dx * (1 - dy) * dz;
        double val221 = this.array.getValue(i, j + 1, k + 1) * (1 - dx) * dy * dz;
        double val222 = this.array.getValue(i + 1, j + 1, k + 1) * dx * dy * dz;
        
        // sum up neighbor values
        double val = val111 + val112 + val121 + val122 + val211 + val212 + val221 + val222;
        return val;
    }
    
}
