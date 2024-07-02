/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.Float64;
import net.sci.array.numeric.Numeric;
import net.sci.array.numeric.NumericArray;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Compute for each value of a numeric array the complement with respect to
 * another value.
 * 
 * If input value is <code>v</code> and complement value is <code>comp</code>,
 * the result will be given by <code>comp - v</code>.
 * 
 * This class can be used to invert an image, transform the result of a distance
 * map...
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
    
    public Complement(Numeric<?> complement)
    {
        this.complement = complement;
        if (complement instanceof Scalar)
        {
            this.complementValue = ((Scalar<?>) complement).getValue();
        }
    }
    
    public Complement(double value)
    {
        this.complementValue = value;
        this.complement = new Float64(value);
    }
    
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
