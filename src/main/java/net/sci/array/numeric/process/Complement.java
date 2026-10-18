/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.Float64;
import net.sci.array.numeric.Numeric;
import net.sci.array.numeric.NumericArray;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Computes for each value of a numeric array the complement with respect to
 * another value.
 * 
 * If input value is {@code v} and complement value is {@code comp}, the result
 * will be given by {@code comp-v}.
 * 
 * This class can be used to invert an image, transform the result of a distance
 * map...
 * 
 * @see AdditiveInverse
 */
public class Complement extends AlgoStub implements ArrayOperator
{
    /**
     * The complement value.
     */
    Numeric<?> complement;
    
    /**
     * For scalars.
     */
    double complementValue = Double.NaN;
    
    /**
     * Creates an operator that computes the complement to the specified numeric
     * element.
     * 
     * @param complement
     *            the element to compute the complement to.
     */
    public Complement(Numeric<?> complement)
    {
        this.complement = complement;
        if (complement instanceof Scalar)
        {
            this.complementValue = ((Scalar<?>) complement).value();
        }
    }
    
    /**
     * Creates an operator that computes the complement to the specified value.
     * 
     * @param value
     *            the value to complement to.
     */
    public Complement(double value)
    {
        this.complementValue = value;
        this.complement = new Float64(value);
    }
    
    /**
     * Processes a scalar array, and returns a scalar array.
     * 
     * @param array
     *            the array to process
     * @return the resulting array of scalar values
     */
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        // check compatibility with inner complement base
        if (Double.isNaN(complementValue))
        {
            throw new RuntimeException("Complement value must be an instance of scalar");
        }
        
        ScalarArray<?> res = array.newInstance(array.size());
        for (int[] pos : res.positions())
        {
            res.setValue(pos, complementValue - array.getValue(pos));
        }
        
        return res;
    }

    /**
     * Processes an array of numeric elements, and returns an array containing
     * numeric elements.
     * 
     * @param <N>
     *            the type of numeric within the array
     * @param array
     *            the array to process
     * @return the resulting array of numeric elements
     */
    @SuppressWarnings("unchecked")
    public <N extends Numeric<N>> NumericArray<N> processNumeric(NumericArray<N> array)
    {
        // check compatibility with inner complement class
        if (!array.elementClass().isAssignableFrom(complement.getClass()))
        {
            throw new RuntimeException("Requires a numeric array with class compatible to inner complement value: " + complement.getClass().getName());
        }
        
        NumericArray<N> res = array.newInstance(array.size());
        for (int[] pos : res.positions())
        {
            res.set(pos, ((N) complement).minus(array.get(pos)));
        }
        
        return res;
    }

    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        if (array instanceof NumericArray)
        {
            // check compatibility with inner complement class
            if (!array.elementClass().isAssignableFrom(complement.getClass()))
            {
                throw new RuntimeException("Requires a numeric array with class compatible to inner complement value: " + complement.getClass().getName());
            }
            return processNumeric((NumericArray<?>) array);
        }
        
        throw new RuntimeException("Requires a numeric array as input");
    }

    @Override
    public boolean canProcess(Array<?> array)
    {
        if (!(array instanceof NumericArray)) return false;
        return array.elementClass().isAssignableFrom(complement.getClass());
    }
}
