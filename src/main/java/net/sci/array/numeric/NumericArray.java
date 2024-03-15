/**
 * 
 */
package net.sci.array.numeric;

import net.sci.algo.AlgoListener;
import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;

/**
 * Specialization of Array interface that supports elementary arithmetic
 * operations (plus, minus...).
 * 
 * Main sub-interfaces are ScalarArray and VectorArray.
 * 
 * @author dlegland
 */
public interface NumericArray<N extends Numeric<N>> extends Array<N>
{
    // =============================================================
    // static methods

    public static <N extends Numeric<N>> NumericArray<N> wrap(Array<N> array)
    {
        if (array instanceof  NumericArray) return (NumericArray<N>) array;
        
        return new Wrapper<N>(array);
    }
    
    
    // =============================================================
    // Default methods for arithmetic on arrays
    
    /**
     * Adds a numeric value to each element of this numeric array, and returns
     * the result array.
     * 
     * @param v
     *            the value to add
     * @return a new array with the value added
     */
    public default NumericArray<N> plus(N v)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).plus(v));
        return res;
    }

    /**
     * Subtracts a numeric value from each element of this numeric array, and
     * returns the result array.
     * 
     * @param v
     *            the value to subtract
     * @return a new array with the value subtracted
     */
    public default NumericArray<N> minus(N v)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).minus(v));
        return res;        
    }

    /**
     * Applies a multiplication with a scalar value to each element of this
     * numeric array, and returns the result array.
     * 
     * @param k
     *            the factor to multiply by
     * @return a new array with the elements multiplied
     */
    public default NumericArray<N> times(double k)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).times(k));
        return res;        
    }
    
    /**
     * Applies a division by a scalar value to each element of this numeric
     * array, and returns the result array.
     * 
     * @param k
     *            the factor to divide by
     * @return a new array with the elements divided
     */
    public default NumericArray<N> divideBy(double k)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).divideBy(k));
        return res;        
    }
    
    
    // =============================================================
    // Specialization of the Array interface

    @Override
    public NumericArray<N> newInstance(int... dims);

    @Override
    public default NumericArray<N> duplicate()
    {
        NumericArray<N> res = newInstance(this.size());
        res.fill((int[] pos) -> this.get(pos));
        return res;
    }

    public static class Wrapper<N extends Numeric<N>> extends ArrayWrapperStub<N> implements NumericArray<N>
    {
        Array<N> array;
        protected Wrapper(Array<N> array)
        {
            super(array);
            this.array = array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<N> elementClass()
        {
            return (Class<N>) array.sampleElement().getClass();
        }

        @Override
        public Factory<N> factory()
        {
            return new Array.Factory<N>()
            {
                @Override
                public void addAlgoListener(AlgoListener listener)
                {
                }

                @Override
                public void removeAlgoListener(AlgoListener listener)
                {
                }

                @Override
                public Array<N> create(int[] dims, N value)
                {
                    return new Wrapper<N>(array.factory().create(dims, value));
                }
            };
        }

        @Override
        public N get(int[] pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, N value)
        {
            array.set(pos, value);
        }

        @Override
        public NumericArray<N> newInstance(int... dims)
        {
            return new Wrapper<N>(array.factory().create(dims, array.sampleElement()));
        }
    }
}
