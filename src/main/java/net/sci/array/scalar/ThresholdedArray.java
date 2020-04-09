/**
 * 
 */
package net.sci.array.scalar;

/**
 * A binary array view representing the result of a threshold operation on a
 * scalar array.
 * 
 * Can not be modified.
 * 
 * Example of use:
 * <pre>{@code
 *   // create scalar array
 *   Float32Array2D array = Float32Array2D.create(20, 20);
 *   array.populateValues((pos) -> 10 - Math.hypot(pos[0] - 9.5, pos[1] - 9.5));
 *   System.out.println("Input array:");
 *   array.print(System.out);
 *   
 *   // create the threshold view
 *   ThresholdedArray view = new ThresholdedArray(array, 2.0);
 *   
 *   // use a 2D view to display content of the view
 *   BinaryArray2D view2d = BinaryArray2D.wrap(view);
 *   System.out.println("Binary view:");
 *   view2d.print(System.out);
 * }</pre>
 * 
 * @author dlegland
 */
public class ThresholdedArray implements BinaryArray
{
    // =============================================================
    // Class members

    /**
     * The reference array.
     */
    ScalarArray<?> array;
    
    /**
     * The value used to threshold the reference array.
     */
    double thresholdValue;

    
    // =============================================================
    // Constructors

    public ThresholdedArray(ScalarArray<?> array)
    {
        this(array, 0.0);
    }
    
    public ThresholdedArray(ScalarArray<?> array, double thresholdValue)
    {
        this.array = array;
        this.thresholdValue = thresholdValue;
    }
    
    
    // =============================================================
    // Implementation of the BinaryArray interface
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#getBoolean(int[])
     */
    @Override
    public boolean getBoolean(int... pos)
    {
        return array.getValue(pos) > this.thresholdValue;
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#setBoolean(boolean, int[])
     */
    @Override
    public void setBoolean(boolean state, int... pos)
    {
        throw new RuntimeException("Unauthorized operation: can not modify a type conversion view");
    }
    

    // =============================================================
    // Implementation of the Array interface

    /* (non-Javadoc)
     * @see net.sci.array.Array#dimensionality()
     */
    @Override
    public int dimensionality()
    {
        return array.dimensionality();
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.Array#size()
     */
    @Override
    public int[] size()
    {
        return array.size();
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.Array#size(int)
     */
    @Override
    public int size(int dim)
    {
        return array.size(dim);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.Array#positionIterator()
     */
    @Override
    public PositionIterator positionIterator()
    {
        return array.positionIterator();
    }
    
}
