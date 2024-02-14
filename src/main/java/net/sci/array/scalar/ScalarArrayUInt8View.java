/**
 * 
 */
package net.sci.array.scalar;

/**
 * Conversion from a scalar array to an UInt8 array taking into account min and
 * max values for conversion.
 * 
 * @see UInt8Array.ScalarArrayWrapper
 * @see ScalarArrayThresholdView
 * 
 * @author dlegland
 *
 */
public class ScalarArrayUInt8View implements UInt8Array
{
    // =============================================================
    // Class members

    ScalarArray<?> array;
    
    double minValue;
    double diff;
    

    // =============================================================
    // Constructors

    /**
     * 
     * @param array
     *            the original scalar array to convert
     * @param minValue
     *            the value in original array that corresponds to 0 in the view
     * @param maxValue
     *            the value in original array that corresponds to 255 in the
     *            view
     */
    public ScalarArrayUInt8View(ScalarArray<?> array, double minValue, double maxValue)
    {
        this.array = array;
        this.minValue = minValue;
        this.diff = maxValue - minValue;
    }


    // =============================================================
    // Specialization of the UInt8Array interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array#getByte(int[])
     */
    @Override
    public byte getByte(int[] pos)
    {
        double value = array.getValue(pos);
        value = 255.0 * (value - minValue) / diff;
        return (byte) UInt8.convert(value);
    }


    /* (non-Javadoc)
     * @see net.sci.array.scalar.UInt8Array#setByte(int[], byte)
     */
    @Override
    public void setByte(int[] pos, byte value)
    {
        throw new RuntimeException("Can not modify a type conversion view");
    }


    // =============================================================
    // Specialization of the Array interface

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
    public net.sci.array.Array.PositionIterator positionIterator()
    {
        return array.positionIterator();
    }
}
