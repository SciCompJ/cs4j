/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.Numeric;
import net.sci.array.numeric.NumericArray;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Computes the additive inverse, or opposite, of the numerical values in the
 * array.
 * 
 * @see Complement
 */
public class AdditiveInverse extends AlgoStub implements ArrayOperator
{
    /**
     * Applies specific processing for scalar arrays.
     * 
     * @param <S>
     *            the type of the scalar values
     * @param array
     *            the input array
     * @return a new array of scalars where each element is the opposite of the
     *         corresponding element in original array.
     */
    public <S extends Scalar<S>> ScalarArray<S> processScalar(ScalarArray<S> array)
    {
        ScalarArray<S> res = array.newInstance(array.size());
        res.fillValues((int[] pos) -> array.getValue(pos) * (-1));
        return res;
    }
    
    /**
     * Applies processing to an array of numeric values. Uses the
     * <code>opposite()</code> method of each element within the input array.
     * 
     * @param <N>
     *            the type of the numeric values
     * @param array
     *            the input array
     * @return a new array of scalars where each element is the opposite of the
     *         corresponding element in original array.
     */
    public <N extends Numeric<N>> NumericArray<N> processNumeric(NumericArray<N> array)
    {
        NumericArray<N> res = array.newInstance(array.size());
        res.fill((int[] pos) -> array.get(pos).opposite());
        return res;
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        // switch processing depending on data type
        if (Scalar.class.isAssignableFrom(array.dataType()))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> numArray = ScalarArray.wrap((Array<Scalar>) array);
            return processScalar(numArray);
        }
        else if (Numeric.class.isAssignableFrom(array.dataType()))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            NumericArray<?> numArray = NumericArray.wrap((Array<Numeric>) array);
            return processNumeric(numArray);
        }
        
        throw new IllegalArgumentException("Array must contain instances of Numeric");
    }

}
