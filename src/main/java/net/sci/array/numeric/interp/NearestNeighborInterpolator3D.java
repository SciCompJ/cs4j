/**
 * 
 */
package net.sci.array.numeric.interp;

import net.sci.array.numeric.ScalarArray3D;


/**
 * Evaluates values within a 3D scalar array using nearest-neighbor
 * interpolation.
 * 
 * This implementation allows to specify the value that will be returned when
 * evaluating outside of array bounds.
 * 
 * Example:<pre>{@code
    // Create a sample array with a single value at position (5,5)
    Float32Array3D array = Float32Array3D.create(10, 10, 10);
    array.setValue(5, 5, 5, 100.0);
    // Create interpolator for input array
    NearestNeighborInterpolator3D interp = new NearestNeighborInterpolator3D(array);
    // evaluate value close to the defined value
    double value = interp.evaluate(4.6, 4.6, 4.6);
    // should obtain 100.0, the value of the nearest neighbor
 * }</pre>
 * 
 * @see NearestNeighborInterpolator2D
 * @see LinearInterpolator3D
 * 
 * @author dlegland
 *
 */
public class NearestNeighborInterpolator3D implements ScalarFunction3D
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
     * Creates a new nearest-neighbor interpolator for a 3D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     */
    public NearestNeighborInterpolator3D(ScalarArray3D<?> array)
    {
        this.array = array;
    }
    
    /**
     * Creates a new nearest-neighbor interpolator for a 3D scalar array.
     * 
     * @param array
     *            the array containing values to interpolate.
     * @param padValue
     *            the value returned when interpolating outside of array bounds.
     *            Default value is 0.0.
     */
    public NearestNeighborInterpolator3D(ScalarArray3D<?> array, double padValue)
    {
        this.array = array;
        this.padValue = padValue;
    }
    
    
    // ===================================================================
    // implementation of the ScalarFunction3D interface
    
    /**
     * Evaluates value within the 3D scalar array. Returns pad value if position
     * is outside array bound.
     * 
     * @param x
     *            the x-coordinate of the position to evaluate
     * @param y
     *            the y-coordinate of the position to evaluate
     * @param z
     *            the z-coordinate of the position to evaluate
     * @return the value evaluated at the (x,y,z) position
     */
    public double evaluate(double x, double y, double z)
    {
        // compute indices
        int i = (int) Math.round(x);
        int j = (int) Math.round(y);
        int k = (int) Math.round(z);
        
        // check if point is located within interpolation area
        if (!array.containsPosition(i, j, k)) return this.padValue;
        
        // Returns the state of the closest image point
        return this.array.getValue(i, j, k);
    }
    
}
