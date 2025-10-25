/**
 * 
 */
package net.sci.array.numeric.process;

import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Applies a scalar function to each element of a scalar array.
 * {@snippet lang="java" :
 * // create an initial array initialize with distance to center 
 * UInt8Array2D array = UInt8Array2D.create(20, 20);
 * array.fillInts((x,y)-> (int) (10 * Math.hypot(x - 10.0, y - 10.0)));
 * // create the operator 
 * ApplyFunction op = new ApplyFunction(v -> v > 100 ? 0 : Math.sqrt(10000 - v*v));
 * // apply function to array and get a new array
 * UInt8Array2D array = UInt8Array2D.wrap(UInt8Array.wrap(op.processScalar(array)));
 * // alternatively, can create a view to avoid memory allocation
 * UInt8Array2D view = UInt8Array2D.wrap(UInt8Array.wrap(op.createView(array)));
 * }
 * 
 * @see net.sci.array.numeric.ScalarArray#apply(java.util.function.UnaryOperator)
 */
public class ApplyFunction extends AlgoStub implements ArrayOperator, ScalarArrayOperator
{
    Function<Double, Double> function;
    
    /**
     * Creates a new operator based on a function that map double values to
     * double values.
     * 
     * @param function
     *            the function to apply to array data
     */
    public ApplyFunction(Function<Double, Double> function)
    {
        this.function = function;
    }
 
    /**
     * Creates a view that applies the inner function to the specified array.
     * 
     * @param <S>
     *            the type of data within the input array. View array has same
     *            type.
     * @param array
     *            the array to process
     * @return a view that applies the inner function to the specified array.
     */
    public <S extends Scalar<S>> ScalarArray<?> createView(ScalarArray<S> array)
    {
        return new ScalarView<S>(array);
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> res = array.newInstance(array.size());
        for (int[] pos : res.positions())
        {
            res.setValue(pos, function.apply(array.getValue(pos)));
        }
        return res;
    }
    
    /**
     * Creates a view on the result of function applied on the specified array,
     * using the same type as the array.
     * 
     * @param <S>
     *            the type of the array, and of the view
     */
    public class ScalarView<S extends Scalar<S>> extends ArrayWrapperStub<S> implements ScalarArray<S>
    {
        ScalarArray<S> array;
        
        protected ScalarView(ScalarArray<S> array)
        {
            super(array);
            this.array = array;
        }

        @Override
        public double getValue(int[] pos)
        {
            return function.apply(array.getValue(pos));
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            throw new RuntimeException("Can not modify value of a view");
        }

        @Override
        public Class<S> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public S get(int[] pos)
        {
            return array.createElement(function.apply(array.getValue(pos)));
        }

        @Override
        public void set(int[] pos, S value)
        {
            throw new RuntimeException("Can not modify value of a view");
        }

        /**
         * Returns false, as the view can not be modified.
         * 
         * @return false
         */
        public boolean isModifiable()
        {
            return false;
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
}
