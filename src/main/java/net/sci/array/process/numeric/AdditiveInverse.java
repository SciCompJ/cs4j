/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.ArrayWrapperStub;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.Numeric;
import net.sci.array.numeric.NumericArray;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Computes the additive inverse, or opposite, of the numerical values in the
 * array.
 * 
 * @see Complement
 */
public class AdditiveInverse extends AlgoStub implements ArrayOperator
{
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <N extends Numeric<N>> Array<N> view(Array<N> array)
    {
        if (Int.class.isAssignableFrom(array.elementClass()))
        {
            return new IntView(IntArray.wrap((Array<? extends Int>) array));
        }
        
        if (Scalar.class.isAssignableFrom(array.elementClass()))
        {
            return new ScalarView(ScalarArray.wrap((Array<? extends Scalar>) array));
        }
        
        return new NumericView(NumericArray.wrap(array));
    }
    
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
        if (Scalar.class.isAssignableFrom(array.elementClass()))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray<?> numArray = ScalarArray.wrap((Array<Scalar>) array);
            return processScalar(numArray);
        }
        else if (Numeric.class.isAssignableFrom(array.elementClass()))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            NumericArray<?> numArray = NumericArray.wrap((Array<Numeric>) array);
            return processNumeric(numArray);
        }
        
        throw new IllegalArgumentException("Array must contain instances of Numeric");
    }
    
    public static class NumericView<N extends Numeric<N>> extends ArrayWrapperStub<N> implements NumericArray<N>
    {
        NumericArray<N> array;
        
        protected NumericView(NumericArray<N> array)
        {
            super(array);
            this.array = array;
        }

        @Override
        public Class<N> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public N get(int[] pos)
        {
            return array.get(pos).opposite();
        }

        @Override
        public void set(int[] pos, N value)
        {
            array.set(pos, value.opposite());
        }

        @Override
        public NumericArray<N> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public net.sci.array.numeric.NumericArray.Factory<N> factory()
        {
            return array.factory();
        }
    }
    
    public static class ScalarView<S extends Scalar<S>> extends ArrayWrapperStub<S> implements ScalarArray<S>
    {
        ScalarArray<S> array;
        
        protected ScalarView(ScalarArray<S> array)
        {
            super(array);
            this.array = array;
        }

        @Override
        public Class<S> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public S get(int[] pos)
        {
            return array.createElement(-array.getValue(pos));
        }

        @Override
        public void set(int[] pos, S value)
        {
            array.setValue(pos, -value.getValue());
        }

        @Override
        public double getValue(int[] pos)
        {
            return -array.getValue(pos);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.setValue(pos, -value);
        }

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public net.sci.array.numeric.ScalarArray.Factory<S> factory()
        {
            return array.factory();
        }
    }
    
    public static class IntView<I extends Int<I>> extends ArrayWrapperStub<Int32> implements Int32Array
    {
        IntArray<I> array;
        
        protected IntView(IntArray<I> array)
        {
            super(array);
            this.array = array;
        }

        @Override
        public int getInt(int[] pos)
        {
            return -array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            array.setInt(pos, -value);
        }

        @Override
        public Int32 get(int[] pos)
        {
            return new Int32(-array.getInt(pos));
        }

        @Override
        public void set(int[] pos, Int32 value)
        {
            int v = -value.getInt();
            array.set(pos, array.sampleElement().fromInt(v));
        }

        @Override
        public Class<Int32> elementClass()
        {
            return Int32.class;
        }

        @Override
        public Int32Array newInstance(int... dims)
        {
            return Int32Array.create(dims);
        }

        @Override
        public net.sci.array.numeric.Int32Array.Factory factory()
        {
            return Int32Array.defaultFactory;
        }
    }
}
